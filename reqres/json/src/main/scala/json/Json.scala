package json

object Json {
  sealed trait JsonValue {
    def toString: String
  }

  implicit class JsonString(val value: String) extends JsonValue {
    override def toString: String = "\"" + value + "\""
  }

  object JsonString {
    def unapply(arg: JsonString): Option[String] = Some(arg.value)
  }

  trait JsonNumber extends JsonValue

  implicit class JsonBool(val value: Boolean) extends JsonValue {
    override def toString: String = value.toString
  }

  object JsonBool {
    def unapply(arg: JsonBool): Option[Boolean] = Some(arg.value)
  }

  implicit class JsonInt(val value: Int) extends JsonNumber {
    override def toString: String = value.toString
  }

  object JsonInt {
    def unapply(arg: JsonInt): Option[Int] = Some(arg.value)
  }

  implicit class JsonLong(val value: Long) extends JsonNumber {
    override def toString: String = value.toString
  }

  object JsonLong {
    def unapply(arg: JsonLong): Option[Long] = Some(arg.value)
  }

  implicit class JsonFloat(val value: Float) extends JsonNumber {
    override def toString: String = value.toString
  }

  object JsonFloat {
    def unapply(arg: JsonFloat): Option[Float] = Some(arg.value)
  }

  implicit class JsonDouble(val value: Double) extends JsonNumber {
    override def toString: String = value.toString
  }

  object JsonDouble {
    def unapply(arg: JsonDouble): Option[Double] = Some(arg.value)
  }

  final case class JsonArray(value: Seq[JsonValue]) extends JsonValue {
    override def toString: String = value.filter(_ != JsonNull).map(_.toString).mkString("[", ",", "]")
  }

  final class JsonObject(val values: (String, JsonValueWrapper)*) extends JsonValue {
    override def toString: String = {
      values.filter (_._2 != JsonNull)
        .map { pair =>
          pair._2 match {
            case JsonValueWrapperImpl(value) =>
              "\"" + pair._1 + "\":" + value.toString
          }
        }.mkString("{", ",", "}")
    }
  }

  object JsonObject {
    /* to fix UNEXPECTED apply method in case class apply method */
    def apply(values: (String, JsonValueWrapper)*): JsonObject = new JsonObject(values: _*)

    def unapply(arg: JsonObject): Option[Seq[(String, JsonValueWrapper)]] = Some(arg.values)
  }

  trait Writer[T] {
    def makeJson(value: T): JsonValue
  }

  trait Reader[T] {
    def extractJson(value: JsonValue): T
  }

  object Converter {

    implicit val stringWriter: Writer[String] = JsonString
    implicit val intWriter: Writer[Int] = JsonInt
    implicit val floatWriter: Writer[Float] = JsonFloat
    implicit val doubleWriter: Writer[Double] = JsonDouble
    implicit val longWriter: Writer[Long] = JsonLong
    implicit def seqWriter[T](implicit wjs: Writer[T]): Writer[Seq[T]] = (seq: Seq[T]) => JsonArray(seq.map(wjs.makeJson))
    implicit def listWriter[T](implicit wjs: Writer[T]): Writer[List[T]] = (list: List[T]) => JsonArray(list.map(wjs.makeJson))
    implicit def arrayWriter[T](implicit wjs: Writer[T]): Writer[Array[T]] = (array: Array[T]) => JsonArray(array.map(wjs.makeJson))
    implicit def optionWriter[T](implicit wjs: Writer[T]): Writer[Option[T]] = (value: Option[T]) =>
      if (value.isEmpty) JsonNull else wjs.makeJson(value.get)

    implicit class ImplicitConverter[T](value: T) {
      def toJson(implicit wjs: Writer[T]): JsonValue = wjs.makeJson(value)
    }
  }

  sealed trait JsonValueWrapper
  implicit class JsonValueWrapperImpl[T](val value: T)(implicit val wjs: Writer[T]) extends JsonValueWrapper
  implicit class JsonOptionalWrapper[T](val value: Option[T])(implicit wjs: Writer[T]) extends JsonValueWrapper
  object JsonNull extends JsonValueWrapper with JsonValue {
    override def toString: String = ""
  }

  object JsonValueWrapperImpl {
    def unapply[T](arg: JsonValueWrapperImpl[T]): Option[JsonValue] = Some(arg.wjs.makeJson(arg.value))
  }

}
