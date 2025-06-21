```
package main

import "github.com/obj-p/cross/state"

template Person(person Person) {
    cross.useState()

    Container {
        Text(person.firstName)

        Button("Commit", onClick = {
            let success = StorePerson(person)
            if (success) {

            }
        })
    }
}
```
