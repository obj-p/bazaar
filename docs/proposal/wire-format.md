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
            Text(note, bold = false)
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
                                "value": { "$ref": "note" },
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

### Reducers vs Control Flow Nodes

Client-evaluated mode encodes `$switch`, `$if`, and `$for` nodes directly in the UI tree. The client must interpret these — matching cases, evaluating conditions, iterating collections — which pushes business logic into the rendering layer.

An alternative is a **reducer** (analogous to React's `useReducer`): a pure function that computes derived state from current state. The UI tree then uses simple state bindings instead of control flow nodes.

**Example: priority badge.** Currently, a `switch` on `todo.priority` produces a `$switch` node with four case branches, each containing a different `Badge`. With a reducer, the priority-to-badge mapping is computed upfront and the tree contains a single `Badge` with state bindings:

```jsonc
// Control flow approach — client interprets $switch
{
    "$type": "$switch",
    "expr": { "$expr": ["get", "todo", "priority"] },
    "cases": [
        { "value": "high", "body": [{ "$type": "Badge", "props": { "label": "!", "color": "#FF3B30" } }] },
        { "value": "medium", "body": [{ "$type": "Badge", "props": { "label": "~", "color": "#FF9500" } }] }
    ]
}

// Reducer approach — client just binds
{
    "$type": "Badge",
    "props": {
        "label": { "$ref": "todo.priorityLabel" },
        "color": { "$ref": "todo.priorityColor" }
    }
}
```

**Example: filtered list.** Instead of a `$for` with a nested `$if` that the client evaluates per item, the reducer produces an already-filtered list and the UI iterates without conditions.

**Tradeoffs:**

| | Control flow nodes | Reducer |
|---|---|---|
| Client complexity | High — must interpret `$switch`, `$if`, `$for` | Low — just bind and render |
| Wire payload | Larger — contains all branches | Smaller — only the active state |
| Separation of concerns | Logic mixed into UI tree | Logic isolated in reducer |
| Language impact | None (current design) | Requires `@Reducer` or equivalent |
| Server vs client | Can run either side | Reducer runs where state lives |

A reducer doesn't eliminate all control flow from the tree — `$if` for conditional rendering (e.g., show/hide a section) and `$for` for list iteration are inherently view concerns. But `$switch` on data to select prop values is state derivation, not rendering, and could be lifted into a reducer.

This is also related to the **derived state** question in [section 6](#6-state-bindings): a reducer generalizes derived state by bundling multiple derivations into a single state transition function.

### Pagination

Lists in SDUI can be large — a TODO app might have hundreds of items, a feed thousands. The wire format needs a strategy for delivering lists incrementally without sending the entire dataset upfront.

#### Option A: Server-Controlled Pagination

The server sends a page of items and a continuation token. The client requests more when needed (e.g., user scrolls to the bottom):

```json
{
    "$type": "$for",
    "bindings": ["todo"],
    "in": { "$ref": "todos" },
    "body": [ ... ],
    "$page": {
        "cursor": "eyJpZCI6NDJ9",
        "hasNext": true
    }
}
```

The client sends the cursor back via an action to fetch the next page:

```json
{
    "$type": "$action",
    "name": "$loadMore",
    "args": [{ "cursor": "eyJpZCI6NDJ9" }]
}
```

The server responds with a **patch** — additional children to append — rather than re-sending the entire tree. This requires a tree-patching protocol on top of the current replace-the-whole-tree model.

#### Option B: Client-Side Virtual Pagination

The server sends all items in a single payload but the client only renders a visible window. This is the "virtualized list" pattern (e.g., `react-window`). No wire format changes needed — it's purely a client rendering optimization.

This works for moderate datasets (hundreds of items) but breaks down for very large ones where the payload itself is too large.

#### Option C: Progressive List Delivery

Combine pagination with [progressive delivery](#4-progressive-delivery). The server sends an initial page inline and a `$pending` placeholder for the rest:

```json
{
    "$type": "$for",
    "bindings": ["todo"],
    "in": { "$ref": "todos" },
    "body": [ ... ]
}
```

Where `todos` is delivered progressively:

```json
{ "$state": { "todos": { "initial": [{ "$pending": "page-0" }] } } }
{ "$chunk": "page-0", "value": [ ... first 20 items ... ], "next": { "$pending": "page-1" } }
{ "$chunk": "page-1", "value": [ ... next 20 items ... ] }
```

This streams pages as they become available without client-initiated requests, similar to RSC streaming.

#### Key Questions

- **Trigger mechanism:** How does the client signal it needs more data? Scroll position? Explicit "load more" button? Automatic prefetch?
- **Tree patching:** If the server sends a page of new items, how are they merged into the existing tree? Append to `children`? Replace a `$pending` slot? This implies a **diff/patch protocol** that doesn't exist yet.
- **Interaction with filtering:** If the client changes a filter (`@State var filter`), does that invalidate the pagination cursor? In server-controlled pagination, the server must be aware of the active filter. In client-side virtual pagination, the client re-filters the full dataset locally.
- **Interaction with state:** If an item on page 1 is deleted while the user is viewing page 3, how is the list reindexed? Cursor-based pagination (keyed by item ID) is more resilient than offset-based.
- **Stable IDs:** `$id` generation inside paginated `$for` loops must remain stable across page loads. Using the item's natural key (e.g., `todo.id`) rather than array index is essential.
- **Protobuf:** Length-prefixed framing naturally supports streaming pages. Each frame could carry a page of items with a continuation marker.

#### Tradeoffs

| | Server-controlled | Client virtual | Progressive |
|---|---|---|---|
| Initial payload | Small (one page) | Large (all items) | Small, then streamed |
| Server round-trips | Per page | One | One (streamed) |
| Client complexity | Must request pages + patch tree | Virtualized rendering | Must resolve `$pending` slots |
| Filtering | Server re-paginates | Client filters locally | Server re-streams |
| Offline | Only cached pages | Full dataset available | Partial until stream completes |

Server-controlled pagination is the most practical starting point — it matches how most APIs work and keeps payloads small. Progressive delivery is an optimization for the initial load. Client-side virtualization is orthogonal and can be layered on top of either approach.

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
| `$page` | `$for` node | Pagination cursor and metadata |
| `$loadMore` | Action name | Client requests next page |
| `$version` | Tree root | Format version (for evolution) |

## Appendix: Android Client Deserialization

This appendix explores how an Android client would deserialize the JSON wire format into concrete Kotlin types and render them with Jetpack Compose. The goal is to surface design considerations early, not to prescribe a final implementation.

### Type Hierarchy

The node envelope maps naturally to a sealed interface hierarchy. Component nodes, control flow nodes, and action nodes share a common `TreeNode` supertype so they can appear interchangeably in `children`, `body`, and `actions` arrays:

```kotlin
sealed interface TreeNode

data class ComponentNode(
    val type: String,
    val id: String? = null,
    val props: Map<String, Value> = emptyMap(),
    val modifier: List<ModifierNode> = emptyList(),
    val children: List<TreeNode> = emptyList(),
    val actions: List<TreeNode> = emptyList(),
) : TreeNode

data class ModifierNode(
    val type: String,
    val fields: Map<String, Value> = emptyMap(),
)

// Control flow
data class IfNode(val condition: Value? = null, val bind: Map<String, Value>? = null,
    val then: List<TreeNode>, val elseIfs: List<ElseIfBranch> = emptyList(),
    val elseBody: List<TreeNode> = emptyList()) : TreeNode
data class ForNode(val bindings: List<String>, val iterable: Value, val body: List<TreeNode>) : TreeNode
data class SwitchNode(val expr: Value, val cases: List<SwitchCase>, val default: List<TreeNode> = emptyList()) : TreeNode
data class LetNode(val name: String, val value: Value, val body: List<TreeNode>) : TreeNode

// Actions
data class ActionNode(val name: String, val args: List<Value> = emptyList()) : TreeNode
data class AssignNode(val target: Value, val op: String, val value: Value) : TreeNode
```

### Value Type

The heterogeneous `Value` type (props, expression operands, action args) requires a sealed hierarchy that mirrors the wire format's union of primitives, containers, and reference types:

```kotlin
sealed interface Value

@JvmInline value class IntValue(val value: Long) : Value
@JvmInline value class DoubleValue(val value: Double) : Value
@JvmInline value class StringValue(val value: String) : Value
@JvmInline value class BoolValue(val value: Boolean) : Value
data object NullValue : Value
data class ArrayValue(val elements: List<Value>) : Value
data class MapValue(val entries: Map<String, Value>) : Value
data class DataObjectValue(val type: String, val fields: Map<String, Value>) : Value
data class RefValue(val name: String) : Value                  // { "$ref": "count" }
data class ExprValue(val operands: List<Value>) : Value        // { "$expr": ["+", ...] }
```

Using `value class` for primitives avoids boxing overhead on the JVM. An alternative is collapsing primitives into a single `LiteralValue` wrapper around `Any`, trading type safety for fewer classes. The sealed approach is safer and pairs well with `when` exhaustiveness checks.

### Decoding Strategy

Three viable approaches, in rough order of type safety:

**kotlinx.serialization with `JsonContentPolymorphicSerializer`.** A custom serializer inspects the `$type` key and delegates to the appropriate subclass. This integrates with Kotlin's serialization compiler plugin and provides compile-time safety for the non-polymorphic parts:

```kotlin
object TreeNodeSerializer : JsonContentPolymorphicSerializer<TreeNode>(TreeNode::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<TreeNode> {
        val obj = element.jsonObject
        return when (val type = obj["\$type"]?.jsonPrimitive?.contentOrNull) {
            "\$if"     -> IfNode.serializer()
            "\$for"    -> ForNode.serializer()
            "\$switch" -> SwitchNode.serializer()
            "\$let"    -> LetNode.serializer()
            "\$action" -> ActionNode.serializer()
            "\$assign" -> AssignNode.serializer()
            else       -> ComponentNode.serializer()   // user-defined component
        }
    }
}
```

The `Value` type needs a similar polymorphic serializer that distinguishes `$ref` objects, `$expr` objects, data objects (have `$type`), and plain JSON primitives/arrays/maps.

**Moshi with custom `JsonAdapter`.** Moshi's streaming API (`JsonReader.peekJson()`) allows lookahead on the `$type` field without buffering the entire object. This can be more memory-efficient for large payloads than kotlinx.serialization's `JsonElement` approach, but requires manual adapter wiring.

**Manual `JsonElement` tree walk.** Parse the entire response into a `JsonElement` tree (via kotlinx.serialization or org.json) and walk it manually. This is the most flexible and easiest to debug, but sacrifices compile-time guarantees. It may be the right starting point for prototyping.

All three approaches must handle the `Value` type carefully: a JSON number might be `IntValue` or `DoubleValue` (heuristic: if it has a decimal point or is outside `Long` range, treat as `Double`), and a JSON object could be a `$ref`, `$expr`, `DataObjectValue`, or `MapValue` depending on which keys are present.

### Expression Evaluation

At runtime, the client maintains a scope stack (state variables at the root, with `$for` bindings, `$let` bindings, and `$if` optional bindings pushed as frames). A recursive evaluator walks `ExprValue` tuples:

```kotlin
fun evaluate(value: Value, scope: Scope): Any? = when (value) {
    is RefValue  -> scope.resolve(value.name)
    is ExprValue -> {
        val op = (value.operands[0] as StringValue).value
        when (op) {
            "+"   -> (evaluate(value.operands[1], scope) as Number).toDouble() +
                     (evaluate(value.operands[2], scope) as Number).toDouble()
            "get" -> {
                val target = scope.resolve((value.operands[1] as StringValue).value) as Map<*, *>
                target[(value.operands[2] as StringValue).value]
            }
            // ... other operators
            else  -> error("Unknown operator: $op")
        }
    }
    is IntValue    -> value.value
    is StringValue -> value.value
    // ... other literal types pass through
    else -> error("Cannot evaluate: $value")
}
```

This is intentionally sketch-level. A production evaluator would need type coercion rules, error boundaries (what happens when a `$ref` is unresolved?), and the depth/complexity limits described in the security section. Whether the evaluator returns `Any?` or a typed `Value` is a design choice -- `Any?` is simpler for Compose interop, but a typed return enables better error reporting.

### Jetpack Compose Integration

Deserialized `TreeNode` values map to `@Composable` functions via a registry-based dispatch. The registry maps `$type` strings to composable renderers, allowing the component set to be extended without modifying the core rendering loop:

```kotlin
typealias NodeRenderer = @Composable (ComponentNode, Scope) -> Unit

class RendererRegistry {
    private val renderers = mutableMapOf<String, NodeRenderer>()
    fun register(type: String, renderer: NodeRenderer) { renderers[type] = renderer }
    fun resolve(type: String): NodeRenderer = renderers[type] ?: ::FallbackRenderer
}

@Composable
fun RenderTree(node: TreeNode, scope: Scope, registry: RendererRegistry) {
    when (node) {
        is ComponentNode -> registry.resolve(node.type).invoke(node, scope)
        is IfNode        -> RenderIf(node, scope, registry)
        is ForNode       -> RenderFor(node, scope, registry)
        is SwitchNode    -> RenderSwitch(node, scope, registry)
        is LetNode       -> RenderLet(node, scope, registry)
        is ActionNode    -> { /* actions are dispatched on events, not during composition */ }
        is AssignNode    -> { }
    }
}
```

Modifiers map to Compose `Modifier` chains. Each `ModifierNode` type resolves to a `Modifier` extension:

```kotlin
fun resolveModifier(nodes: List<ModifierNode>, scope: Scope): Modifier =
    nodes.fold(Modifier as Modifier) { acc, node ->
        when (node.type) {
            "Padding" -> acc.padding(
                start = node.fields.dp("leading", scope),
                top = node.fields.dp("top", scope),
                end = node.fields.dp("trailing", scope),
                bottom = node.fields.dp("bottom", scope),
            )
            "Opacity" -> acc.alpha(node.fields.float("value", scope))
            else -> acc // unknown modifier -- ignore or log
        }
    }
```

State bindings from `$state` map to Compose `mutableStateOf` holders. When an `$assign` action fires, the client updates the corresponding `MutableState`, and Compose's snapshot system triggers recomposition of any composable reading that state via `$ref`. This gives client-evaluated mode reactive updates without a server round-trip.

### Tradeoffs and Open Questions

- **Code generation vs hand-written renderers.** If the Bazaar compiler knows the full set of built-in components, it could generate the renderer registry and modifier mapping at build time. Hand-written renderers are more flexible but risk drift.
- **Error boundaries.** An unknown `$type` or a failed expression evaluation should not crash the entire tree. A `FallbackRenderer` (blank space, error text in debug builds) provides graceful degradation.
- **Performance.** For large trees, converting the entire JSON into a `TreeNode` graph upfront may cause allocation pressure. A lazy or streaming deserializer that walks the JSON tree during composition -- similar to how Compose's `LazyColumn` defers item composition -- could amortize cost. This interacts with the progressive delivery design.
- **Testing.** The sealed class hierarchy is straightforward to snapshot-test: serialize a `TreeNode` back to JSON and compare. Expression evaluation can be unit-tested in isolation from Compose.

## Appendix: Swift Client Deserialization

This section explores how an iOS/macOS client would deserialize Bazaar wire format responses into concrete Swift types and render them as SwiftUI views. The goal is to surface design tradeoffs — not to prescribe a final implementation.

### Type Hierarchy

The node envelope maps naturally to a Swift struct with an enum for the `$type` discriminator. The central modeling question is whether `TreeNode` should be an enum (closed set of cases), a protocol (open for extension), or a struct with a type tag.

An **enum with associated values** is the most idiomatic choice for a closed wire format. Control flow nodes (`$for`, `$if`, `$switch`, `$let`, `$while`), action nodes (`$assign`, `$action`), and component nodes are all known at compile time. If the node set is truly fixed, an enum gives exhaustive switch checking:

```swift
enum TreeNode: Sendable {
    case component(ComponentNode)
    case forLoop(ForNode)
    case whileLoop(WhileNode)
    case ifCond(IfNode)
    case switchStmt(SwitchNode)
    case letBinding(LetNode)
    case assign(AssignNode)
    case action(ActionNode)
}

struct ComponentNode: Sendable {
    let type: String
    let id: String?
    let props: [String: Value]
    let modifier: [Modifier]
    let children: [TreeNode]
    let actions: [TreeNode]
}

struct Modifier: Sendable {
    let type: String
    let fields: [String: Value]
}
```

A **protocol-based** approach (`protocol TreeNode`) would allow third-party node types, but the wire format's `$`-prefixed control flow set is closed by design — unknown `$type` values should hit a fallback path, not a plugin system. Protocols also lose exhaustive switching unless `@frozen` semantics are simulated with a visitor.

### The Value Type

Props are heterogeneous: a prop map can contain ints, doubles, strings, bools, null, arrays, maps, data objects, `$ref` bindings, and `$expr` trees. A Swift enum with associated values models this directly:

```swift
enum Value: Sendable {
    case int(Int64)
    case double(Double)
    case string(String)
    case bool(Bool)
    case null
    case array([Value])
    case map([String: Value])
    case data(DataObject)
    case ref(String)
    case expr([Value])  // tuple-encoded: [op, ...operands]
}

struct DataObject: Sendable {
    let type: String
    let fields: [String: Value]
}
```

One subtlety: JSON numbers are untyped. A `spacing: 8.0` and a `count: 8` are indistinguishable at the JSON level. The decoder could use `Decimal` as a single numeric case, or attempt `Int64` first and fall back to `Double`. The protobuf path avoids this entirely since `int64` and `double` are distinct wire types.

### Decoding Strategy

Two viable approaches exist for JSON deserialization.

**`Codable` with custom `init(from:)`** — The `$type` discriminator pattern requires peeking at a key before deciding which type to decode. This means `TreeNode` cannot use synthesized `Decodable` conformance; it needs a manual implementation:

```swift
extension TreeNode: Decodable {
    private enum CodingKeys: String, CodingKey {
        case type = "$type"
    }

    init(from decoder: any Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let typeName = try container.decode(String.self, forKey: .type)

        switch typeName {
        case "$for":    self = .forLoop(try ForNode(from: decoder))
        case "$while":  self = .whileLoop(try WhileNode(from: decoder))
        case "$if":     self = .ifCond(try IfNode(from: decoder))
        case "$switch": self = .switchStmt(try SwitchNode(from: decoder))
        case "$let":    self = .letBinding(try LetNode(from: decoder))
        case "$assign": self = .assign(try AssignNode(from: decoder))
        case "$action": self = .action(try ActionNode(from: decoder))
        default:        self = .component(try ComponentNode(from: decoder))
        }
    }
}
```

`Value` decoding is trickier because values can be primitives, objects with `$ref`/`$expr` keys, or data objects with `$type`. The decoder must attempt multiple strategies:

```swift
extension Value: Decodable {
    init(from decoder: any Decoder) throws {
        if let container = try? decoder.singleValueContainer() {
            if container.decodeNil() { self = .null; return }
            if let b = try? container.decode(Bool.self) { self = .bool(b); return }
            if let i = try? container.decode(Int64.self) { self = .int(i); return }
            if let d = try? container.decode(Double.self) { self = .double(d); return }
            if let s = try? container.decode(String.self) { self = .string(s); return }
            if let a = try? container.decode([Value].self) { self = .array(a); return }
        }
        let container = try decoder.container(keyedBy: DynamicCodingKey.self)
        if let key = DynamicCodingKey(stringValue: "$ref"),
           let name = try? container.decode(String.self, forKey: key) {
            self = .ref(name); return
        }
        if let key = DynamicCodingKey(stringValue: "$expr"),
           let tuple = try? container.decode([Value].self, forKey: key) {
            self = .expr(tuple); return
        }
        // Fall through to data object (has $type) or generic map
        if let typeKey = DynamicCodingKey(stringValue: "$type"),
           let typeName = try? container.decode(String.self, forKey: typeKey) {
            let fields = try decodeDynamicFields(from: container, excluding: ["$type"])
            self = .data(DataObject(type: typeName, fields: fields)); return
        }
        self = .map(try decodeDynamicFields(from: container, excluding: []))
    }
}
```

**`JSONSerialization` manual parsing** — An alternative is to deserialize into `Any` first, then walk the dictionary tree. This avoids the awkward `Codable` gymnastics for heterogeneous types but loses compile-time safety and requires casting at every level. For a wire format this dynamic, manual parsing may actually be simpler in practice — but it makes it harder to catch malformed payloads early.

A pragmatic middle ground: use `JSONSerialization` to get `[String: Any]`, then convert to the typed `Value` / `TreeNode` model in a second pass. This separates JSON parsing from semantic interpretation.

### Expression Evaluation

`$expr` tuples and `$ref` bindings are resolved against a **scope stack** — an ordered list of `[String: Value]` dictionaries representing state, `$for` bindings, and `$let` bindings. Resolution walks innermost-first:

```swift
struct Scope: Sendable {
    private var frames: [[String: Value]]

    func resolve(_ name: String) -> Value? {
        for frame in frames.reversed() {
            if let value = frame[name] { return value }
        }
        return nil
    }

    func pushing(_ bindings: [String: Value]) -> Scope {
        Scope(frames: frames + [bindings])
    }
}

func evaluate(_ value: Value, in scope: Scope) -> Value {
    switch value {
    case .ref(let name):
        return scope.resolve(name) ?? .null
    case .expr(let tuple):
        guard case .string(let op) = tuple.first else { return .null }
        switch op {
        case "+":
            return add(evaluate(tuple[1], in: scope), evaluate(tuple[2], in: scope))
        case "get":
            guard case .string(let root) = tuple[1],
                  case .string(let field) = tuple[2],
                  case .data(let obj) = scope.resolve(root) else { return .null }
            return obj.fields[field] ?? .null
        default: return .null
        }
    default:
        return value  // literals pass through
    }
}
```

The expression evaluator should enforce the depth and complexity limits described in the security section. A recursive evaluator with a depth counter is straightforward; a stack-based interpreter would be needed if bytecode expressions are adopted later.

### SwiftUI Integration

Deserialized `TreeNode` values must map to SwiftUI views. The core challenge is that SwiftUI's type system is static (`some View`), but the node tree is dynamic.

**Option A: `AnyView` wrapping.** A recursive function maps each node to `AnyView`. This is simple but defeats SwiftUI's diffing optimizations — every re-render produces a new type-erased wrapper, so SwiftUI cannot detect structural identity changes.

**Option B: `@ViewBuilder` with bounded switch.** Since `TreeNode` is an enum with a fixed case set, a `@ViewBuilder` switch produces a concrete `_ConditionalContent` type tree. This preserves structural identity but produces deeply nested generic types for large trees.

**Option C: Custom `View` per node kind.** Each node type gets its own `View` conformance. The top-level renderer switches on the enum and delegates. Combined with `ForEach` for children and `$id` for stable identity, this gives SwiftUI the best diffing information:

```swift
struct BazaarNodeView: View {
    let node: TreeNode
    let scope: Scope

    var body: some View {
        switch node {
        case .component(let comp):
            ComponentView(node: comp, scope: scope)
        case .ifCond(let ifNode):
            IfView(node: ifNode, scope: scope)
        case .forLoop(let forNode):
            ForLoopView(node: forNode, scope: scope)
        // ... remaining cases
        }
    }
}

struct ComponentView: View {
    let node: ComponentNode
    let scope: Scope

    var body: some View {
        let resolved = node.props.mapValues { evaluate($0, in: scope) }
        let content = ForEach(node.children.indices, id: \.self) { i in
            BazaarNodeView(node: node.children[i], scope: scope)
        }
        switch node.type {
        case "Row":   HStack(spacing: resolved.cgFloat("spacing")) { content }
        case "Text":  Text(resolved.string("value") ?? "")
        case "Badge": BadgeView(label: resolved.string("label"), color: resolved.string("color"))
        default:      FallbackView(typeName: node.type)
        }
    }
}
```

**Modifier application** is a separate concern. Since modifiers are an ordered array of typed objects, they can be applied with a `reduce` over the chain. This is one place where `AnyView` is hard to avoid — the return type changes with each modifier. This is an acceptable tradeoff since modifier chains are typically short (2-3 deep), and the outer `ComponentView` still maintains structural identity:

```swift
func applyModifiers(_ modifiers: [Modifier], to view: some View, scope: Scope) -> AnyView {
    modifiers.reduce(AnyView(view)) { current, mod in
        switch mod.type {
        case "Padding":
            let edges = resolvePaddingEdges(mod, scope: scope)
            return AnyView(current.padding(edges))
        case "Opacity":
            let value = evaluate(mod.fields["value"] ?? .double(1.0), in: scope)
            return AnyView(current.opacity(value.asDouble ?? 1.0))
        default:
            return current  // unknown modifier — pass through
        }
    }
}
```

**State management** maps to SwiftUI's `@Observable`. The `$state` block at the tree root initializes an observable store. `$assign` nodes in event handlers mutate the store, which triggers SwiftUI re-renders. Whether this store is a flat dictionary (`[String: Value]`) or a generated `@Observable` class depends on whether the client has schema access at build time.

### Tradeoffs and Open Questions

- **Component registry vs inline switch.** A `switch` on `node.type` is a closed mapping — new server-side components require a client update. A dictionary-based registry (`[String: (ComponentNode, Scope) -> AnyView]`) allows dynamic extension but loses type safety.
- **Performance.** Recursive `AnyView` construction on every state change is a concern for large trees. Memoization, structural identity hints from `$id`, and selective re-evaluation of `$expr` nodes could mitigate this. Instruments profiling on real payloads is needed before optimizing.
- **Accessibility.** Server-driven views must still produce correct accessibility trees. This likely requires accessibility-related props (`accessibilityLabel`, `accessibilityRole`) in the wire format, passed through to SwiftUI's accessibility modifiers.
- **Previews.** The server-evaluated examples from sections 2.8 and 2.9 are static JSON — they could drive SwiftUI Previews directly during development, without a running server.

## References

- [JSX Over The Wire](https://overreacted.io/jsx-over-the-wire/) — serializing component trees as JSON, client vs server component boundaries
- [Progressive JSON](https://overreacted.io/progressive-json/) — breadth-first streaming with placeholder references
- [Introducing RSC Explorer](https://overreacted.io/introducing-rsc-explorer/) — RSC wire protocol internals, streaming, suspense holes
- [Support All of Structured Clone in RSC Serialization](https://github.com/facebook/react/issues/25687) — RSC serialization capabilities and type support
