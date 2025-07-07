# Syntax

## Declarations

First and foremost, Bazaar is a language to specify components, data, and functions for use in server-driven UI (SDUI).
The language is meant to declare types. The declarations may then be used to generate FE and BE code. Here's an example
of a few component declarations.

```
component Button {
    label string
    onClick function()
}

component Row {
    alignment? HorizontalAlignment = null // an example of an optional field and defaulting
    children []component
}

component Text {
    value string
}
```

Peaking ahead, the BE may specify a "template" using these components.

```
function showAToast() // A function declaration

template TextAndButtonRow() {
    Row {
        Text("Some text.")
        Button("A button.", showAToast)
    }
}
```

## Built-in types

Bazaar supports the following built-in types: `bool`, `int`, `double`, `string`, and `function`. The following are
also keywords for declaring types: `component`, `data`, and `function`. A `template` is a special kind of keyword that
is not available for use as a referenced type in a field or parameter.

Arrays may be declared by prepending a `[]` to any type. A map may be specified by prepending a `{string}` to any type
where the `string` may be replaced by a primitive or user-specified type.

## Components

Components are declarations that specify available UI views. The declaration may specify required or optional fields
using any of the aforementioned built-in or user defined types.

```
data Person {
    firstName String
    lastName String
    email? String
}

component Profile {
    person Person
}
```

A component may be used within a `template` with a function-like syntax. The fields must be specified as arguments in
the order they are declared.

```
Row([Text("Some text."), Button("A button.", showAToast)])

// or
Row(children = [Text("Some text."), Button("A button.", showAToast)])

// or the following syntax may be used for []component...
Row({
    Text("Some text.")
    Button("A button.", showAToast)
})

// or if the []component is the last field...
Row {
    Text("Some text.")
    Button("A button.", showAToast)
}
```

The closure-like syntax used in the above example is available to `component` only. A function may emulate it.

```
Button("SomeButton", function() {
    showAToast()
})
```

If a component has two or more `[]component` fields specified last, the following syntax is available.

```
LeadingTrailingRow {
    // Leading components
} trailing {
    // Trailing components
}
```

## Data

Data can be used to specifiy custom types. Data is purely data and cannot specify `function`s or `component`s as fields.

```
data Person {
    firstName String
    middleName? String
    lastName String
    email? String
    photoUrl String
    isVerified Bool
}
```

## Enums

```
enum HorizontalAlignment {
    leading, center, trailing
}

enum VerticalAlignment {
    leading, center, trailing
}
```

## Functions

```
function concatenatedName(person Person) String {
    "${person.lastName}, ${person.firstName}"
}
```

### Control flow

### Concurrency

## Imports

## Modifiers

```
modifier isHidden(hidden Bool)
```

```
Badge()
    .isHidden(!person.isVerified)
```

## State

```
@State var isVerified = person.isVerified
```

## Templates

```
template Profile(person Person) component {
    @State var isVerified = person.isVerified

    Row {
        Overlay(alignment = Alignment(.center, .center)) {
            Image(person.photoUrl)

            if (isVerified) {
               Badge()
            }
        }

        Text(concatenatedName(person))
    } onClick {
        verifyPerson(function (isSuccess) {
            isVerified = isSuccess
        })
    }
}
```

### Control flow

### Concurrency

### Lifecycle
