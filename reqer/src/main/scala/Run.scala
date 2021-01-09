
import json.Json._
import json.Json.Converter._

/**
 * This file: Run.scala is created to test the library, will be removed in the first release
 */

object Run extends App {


  case class Admin(name: String, age: Int)
  object Admin {
    implicit val wjs: Writer[Admin] = JsonDefault.writer[Admin]
  }

  case class TestingWriter(name: String, age: Double, numbers: Seq[Int], admin: Admin)
  object TestingWriter {
    implicit val wjs: Writer[TestingWriter] = JsonDefault.writer[TestingWriter]
  }

  val admin = Admin("some name", 21)
  println(admin.toJson)

  val testingWriter = TestingWriter(
    name = "test",
    age = 2.54,
    numbers = Seq(1, 2, 3),
    admin = admin
  )
  println(testingWriter.toJson)
  case class IHaveOptionals(name: String, numbers: Option[Int])
  object IHaveOptionals {
    implicit val writer: Writer[IHaveOptionals] = JsonDefault.optionalWriter[IHaveOptionals]
  }

  val o1 = IHaveOptionals("Amin", Some(1))
  val o2 = IHaveOptionals("Ali", None)
}

