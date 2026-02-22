# Wire Format Proposal

## Status

**Draft** — design exploration, not a specification.

## Problem

Bazaar compiles `.bzr` files into an IR that describes component trees, state bindings, modifiers, control flow, and expressions. Codegen backends (Go, Kotlin) will emit code that produces a **wire format** at runtime — the payload a server sends to a client in an SDUI system.

This format must be:

- **Invariant** — one universal schema, not per-component schemas. A `Row` and a `Button` use the same envelope; the `type` field distinguishes them.
- **Self-describing** — a client that has never seen a `Row` before can still parse the payload, walk its props, and render a fallback.
- **Expressive** — must encode state bindings, conditional/repeated rendering, event handlers, and modifier chains.
- **Streamable** — large trees should be deliverable progressively, inspired by React Server Components' [progressive JSON](https://overreacted.io/progressive-json/) model.

## Running Example

All examples in this document render the `TodoItemRow` template from a TODO app:

```bzr
enum Priority { low, medium, high }

data TodoItem {
    id int
    title string
    completed bool = false
    priority Priority = Priority.medium
    note string? = null
}

func ToggleTodo(id int)
func DeleteTodo(id int)

template TodoItemRow(todo TodoItem) {
    @Modifier(Padding(vertical = 4.0, horizontal = 0.0))
    Row(spacing = 8.0) {
        Checkbox(todo.completed) {
            ToggleTodo(todo.id)
        }

        if todo.completed {
            Text(todo.title, strikethrough = true)
        } else {
            Text(todo.title)
        }

        switch todo.priority {
        case Priority.high:
            Badge("!", color = "#FF3B30")
        case Priority.medium:
            Badge("~", color = "#FF9500")
        case Priority.low:
            Badge("-", color = "#34C759")
        default:
            Badge("?")
        }

        if var note = todo.note {
            @Modifier(Opacity(value = 0.6))
            Text(note ?? "", bold = false)
        } else if todo.completed {
            Icon("checkmark")
        }

        Button("Delete") {
            DeleteTodo(todo.id)
        }
    }
}
```

Called with `TodoItemRow(TodoItem(1, "Buy groceries", priority = Priority.high, note = "Milk, eggs, bread"))` — a single item with a high priority and a note.

---

## 1. Concepts to Encode

| Concept | Bazaar IR | Wire concern |
|---|---|---|
| Component instance | `IrComponentCall` | Type discriminator, props, children |
| Data object | `IrData` | Typed key-value map passed as prop values |
| Modifier | `IrModifierCall` | Metadata attached to a node |
| Enum value | `IrEnum` | String or integer encoding |
| State variable | `IrStateDecl` | Client-side mutable binding |
| Local variable | `IrLocalDecl` | Scoped binding within control flow |
| Expression | AST `Expr` | Evaluable by the client (see [section 5](#5-expression-encoding)) |
| Control flow | `IrForNode`, `IrForCondNode`, `IrIfNode`, `IrSwitchNode` | Conditional/repeated subtrees |
| State mutation | `IrAssignNode` | Client-side state update |
| Action | `IrFunctionCall` | Server-side function reference |

### IR Concepts with No Wire Encoding

Some IR types are compiler-internal and never appear in wire output:

- **`IrRawBody`** — placeholder before Pass 4 analysis. Replaced by specific `IrTemplateNode` types.
- **`IrReturnNode`** — return statements are errors in templates (caught by sema). No wire encoding.
- **`IrExprNode`** — catch-all expression statements. If they survive analysis, they should be represented as `$expr` nodes or `$action` calls depending on context.
- **`IrPreview`** — development-time rendering aid. Not included in production wire output.

### Constructors and Default Values

Bazaar components, data, and modifiers support custom constructors (e.g., `Padding(all double) = Padding(all, all, all, all)`). **Constructors are always expanded at render time** — the wire format only sees the fully-resolved field values, never the shorthand form. Similarly, **default prop values are always materialized** in the wire format. This makes clients simpler (no need for a component registry to resolve defaults) at the cost of slightly larger payloads.

## 2. JSON Format

### 2.1 Node Envelope

Every node in the tree uses a single envelope:

```json
{
    "$type": "Row",
    "$id": "n1",
    "props": {
        "spacing": 8.0
    },
    "modifier": { ... },
    "children": [ ... ]
}
```

| Field | Required | Description |
|---|---|---|
| `$type` | yes | Component type name. Matches a Bazaar `component` declaration. |
| `$id` | no | Stable identity for diffing and state preservation. |
| `props` | yes | Key-value map. Values are primitives, arrays, maps, data objects, state refs, or expressions. |
| `modifier` | no | Modifier chain applied to this node (always an array). |
| `children` | no | Ordered list of child nodes. |
| `actions` | no | Ordered list of event handler actions (see [section 2.6](#26-actions-and-event-handlers)). |

The `$` prefix on `$type` and `$id` prevents collisions with user-defined prop names.

**Type qualification.** If two packages define a `Button`, the `$type` uses a qualified name: `"layout.Button"`. The package prefix can be omitted when unambiguous within a single file's imports.

**`$id` generation.** IDs must be stable across re-renders for diffing and state preservation. A deterministic scheme based on template path works well: `"TodoItemRow/Row"` encodes the template name and component type. Inside `$for` loops, the iteration index (or a user-specified key) is incorporated to produce unique, stable IDs per iteration.

### 2.2 Prop Values

Primitives map directly:

```json
{
    "label": "Delete",
    "checked": false,
    "opacity": 1.0,
    "enabled": true,
    "onTap": null
}
```

Arrays and maps:

```json
{
    "items": [1, 2, 3],
    "headers": { "Content-Type": "application/json" }
}
```

Enum values are strings:

```json
{
    "alignment": "center"
}
```

Data objects carry a `$type` discriminator:

```json
{
    "todo": {
        "$type": "TodoItem",
        "id": 1,
        "title": "Buy groceries",
        "completed": false,
        "priority": "high",
        "note": "Milk, eggs, bread"
    }
}
```

Function-typed props (e.g., `onTap func()? = null`) are not serialized as values. Instead, the event handler body is encoded in the node's `actions` array (see [section 2.6](#26-actions-and-event-handlers)). A `null` function prop means no handler is attached.

### 2.3 State Bindings

State variables are declared at the template scope. A `$state` block at the root of a template's output declares initial values and gives each variable an ID:

```json
{
    "$state": {
        "count": { "initial": 0 }
    }
}
```

Props that read from state use a reference object:

```json
{
    "value": { "$ref": "count" }
}
```

Props that write to state (event handlers) use action expressions (see [section 5](#5-expression-encoding)).

### 2.4 Modifiers

A modifier is a typed object attached to a node, similar to a data object. Modifiers are always an array to support chaining uniformly (even when there's only one):

```json
{
    "$type": "Row",
    "modifier": [
        {
            "$type": "Padding",
            "top": 12.0,
            "leading": 12.0,
            "bottom": 12.0,
            "trailing": 12.0
        }
    ],
    "props": { "spacing": 8.0 },
    "children": [ ... ]
}
```

Multiple modifiers are applied in order:

```json
{
    "modifier": [
        { "$type": "Padding", "top": 12.0 },
        { "$type": "Background", "color": "#FF0000" }
    ]
}
```

### 2.5 Reference Types

The wire format uses several `$`-prefixed reference objects. Here is the complete set:

| Key | Meaning | Example |
|---|---|---|
| `$ref` | Read a variable (state or local binding) | `{ "$ref": "count" }` |
| `$expr` | Evaluable expression (tuple encoding) | `{ "$expr": ["+", { "$ref": "count" }, 1] }` |

`$ref` is sugar for `{ "$expr": ["ref", "count"] }`. Both resolve variables from the current scope — state variables, `$for` bindings, and `$let` bindings are all in scope.

Template parameters are not referenced at runtime. In server-evaluated mode, parameters are resolved before wire output. In client-evaluated mode, the template receives its data through the `$for`/`$let` bindings that destructure the parameter — the parameter itself does not appear in the wire format.

### 2.6 Actions and Event Handlers

In Bazaar source, trailing lambdas on components (e.g., `Checkbox(todo.completed) { ToggleTodo(todo.id) }`) represent event handlers. In the wire format, the handler body is encoded as an `actions` array on the node, separate from `children` (which are for component composition):

```json
{
    "$type": "Checkbox",
    "props": { "checked": false },
    "actions": [
        {
            "$type": "$action",
            "name": "ToggleTodo",
            "args": [1]
        }
    ]
}
```

**`$assign`** — client-side state mutation. The client evaluates this locally.

**`$action`** — server-side function call. The client sends an RPC to the server with the function name and resolved arguments. Open questions around actions (correlation IDs, error handling, batching, idempotency) are discussed in [section 7](#7-open-questions).

### 2.7 Control Flow

Control flow can be handled in two ways. This is an open design question.

**Option A: Server-evaluated.** The server runs loops and conditionals, emitting a flat tree of concrete nodes. The wire format contains no control flow — just the rendered result. This is simpler for clients but means the server must re-render on state changes.

**Option B: Client-evaluated.** The wire format includes control flow nodes that the client interprets. This allows the client to re-render locally when state changes, without a server round-trip.

Control flow nodes use `$`-prefixed type names to distinguish them from user components.

#### `$for` (for-in loop)

Maps to `IrForNode`. Iterates over a collection, binding each element to one or more names:

```json
{
    "$type": "$for",
    "bindings": ["todo"],
    "in": { "$expr": ["ref", "todos"] },
    "body": [ ... ]
}
```

`bindings` is an array to support destructuring (e.g., `for (key, value) in map`).

#### `$while` (conditional loop)

Maps to `IrForCondNode`. Repeats while a condition is true:

```json
{
    "$type": "$while",
    "condition": { "$expr": ["<", { "$ref": "i" }, 10] },
    "body": [ ... ]
}
```

#### `$if` (conditional)

Maps to `IrIfNode`. Supports two forms: boolean condition and optional binding.

Boolean condition:

```json
{
    "$type": "$if",
    "condition": { "$expr": ["!=", { "$ref": "count" }, 0] },
    "then": [ ... ],
    "elseIfs": [
        {
            "condition": { "$expr": [">", { "$ref": "count" }, 10] },
            "then": [ ... ]
        }
    ],
    "else": [ ... ]
}
```

Optional binding (`if var note = todo.note`):

```json
{
    "$type": "$if",
    "bind": { "note": { "$expr": ["get", "todo", "note"] } },
    "then": [ ... ]
}
```

When `bind` is used, the named variable (e.g., `note`) is in scope within `then`. The condition is implicitly "the bound value is non-null."

#### `$switch`

Maps to `IrSwitchNode`:

```json
{
    "$type": "$switch",
    "expr": { "$expr": ["get", "todo", "priority"] },
    "cases": [
        { "value": "high", "body": [ ... ] },
        { "value": "medium", "body": [ ... ] }
    ],
    "default": [ ... ]
}
```

#### `$let` (local variable)

Maps to `IrLocalDecl`. Introduces a scoped binding:

```json
{
    "$type": "$let",
    "name": "title",
    "value": { "$expr": ["get", "todo", "title"] },
    "body": [ ... ]
}
```

The variable is in scope only within `body`.

### 2.8 Full Example (Server-Evaluated, completed = true, note = null)

`TodoItemRow(TodoItem(2, "Write tests", completed = true))` — a completed item with no note, rendered server-side:

```json
{
    "children": [
        {
            "$type": "Row",
            "$id": "TodoItemRow/Row",
            "modifier": [
                {
                    "$type": "Padding",
                    "top": 4.0,
                    "leading": 0.0,
                    "bottom": 4.0,
                    "trailing": 0.0
                }
            ],
            "props": {
                "spacing": 8.0
            },
            "children": [
                {
                    "$type": "Checkbox",
                    "$id": "TodoItemRow/Row/Checkbox",
                    "props": { "checked": true },
                    "actions": [
                        {
                            "$type": "$action",
                            "name": "ToggleTodo",
                            "args": [2]
                        }
                    ]
                },
                {
                    "$type": "Text",
                    "$id": "TodoItemRow/Row/Text",
                    "props": {
                        "value": "Write tests",
                        "strikethrough": true
                    }
                },
                {
                    "$type": "Badge",
                    "$id": "TodoItemRow/Row/Badge",
                    "props": {
                        "label": "~",
                        "color": "#FF9500"
                    }
                },
                {
                    "$type": "Icon",
                    "$id": "TodoItemRow/Row/Icon",
                    "props": { "name": "checkmark" }
                },
                {
                    "$type": "Button",
                    "$id": "TodoItemRow/Row/Button",
                    "props": { "label": "Delete" },
                    "actions": [
                        {
                            "$type": "$action",
                            "name": "DeleteTodo",
                            "args": [2]
                        }
                    ]
                }
            ]
        }
    ]
}
```

The `if var note = todo.note` branch is pruned because `note` is `null`. The `else if todo.completed` branch is taken, emitting the `Icon("checkmark")` node. The `switch` resolved to `Priority.medium`, emitting the `Badge("~")` node. The `if todo.completed` branch emitted `Text` with `strikethrough = true`.

### 2.9 Full Example (Server-Evaluated, note = "Milk, eggs, bread")

Same template, but `TodoItem(1, "Buy groceries", priority = Priority.high, note = "Milk, eggs, bread")` — the optional binding is non-null:

```json
{
    "children": [
        {
            "$type": "Row",
            "$id": "TodoItemRow/Row",
            "modifier": [
                {
                    "$type": "Padding",
                    "top": 4.0,
                    "leading": 0.0,
                    "bottom": 4.0,
                    "trailing": 0.0
                }
            ],
            "props": {
                "spacing": 8.0
            },
            "children": [
                {
                    "$type": "Checkbox",
                    "$id": "TodoItemRow/Row/Checkbox",
                    "props": { "checked": false },
                    "actions": [
                        {
                            "$type": "$action",
                            "name": "ToggleTodo",
                            "args": [1]
                        }
                    ]
                },
                {
                    "$type": "Text",
                    "$id": "TodoItemRow/Row/Text",
                    "props": {
                        "value": "Buy groceries"
                    }
                },
                {
                    "$type": "Badge",
                    "$id": "TodoItemRow/Row/Badge",
                    "props": {
                        "label": "!",
                        "color": "#FF3B30"
                    }
                },
                {
                    "$type": "Text",
                    "$id": "TodoItemRow/Row/Text/note",
                    "modifier": [
                        {
                            "$type": "Opacity",
                            "value": 0.6
                        }
                    ],
                    "props": {
                        "value": "Milk, eggs, bread",
                        "bold": false
                    }
                },
                {
                    "$type": "Button",
                    "$id": "TodoItemRow/Row/Button",
                    "props": { "label": "Delete" },
                    "actions": [
                        {
                            "$type": "$action",
                            "name": "DeleteTodo",
                            "args": [1]
                        }
                    ]
                }
            ]
        }
    ]
}
```

Because the server evaluated the `if var note = todo.note` branch and `note` was `"Milk, eggs, bread"`, the note `Text` node is included with an `Opacity` modifier. The `switch` resolved to `Priority.high`, emitting `Badge("!", color = "#FF3B30")`. The item is not completed, so the `else` branch of `if todo.completed` emits `Text(todo.title)` without strikethrough.

### 2.10 Full Example (Client-Evaluated)

Same template, but control flow and data binding are preserved for client interpretation:

```json
{
    "children": [
        {
            "$type": "Row",
            "modifier": [
                {
                    "$type": "Padding",
                    "top": 4.0,
                    "leading": 0.0,
                    "bottom": 4.0,
                    "trailing": 0.0
                }
            ],
            "props": {
                "spacing": 8.0
            },
            "children": [
                {
                    "$type": "Checkbox",
                    "props": {
                        "checked": { "$expr": ["get", "todo", "completed"] }
                    },
                    "actions": [
                        {
                            "$type": "$action",
                            "name": "ToggleTodo",
                            "args": [{ "$expr": ["get", "todo", "id"] }]
                        }
                    ]
                },
                {
                    "$type": "$if",
                    "condition": { "$expr": ["get", "todo", "completed"] },
                    "then": [
                        {
                            "$type": "Text",
                            "props": {
                                "value": { "$expr": ["get", "todo", "title"] },
                                "strikethrough": true
                            }
                        }
                    ],
                    "else": [
                        {
                            "$type": "Text",
                            "props": {
                                "value": { "$expr": ["get", "todo", "title"] }
                            }
                        }
                    ]
                },
                {
                    "$type": "$switch",
                    "expr": { "$expr": ["get", "todo", "priority"] },
                    "cases": [
                        {
                            "value": "high",
                            "body": [
                                {
                                    "$type": "Badge",
                                    "props": { "label": "!", "color": "#FF3B30" }
                                }
                            ]
                        },
                        {
                            "value": "medium",
                            "body": [
                                {
                                    "$type": "Badge",
                                    "props": { "label": "~", "color": "#FF9500" }
                                }
                            ]
                        },
                        {
                            "value": "low",
                            "body": [
                                {
                                    "$type": "Badge",
                                    "props": { "label": "-", "color": "#34C759" }
                                }
                            ]
                        }
                    ],
                    "default": [
                        {
                            "$type": "Badge",
                            "props": { "label": "?" }
                        }
                    ]
                },
                {
                    "$type": "$if",
                    "bind": {
                        "note": {
                            "$expr": ["get", "todo", "note"]
                        }
                    },
                    "then": [
                        {
                            "$type": "Text",
                            "modifier": [
                                {
                                    "$type": "Opacity",
                                    "value": 0.6
                                }
                            ],
                            "props": {
                                "value": { "$expr": ["??", { "$ref": "note" }, ""] },
                                "bold": false
                            }
                        }
                    ],
                    "elseIfs": [
                        {
                            "condition": { "$expr": ["get", "todo", "completed"] },
                            "then": [
                                {
                                    "$type": "Icon",
                                    "props": { "name": "checkmark" }
                                }
                            ]
                        }
                    ]
                },
                {
                    "$type": "Button",
                    "props": { "label": "Delete" },
                    "actions": [
                        {
                            "$type": "$action",
                            "name": "DeleteTodo",
                            "args": [{ "$expr": ["get", "todo", "id"] }]
                        }
                    ]
                }
            ]
        }
    ]
}
```

## 3. Protobuf Format

A protobuf encoding mirrors the JSON structure but benefits from schema evolution, compact binary representation, and strong typing.

### 3.1 Core Messages

```protobuf
syntax = "proto3";
package bazaar.wire;

message BazaarTree {
    map<string, StateVar> state = 1;
    repeated TreeNode children = 2;
}

// TreeNode wraps all possible node types in a oneof.
// This allows component nodes, control flow, and actions
// to appear in the same children/body lists.
message TreeNode {
    oneof kind {
        ComponentNode component = 1;
        ForNode for_loop = 2;
        WhileNode while_loop = 3;
        IfNode if_cond = 4;
        SwitchNode switch_stmt = 5;
        LetNode let_binding = 6;
        AssignNode assign = 7;
        ActionNode action = 8;
        ExprNode expr = 9;
    }
}

message ComponentNode {
    string type = 1;
    optional string id = 2;
    repeated Prop props = 3;
    repeated Modifier modifier = 4;  // array, not linked list
    repeated TreeNode children = 5;
    repeated TreeNode actions = 6;
}

message Prop {
    string name = 1;
    Value value = 2;
}

message Value {
    oneof kind {
        int64 int_value = 1;
        double double_value = 2;
        string string_value = 3;
        bool bool_value = 4;
        NullValue null_value = 5;
        ArrayValue array_value = 6;
        MapValue map_value = 7;
        DataObject data_value = 8;
        VarRef ref = 9;
        Expr expr = 10;
    }
}

enum NullValue {
    NULL = 0;
}

message ArrayValue {
    repeated Value elements = 1;
}

message MapValue {
    repeated MapEntry entries = 1;
}

message MapEntry {
    Value key = 1;
    Value value = 2;
}

message DataObject {
    string type = 1;
    repeated Prop fields = 2;
}

message StateVar {
    Value initial = 1;
}

message VarRef {
    string name = 1;
}

message Modifier {
    string type = 1;
    repeated Prop fields = 2;
}
```

### 3.2 Expressions and Actions

```protobuf
message Expr {
    oneof kind {
        BinaryExpr binary = 1;
        UnaryExpr unary = 2;
        MemberAccess member = 3;
        Literal literal = 4;
        VarRef ref = 5;
        CallExpr call = 6;
    }
}

message BinaryExpr {
    string op = 1;
    Expr left = 2;
    Expr right = 3;
}

message UnaryExpr {
    string op = 1;
    Expr operand = 2;
}

message MemberAccess {
    Expr target = 1;
    string member = 2;
}

message Literal {
    Value value = 1;
}

message CallExpr {
    string name = 1;
    repeated Value args = 2;
}

message AssignNode {
    VarRef target = 1;
    string op = 2;
    Value value = 3;
}

message ActionNode {
    string name = 1;
    repeated Value args = 2;
}

message ExprNode {
    Expr expr = 1;
}
```

### 3.3 Control Flow (if client-evaluated)

```protobuf
message ForNode {
    repeated string bindings = 1;  // supports destructuring
    Expr iterable = 2;
    repeated TreeNode body = 3;
}

message WhileNode {
    Expr condition = 1;
    repeated TreeNode body = 2;
}

message IfNode {
    // Exactly one of condition or bind is set.
    optional Expr condition = 1;
    map<string, Expr> bind = 2;  // optional binding (if var x = ...)
    repeated TreeNode then = 3;
    repeated ElseIfBranch else_ifs = 4;
    repeated TreeNode else_body = 5;
}

message ElseIfBranch {
    optional Expr condition = 1;
    map<string, Expr> bind = 2;
    repeated TreeNode body = 3;
}

message SwitchNode {
    Expr expr = 1;
    repeated SwitchCase cases = 2;
    repeated TreeNode default_body = 3;
}

message SwitchCase {
    Value value = 1;
    repeated TreeNode body = 2;
}

message LetNode {
    string name = 1;
    Value value = 2;
    repeated TreeNode body = 3;
}
```

### 3.4 Tradeoffs

| | JSON | Protobuf |
|---|---|---|
| Human readable | Yes | No (binary) |
| Schema evolution | Fragile (no field numbers) | Built-in (field numbers) |
| Payload size | Larger (string keys, whitespace) | Compact |
| Streaming | Line-delimited or progressive JSON | Native length-prefixed framing |
| Client tooling | Universal | Requires codegen or reflection |
| Debugging | Inspect in browser/curl | Needs decoder |

Both formats can coexist. The schema is defined once (in `bazaar-schema`); codegen produces serializers for both.

## 4. Progressive Delivery

Inspired by RSC's [progressive JSON](https://overreacted.io/progressive-json/), large trees can be streamed breadth-first. A placeholder marks content that will arrive later:

```json
{
    "$state": { "count": { "initial": 0 } },
    "children": [{ "$pending": "0" }]
}
{"$chunk": "0", "value": { "$type": "Row", ... }}
```

Each line is a self-contained JSON object. The first line contains the skeleton with `$pending` placeholders. Subsequent lines fill them in, in any order. This allows fast-loading sections to display immediately while slow data (e.g., from a database) arrives later.

For protobuf, the equivalent is length-prefixed frames where each frame carries a chunk ID and payload.

### Tradeoffs

- **Pro:** Perceived performance improves — the client can render a skeleton immediately.
- **Pro:** Out-of-order delivery means one slow query doesn't block the entire response.
- **Con:** Client must manage partial trees and handle placeholder resolution.
- **Con:** Adds complexity to the format and the client renderer.
- **Open question:** Is this needed for SDUI? RSC uses it because server components can suspend on data fetching. Bazaar templates are compiled ahead of time — the server has all data at render time. Progressive delivery may only matter if templates compose across service boundaries.

## 5. Expression Encoding

Some prop values aren't static — they depend on state or loop variables. These require an expression encoding the client can evaluate.

### Option A: AST-as-JSON

Serialize the expression tree directly:

```json
{
    "$expr": {
        "op": "+",
        "left": { "$ref": "count" },
        "right": 1
    }
}
```

- **Pro:** Transparent, easy to debug, maps 1:1 to Bazaar AST.
- **Con:** Verbose for complex expressions. Deep nesting.

### Option B: S-expression / Tuple Encoding

Flatten to prefix notation:

```json
{ "$expr": ["+", { "$ref": "count" }, 1] }
{ "$expr": ["get", "todo", "title"] }
{ "$expr": ["call", "ToggleTodo", { "$expr": ["get", "todo", "id"] }] }
```

- **Pro:** Compact, easy to parse, no key overhead.
- **Con:** Positional semantics — less self-describing.

### Option C: Stack-Based Bytecode

Encode as a compact instruction sequence:

```json
{ "$expr": "LOAD count; PUSH 1; ADD" }
```

Or as a byte array in protobuf:

```protobuf
message BytecodeExpr {
    bytes instructions = 1;
}
```

- **Pro:** Most compact. Fast to interpret.
- **Con:** Requires a bytecode spec, assembler, and interpreter. Harder to debug. Overkill for simple member access.

### Option D: Hybrid

Use inline values for simple cases, expressions only when needed:

```json
{
    "label": "Delete",
    "checked": { "$expr": ["get", "todo", "completed"] },
    "count": { "$ref": "count" }
}
```

A `$ref` is sugar for the common case of reading a single state variable. `$expr` handles everything else.

### Recommendation

Start with **Option B (tuple encoding)** with `$ref` sugar for simple state reads. It balances compactness and readability. Bytecode can be introduced later as an optimization if expression evaluation becomes a bottleneck.

### Security

If clients evaluate `$expr` trees, the expression language becomes an attack surface. Mitigations:

- **Depth limit** — reject expressions deeper than N levels (e.g., 32).
- **No recursion** — expressions cannot define or call user functions. Only built-in operators and member access.
- **No side effects** — expressions are pure. Side effects (state mutation, server calls) go through `$assign` and `$action`, which have their own validation.
- **Sandboxed evaluation** — the client expression interpreter should have no access to host APIs, filesystem, or network.

## 6. State Bindings

### Declaration

State variables are declared in a `$state` block with their initial values:

```json
{
    "$state": {
        "count": { "initial": 0 },
        "name": { "initial": "untitled" }
    }
}
```

### Reading State

Any prop can reference state via `$ref`:

```json
{ "value": { "$ref": "count" } }
```

### Mutating State

State mutations appear in event handler children as `$assign` nodes:

```json
{
    "$type": "$assign",
    "target": { "$ref": "count" },
    "op": "+=",
    "value": 1
}
```

### Scoping

State is currently template-level (`@State` in the IR is always at the template root). If templates compose other templates, each template's `$state` block is independent — there is no shared state across template boundaries.

Within a template, `$for` bindings, `$if` optional bindings, and `$let` variables create nested scopes. `$ref` resolves from the innermost scope outward: a `$for` binding named `model` shadows any state variable named `model`.

### Open Questions

- **Derived state:** Should there be computed/derived values that auto-update? (e.g., `fullName` derived from `first` + `last`)
- **Server sync:** When state changes, does the client re-render locally (client-evaluated) or request a new tree from the server (server-evaluated)? This is the central architectural question.
- **Cross-template state:** If a parent template passes state down to a child template, how is that expressed? Props? Context?

## 7. Open Questions

### Server-Evaluated vs Client-Evaluated

| | Server-evaluated | Client-evaluated |
|---|---|---|
| Wire format | Flat tree, no control flow | Tree with `$for`, `$if`, `$expr` |
| State changes | Server round-trip | Local re-render |
| Client complexity | Minimal (just render nodes) | Must interpret expressions + control flow |
| Latency | Higher (network per interaction) | Lower (local) |
| Consistency | Server is source of truth | Risk of client/server divergence |
| Offline | Not possible | Possible for state-only changes |

A hybrid is likely: server-evaluated for initial render, client-evaluated for state-driven interactions (counters, toggles, form inputs), server round-trip for actions (`ToggleTodo`, `DeleteTodo`).

### Versioning

How does the format evolve?

- **JSON:** Use `$version` field. Unknown keys are ignored.
- **Protobuf:** Field numbers provide natural evolution. New fields are additive.
- **Component types:** The client needs a registry mapping `$type` to renderers. Unknown types need a fallback strategy (blank, error boundary, server-rendered image).

### Streaming

Is progressive delivery needed? See [section 4](#4-progressive-delivery). If Bazaar templates are always rendered from complete data, streaming may not be a priority. But it becomes important if templates compose data from multiple services.

### Actions and Server Functions

How are server actions invoked?

- RSC model: action references are opaque IDs. The client sends them back with arguments.
- Bazaar equivalent: `{ "$action": "ToggleTodo", "args": [1] }` triggers a server RPC.
- **Correlation:** Each action invocation needs a request ID so the client can match responses.
- **Error handling:** What does the client do when an action fails? Show an error boundary? Retry? Roll back optimistic state changes?
- **Batching:** A single event handler can contain multiple `$assign` and `$action` nodes. Should they execute atomically or independently?
- **Ordering:** Are actions in the `actions` array executed sequentially or can they be parallelized?
- **Idempotency:** If the network drops a response, can the client safely retry?
- **Authentication:** Action names are visible in the wire format. The server must validate that the caller is authorized to invoke each action.

### Children vs Slots

In Bazaar, `children [component]` is a typed slot. In the wire format, `children` is an array of nodes and `actions` is an array of event handler actions (see [section 2.6](#26-actions-and-event-handlers)). This separation resolves the ambiguity between component composition and event handlers.

Open question: Should there be **named slots** (e.g., `header`, `footer`) in addition to the default `children` array? This would require a language-level feature (named children declarations) that doesn't exist yet.

## Appendix: Reserved Keys

All `$`-prefixed keys used in the wire format:

| Key | Context | Meaning |
|---|---|---|
| `$type` | Node envelope, data objects, modifiers | Type discriminator |
| `$id` | Node envelope | Stable identity for diffing |
| `$state` | Tree root | State variable declarations |
| `$ref` | Prop value | Read a variable (state, for-binding, let-binding) |
| `$expr` | Prop value | Evaluable expression (tuple-encoded) |
| `$for` | Node `$type` | For-in loop (client-evaluated) |
| `$while` | Node `$type` | Conditional loop (client-evaluated) |
| `$if` | Node `$type` | Conditional (client-evaluated) |
| `$switch` | Node `$type` | Switch/case (client-evaluated) |
| `$let` | Node `$type` | Local variable binding (client-evaluated) |
| `$assign` | Node `$type` | Client-side state mutation |
| `$action` | Node `$type` | Server-side function call |
| `$pending` | Streaming placeholder | Content arriving later |
| `$chunk` | Streaming frame | Fills a `$pending` placeholder |
| `$version` | Tree root | Format version (for evolution) |

## References

- [JSX Over The Wire](https://overreacted.io/jsx-over-the-wire/) — serializing component trees as JSON, client vs server component boundaries
- [Progressive JSON](https://overreacted.io/progressive-json/) — breadth-first streaming with placeholder references
- [Introducing RSC Explorer](https://overreacted.io/introducing-rsc-explorer/) — RSC wire protocol internals, streaming, suspense holes
- [Support All of Structured Clone in RSC Serialization](https://github.com/facebook/react/issues/25687) — RSC serialization capabilities and type support
