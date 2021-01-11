
import json.Json._
import json.Json.Converter._

/**
 * This file: Run.scala is created to test the library, will be removed in the first release
 */

object Run extends App {

  case class Person(name: String, age: Int)
  object Person {
    implicit val writer: Writer[Person] = JsonDefault.writer[Person]
  }
  case class ComplexDataStructure(
                                 structureName: String,
                                 number_of_usages: Int,
                                 persons: Option[Person]
                                 )
  object ComplexDataStructure {
    implicit val writer: Writer[ComplexDataStructure] = JsonDefault.writer[ComplexDataStructure]
  }

  val nonEmptyPersons = ComplexDataStructure (
    "some complex data structure",
    number_of_usages = 2,
    persons = Some(Person("Amin", 21))
  )
  val emptyPersons = ComplexDataStructure (
    "another complex ds",
    number_of_usages = 0,
    persons = None
  )
  println("the one that has some person in it ->")
  println(nonEmptyPersons.toJson.toString)
  println("The one that has none inside -> ")
  println(emptyPersons.toJson.toString)
}

