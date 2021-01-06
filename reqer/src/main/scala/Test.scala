
import json.Json._
import json.Json.Converter._

object Test extends App {

  case class Person(name: String, age: Int)

  object Person {
    implicit val writer: Writer[Person] = Testing.defaultWriter[Person]
  }

  def uniter[T](c: T): Unit = Testing.unitMacro[T](c)
  uniter("someString")
  val p: Person = Person("Amin", 21)
  println(p.toJson)

  uniter[Int](2)
}
