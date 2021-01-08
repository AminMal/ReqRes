
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

  val testingWriter = TestingWriter(
    name = "test",
    age = 2.54,
    numbers = Seq(1, 2, 3),
    admin = Admin("Joe", 34)
  )
  println(testingWriter.toJson) // Works pretty well

  object TestingReader {
    val someJsonSeq: JsonValue = Seq(1, 2, 3).toJson
  }
  import TestingReader._
  // Now say you have a JsonValue as a Seq of numbers like "[1, 2, 3]" without the quotes
  // And you want to extract it as List[Int], here's how you do it
  val normalList: List[Int] = someJsonSeq.extractAs[List[Int]]
  println(normalList)
  try {
    println(someJsonSeq.extractAs[String])
  } catch {
    case CannotCastTypeException =>
      println("Cannot cast as string, because It's a list of ints")
  }
}

