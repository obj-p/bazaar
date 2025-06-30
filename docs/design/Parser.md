Person.bzr

```
data Person(
    firstName String
    lastName String
)

template Person(person Person) {
    Stack(axis: .vertical) {
        Text(person.firstName)
        Text(person.lastName)
    }
}
```
