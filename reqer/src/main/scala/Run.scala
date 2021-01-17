
import json.Json
import json.Json._
import json.Json.Converter._

/**
 * This file: Run.scala is created to test the library, will be removed in the first release
 */

object Run extends App {

  case class Person(name: String, age: Int)
  object Person {
    implicit val wjs: Writer[Person] = JsonDefault.writer[Person]
  }

  case class Complex(
                    id: String,
                    persons: Seq[Option[Person]],
                    maybeNumbers: Option[Seq[Int]]
                    )
  object Complex {
    implicit val wjs: Writer[Complex] = JsonDefault.writer[Complex]
  }

  val a = Person("Amin", 21)
  val b = Person("Ali", 32)

  val firstComplex: Complex = Complex(
    id = "12Abd4",
    persons = Seq(Some(a), None, Some(b), None, None),
    maybeNumbers = Some(Seq(1, 2, 3))
  )

  val secondComplex: Complex = Complex (
    id = "someId",
    persons = Seq(Some(a)),
    maybeNumbers = None
  )

  println(firstComplex.toJson.toString)
  println(secondComplex.toJson.toString)
  val obj = JsonObject("single param" -> 2)
  obj match {
    case JsonObject(values) =>
      println("Values => " + values.map {
        case (first, second) =>
          first + " -> " + (second match {  /** Testing JsonObject unapply, works fine **/
            case impl: JsonValueWrapperImpl[_] => impl.value.toString
            case wrapper: JsonOptionalWrapper[_] => wrapper.value.toString
            case Json.JsonNull => "null"
          })
      })
  }
}

