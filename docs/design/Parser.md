```
// Person.bzr

data Person {
    firstName String
    middleName? String
    lastName String
    photoUrl String
    isVerified Bool
}
```

```
// Profile.bzr

import Person

enum HorizontalAlignment {
    center
}

enum VerticalAlignment {
    center
}

data Alignment {
    horizontalAlignment HorizontalAlignment
    verticalAlignment VerticalAlignment
}

component Badge {}

component Overlay {
    alignment Alignment
    children component[]
}

component Row {
    children component[]
    onClick function ()
}

component Text {
    value String
}

component Image {
    url String
}

function navigateToProfileDetails()

function concatenatedName(person Person) String {
    "${person.lastName}, ${person.firstName}"
}

template Profile(person Person) component {
    Row {
        Overlay(alignment = Alignment(.center, .center)) {
            Image(person.photoUrl)

            if (person.isVerified) {
               Badge()
            }
        }

        Text(concatenatedName(person))
    } onClick {
        navigateToProfileDetails()
    }
}
```
