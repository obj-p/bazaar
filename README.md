# Bazaar

A statically-typed, component-oriented language for declarative UI definition.

Bazaar is designed around composable UI primitives — components, modifiers, templates, and previews — with a concise syntax that blends declarative structure with imperative logic where needed.

## Example

```
package example

import layout

enum RowAlignment {
    top, center, bottom
}

component Row {
    alignment RowAlignment = RowAlignment.center
    children [component]
}

component Button {
    label string
    onClick func()? = null
}

component Text {
    value string
}

modifier Padding {
    top double = 0
    leading double = 0
    bottom double = 0
    trailing double = 0

    constructor(all double) = Padding(all, all, all, all)
    constructor(vertical double, horizontal double) = Padding(vertical, horizontal, vertical, horizontal)
}

func Add(x int, y int) -> int

func Truncate(x double) -> int

func Print(message string)

func Dismiss(animated bool = false)

data Point {
    x double = 0.0
    y double = 0
}

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

preview TextAndButtonRowPreview {
    TextAndButtonRow([TextAndButtonRowModel()])
}
```

## Features

- **Declarations**: enums, components, data classes, modifiers, functions, templates, previews
- **Type system**: named types, function types, arrays, maps, nullable types
- **Expressions**: arithmetic, comparison, logical, null coalescing (`??`), exponentiation (`**`), optional chaining (`?.`)
- **Control flow**: if/else with optional binding, for-in, for-condition, switch
- **Lambdas** with trailing closure syntax
- **String interpolation** and escape sequences
- **Annotations**
- **Imports** with aliases

## Building

**Prerequisites**: Kotlin 2.2+, Gradle 8.13+

```sh
# Run tests for your host platform
./gradlew :bazaar-parser:macosArm64Test

# Run all platform tests
./gradlew :bazaar-parser:allTests

# Regenerate golden test files after intentional AST changes
RECORD=true ./gradlew :bazaar-parser:allTests
```

## Project Structure

The `bazaar-parser` module contains the ANTLR4 grammar, Kotlin Multiplatform parser, AST definitions, and test suite.

**Supported platforms**: macOS (ARM64, x64), Linux x64, Windows x64 (MinGW)

## Roadmap

Tracked in [#32 (future language features)](https://github.com/obj-p/bazaar/issues/32) and [#33 (CLI with native binary output)](https://github.com/obj-p/bazaar/issues/33).

- Binary, hex, and octal literals
- Raw strings and multi-line strings
- Ternary conditional, range expressions, type casting
- `break`, `continue`, guard statements
- Sets, tuples
- Async/await
- Pattern matching
- Generics and protocols
- Native CLI executable (`bazaar-cli`)

