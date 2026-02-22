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

All examples in this document render the `TextAndButtonRow` template:

```bzr
data TextAndButtonRowModel {
    value string = "Hello, world!"
    label string = "Click, me!"
    message string? = null
}

template TextAndButtonRow(models [TextAndButtonRowModel]) {
    @State var count = 0

    for model in models {
        @Modifier(Padding(all = 12.0))
        Row {
            Text(model.value)
            Button(model.label) {
                count += 1
                if var message = model.message {
                    Print(message)
                }
            }
        }
    }
}
```

Called with `TextAndButtonRow([TextAndButtonRowModel()])` — one model with default values.

---

## 1. Concepts to Encode

| Concept | Bazaar IR | Wire concern |
|---|---|---|
| Component instance | `IrComponentCall` | Type discriminator, props, children |
| Data object | `IrData` | Typed key-value map passed as prop values |
| Modifier | `IrModifierCall` | Metadata attached to a node |
| Enum value | `IrEnum` | String or integer encoding |
| State variable | `IrStateDecl` | Client-side mutable binding |
| Expression | AST `Expr` | Evaluable by the client (see [section 5](#5-expression-encoding)) |
| Control flow | `IrForNode`, `IrIfNode` | Conditional/repeated subtrees |
| Action | `IrFunctionCall` | Server-side function reference |

## 2. JSON Format

### 2.1 Node Envelope

Every node in the tree uses a single envelope:

```json
{
    "$type": "Row",
    "$id": "n1",
    "props": {
        "alignment": "center"
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
| `modifier` | no | Modifier chain applied to this node. |
| `children` | no | Ordered list of child nodes. |

The `$` prefix on `$type` and `$id` prevents collisions with user-defined prop names.

### 2.2 Prop Values

Primitives map directly:

```json
{
    "label": "Click, me!",
    "count": 0,
    "opacity": 1.0,
    "enabled": true,
    "onClick": null
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
    "model": {
        "$type": "TextAndButtonRowModel",
        "value": "Hello, world!",
        "label": "Click, me!",
        "message": null
    }
}
```

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

A modifier is a typed object attached to a node, similar to a data object:

```json
{
    "$type": "Row",
    "modifier": {
        "$type": "Padding",
        "top": 12.0,
        "leading": 12.0,
        "bottom": 12.0,
        "trailing": 12.0
    },
    "props": { "alignment": "center" },
    "children": [ ... ]
}
```

Modifier chaining (future) could use an array:

```json
{
    "modifier": [
        { "$type": "Padding", "top": 12.0 },
        { "$type": "Background", "color": "#FF0000" }
    ]
}
```

### 2.5 Control Flow

Control flow can be handled in two ways. This is an open design question.

**Option A: Server-evaluated.** The server runs loops and conditionals, emitting a flat tree of concrete nodes. The wire format contains no control flow — just the rendered result. This is simpler for clients but means the server must re-render on state changes.

**Option B: Client-evaluated.** The wire format includes control flow nodes that the client interprets. This allows the client to re-render locally when state changes, without a server round-trip.

Option B encoding:

```json
{
    "$type": "$for",
    "binding": "model",
    "in": { "$ref": "models" },
    "body": [ ... ]
}
```

```json
{
    "$type": "$if",
    "condition": { "$bind": "model.message" },
    "then": [ ... ]
}
```

Control flow nodes use `$`-prefixed type names (`$for`, `$if`, `$switch`) to distinguish them from user components.

### 2.6 Actions

Function calls in event handlers are server actions — references the client sends back to the server for execution:

```json
{
    "$type": "$action",
    "name": "Print",
    "args": [{ "$ref": "message" }]
}
```

State mutations are client-side actions:

```json
{
    "$type": "$assign",
    "target": { "$ref": "count" },
    "op": "+=",
    "value": 1
}
```

### 2.7 Full Example (Server-Evaluated)

`TextAndButtonRow([TextAndButtonRowModel()])` with defaults, loop unrolled by the server:

```json
{
    "$state": {
        "count": { "initial": 0 }
    },
    "children": [
        {
            "$type": "Row",
            "$id": "n1",
            "modifier": {
                "$type": "Padding",
                "top": 12.0,
                "leading": 12.0,
                "bottom": 12.0,
                "trailing": 12.0
            },
            "props": {
                "alignment": "center"
            },
            "children": [
                {
                    "$type": "Text",
                    "$id": "n2",
                    "props": {
                        "value": "Hello, world!"
                    }
                },
                {
                    "$type": "Button",
                    "$id": "n3",
                    "props": {
                        "label": "Click, me!"
                    },
                    "children": [
                        {
                            "$type": "$assign",
                            "target": { "$ref": "count" },
                            "op": "+=",
                            "value": 1
                        }
                    ]
                }
            ]
        }
    ]
}
```

Note: the `if var message = model.message` branch is pruned because `message` is `null` at render time.

### 2.8 Full Example (Client-Evaluated)

Same template, but control flow and data binding are preserved for client interpretation:

```json
{
    "$state": {
        "count": { "initial": 0 }
    },
    "children": [
        {
            "$type": "$for",
            "binding": "model",
            "in": { "$param": "models" },
            "body": [
                {
                    "$type": "Row",
                    "modifier": {
                        "$type": "Padding",
                        "top": 12.0,
                        "leading": 12.0,
                        "bottom": 12.0,
                        "trailing": 12.0
                    },
                    "props": {
                        "alignment": "center"
                    },
                    "children": [
                        {
                            "$type": "Text",
                            "props": {
                                "value": { "$expr": ["get", "model", "value"] }
                            }
                        },
                        {
                            "$type": "Button",
                            "props": {
                                "label": { "$expr": ["get", "model", "label"] }
                            },
                            "children": [
                                {
                                    "$type": "$assign",
                                    "target": { "$ref": "count" },
                                    "op": "+=",
                                    "value": 1
                                },
                                {
                                    "$type": "$if",
                                    "bind": { "message": { "$expr": ["get", "model", "message"] } },
                                    "then": [
                                        {
                                            "$type": "$action",
                                            "name": "Print",
                                            "args": [{ "$ref": "message" }]
                                        }
                                    ]
                                }
                            ]
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
    repeated Node children = 2;
}

message Node {
    string type = 1;
    optional string id = 2;
    repeated Prop props = 3;
    optional Modifier modifier = 4;
    repeated Node children = 5;
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
        StateRef ref = 9;
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

message StateRef {
    string name = 1;
}

message Modifier {
    string type = 1;
    repeated Prop fields = 2;
    optional Modifier next = 3;  // chaining
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
        StateRef ref = 5;
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

message Action {
    string name = 1;
    repeated Value args = 2;
}
```

### 3.3 Control Flow (if client-evaluated)

```protobuf
message ForNode {
    string binding = 1;
    Expr iterable = 2;
    repeated Node body = 3;
}

message IfNode {
    Expr condition = 1;
    repeated Node then = 2;
    repeated ElseIfBranch else_ifs = 3;
    repeated Node else_body = 4;
}

message ElseIfBranch {
    Expr condition = 1;
    repeated Node body = 2;
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
{ "$expr": ["get", "model", "value"] }
{ "$expr": ["call", "Print", { "$ref": "message" }] }
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
    "label": "Click, me!",
    "value": { "$expr": ["get", "model", "value"] },
    "count": { "$ref": "count" }
}
```

A `$ref` is sugar for the common case of reading a single state variable. `$expr` handles everything else.

### Recommendation

Start with **Option B (tuple encoding)** with `$ref` sugar for simple state reads. It balances compactness and readability. Bytecode can be introduced later as an optimization if expression evaluation becomes a bottleneck.

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

### Open Questions

- **Scoping:** Can state be scoped to a subtree, or is it always template-level? The IR currently has template-level `@State` only.
- **Derived state:** Should there be computed/derived values that auto-update? (e.g., `fullName` derived from `first` + `last`)
- **Server sync:** When state changes, does the client re-render locally (client-evaluated) or request a new tree from the server (server-evaluated)? This is the central architectural question.

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

A hybrid is likely: server-evaluated for initial render, client-evaluated for state-driven interactions (counters, toggles, form inputs), server round-trip for actions (`Print`, `Dismiss`).

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
- Bazaar equivalent: `{ "$action": "Print", "args": ["hello"] }` triggers a server RPC.
- Open: authentication, rate limiting, idempotency, error handling for actions.

### Children Semantics

In Bazaar, `children [component]` is a typed slot. In the wire format, `children` is an array of nodes. Questions:

- Should there be named slots (e.g., `header`, `footer`) in addition to default `children`?
- Should `children` in event handlers (action lists) be distinguished from `children` in component composition?

## References

- [JSX Over The Wire](https://overreacted.io/jsx-over-the-wire/) — serializing component trees as JSON, client vs server component boundaries
- [Progressive JSON](https://overreacted.io/progressive-json/) — breadth-first streaming with placeholder references
- [Introducing RSC Explorer](https://overreacted.io/introducing-rsc-explorer/) — RSC wire protocol internals, streaming, suspense holes
- [Support All of Structured Clone in RSC Serialization](https://github.com/facebook/react/issues/25687) — RSC serialization capabilities and type support
