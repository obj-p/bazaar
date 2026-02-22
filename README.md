# Bazaar

A statically-typed, component-oriented language for declarative UI definition.

Bazaar is designed around composable UI primitives — components, modifiers, templates, and previews — with a concise syntax that blends declarative structure with imperative logic where needed.

## Example

A TODO app that exercises enums, components, modifiers, data, functions, templates, state, control flow, and previews:

```
package todo

import ui
import icons

enum Priority { low, medium, high }
enum Filter { all, active, completed }

component Column {
    spacing double = 0
    children [component]
}

component Row {
    spacing double = 0
    children [component]
}

component Text {
    value string
    bold bool = false
    strikethrough bool = false
}

component TextField {
    value string = ""
    placeholder string = ""
    onSubmit func(string)? = null
}

component Button {
    label string
    enabled bool = true
    onTap func()? = null
}

component Checkbox {
    checked bool = false
    onToggle func()? = null
}

component Icon {
    name string
}

component Badge {
    label string
    color string = "#888888"
}

component Divider {}

modifier Padding {
    top double = 0
    leading double = 0
    bottom double = 0
    trailing double = 0
    constructor(all double) = Padding(all, all, all, all)
    constructor(vertical double, horizontal double) = Padding(vertical, horizontal, vertical, horizontal)
}

modifier Background {
    color string
}

modifier Opacity {
    value double = 1.0
}

data TodoItem {
    id int
    title string
    completed bool = false
    priority Priority = Priority.medium
    note string? = null
}

data TodoStats {
    total int = 0
    completed int = 0
    labels {string: int} = {}
}

func AddTodo(title string, priority Priority) -> TodoItem
func ToggleTodo(id int)
func DeleteTodo(id int)
func ClearCompleted()

template TodoApp(items [TodoItem]) {
    @State var todos = items
    @State var newTitle = ""
    @State var filter = Filter.all
    @State var count = 0

    @Modifier(Padding(all = 16.0))
    Column(spacing = 8.0) {
        TodoHeader(newTitle)
        Divider()
        TodoList(todos, filter)
        Divider()
        TodoFooter(count, filter)
    }
}

template TodoHeader(title string) {
    @Modifier(Padding(vertical = 8.0, horizontal = 0.0))
    Row(spacing = 8.0) {
        TextField(title, placeholder = "What needs to be done?")
        Button("Add", enabled = title != "") {
            AddTodo(title, Priority.medium)
        }
    }
}

template TodoList(todos [TodoItem], filter Filter) {
    for todo in todos {
        if filter == Filter.all || (filter == Filter.active && !todo.completed) || (filter == Filter.completed && todo.completed) {
            TodoItemRow(todo)
        }
    }
}

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

template TodoFooter(count int, activeFilter Filter) {
    Row(spacing = 12.0) {
        Text("${count} items remaining")

        Button("All", enabled = activeFilter != Filter.all)
        Button("Active", enabled = activeFilter != Filter.active)
        Button("Completed", enabled = activeFilter != Filter.completed)

        Button("Clear completed") {
            ClearCompleted()
        }
    }
}

preview TodoAppPreview {
    TodoApp([
        TodoItem(1, "Buy groceries", priority = Priority.high, note = "Milk, eggs, bread"),
        TodoItem(2, "Write tests", completed = true),
        TodoItem(3, "Read proposal")
    ])
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
