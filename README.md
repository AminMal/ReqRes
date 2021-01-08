# ReqRes
### Json
Use this library to convert your objects of any type to json format, having class `Person` like:
```
import json.Json._
import json.Json.Converter._

case class Person(name: String, age: Int)
object Person {
  implicit val writer: Writer[Person] = 
    (p: Person) => 
      JsonObject("name" -> p.name, "age" -> p.age)
}
```
Or even simpler!
```
object Person {
  implicit val writer: Writer[Person] = JsonDefault.write[Person]
}
```
You can convert your object of type `Person` into JSON format like:
```
val person = Person("Bob", 32)
val personAsJson: JsonValue = person.toJson
```
### Http
You can use this package to send requests and receive responses, in development.