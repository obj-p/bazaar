# Syntax

```
enum RowAlignment {
    top, center, bottom
}

component Row {
    alignnment RowAlignment = .center
    children []component
}

component Icon {
    name String
}

component Button {
    label string
    icon? Icon = null
    onClick? function() = null
}

component Text {
    value string
}

function add(x int, y int)

function truncate(x double)

function print(message string)

data TextAndButtonRowModel {
    value string
    label string
    message string
    shouldPrint bool
}

template TextAndButtonRow(models []TextAndButtonRowModel) component {
    for model in models {
        Row {
            Text(model.value)

            Button(model.label, function() {
                if model.shouldPrint {
                    print(model.message)
                }
            })
        }
    }
}
```
