package json

import scala.reflect.runtime.{universe => ru}

object Json {
  sealed trait JsonValue {
    def toString: String
    def valueInside: String = this.toString
  }

  final case class JsonString(value: String) extends JsonValue {
    override def toString: String = "\"" + value + "\""
  }

  final case class JsonInt(value: Int) extends JsonValue {
    override def toString: String = value.toString
  }

  final case class JsonLong(value: Long) extends JsonValue {
    override def toString: String = value.toString
  }

  final case class JsonFloat(value: Float) extends JsonValue {
    override def toString: String = value.toString
  }

  final case class JsonDouble(value: Double) extends JsonValue {
    override def toString: String = value.toString
  }

  final case class JsonArray(value: Seq[JsonValue]) extends JsonValue {
    override def toString: String = value.map(_.toString).mkString("[", ",", "]")
  }

  final case class JsonObject(values: (String, JsonValueWrapper)*) extends JsonValue {
    override def toString: String = {
      values.map { pair =>
        pair._2 match {
          case JsonValueWrapperImpl(value) =>
            "\"" + pair._1 + "\":" + value.toString
        }
      }.mkString("{", ",", "}")
    }
  }

  def runtimeReflector[T : ru.TypeTag]: Writer[T] = {
    import Converter._
    val t = ru.typeOf[T]
    val members: List[String] = t.decls.filter(_.isTerm).filter(_.isPrivateThis).toList.map(_.name.toString)

    def iterate(source: List[String] = members, acc: Seq[(String, JsonValueWrapper)] = Seq()): Seq[(String, JsonValueWrapper)] = {
      if (source.isEmpty) acc
      else {
        val value = source.head.filterNot(_ == ' ') -> new JsonValueWrapperImpl(t.decl(ru.TermName(source.head)).asTerm.toString)
        iterate(source.tail, acc :+ value)
      }
    }

    (_: T) => JsonObject(
      iterate(): _*
    )
  }

  trait Writer[T] {
    def makeJson(value: T): JsonValue
  }

  trait Reader[T] {
    def extractJson(value: JsonValue): T
  }

  object Converter {

    implicit val stringWriter: Writer[String] = (string: String) => JsonString(string)
    implicit val intWriter: Writer[Int] = (int: Int) => JsonInt(int)

    implicit class ImplicitConverter[T](value: T) {
      def toJson(implicit wjs: Writer[T]): JsonValue = wjs.makeJson(value)
    }
  }

  sealed trait JsonValueWrapper
  implicit class JsonValueWrapperImpl[T](val value: T)(implicit val wjs: Writer[T]) extends JsonValueWrapper

  object JsonValueWrapperImpl {
    def unapply[T](arg: JsonValueWrapperImpl[T]): Option[JsonValue] = Some(arg.wjs.makeJson(arg.value))
  }

}
