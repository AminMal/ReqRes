package json

object Json {
  sealed trait JsonValue {
    def toString: String
  }

  final case class JsonString(value: String) extends JsonValue {
    override def toString: String = "\"" + value + "\""
  }

  trait JsonNumber extends JsonValue

  implicit class JsonInt(val value: Int) extends JsonNumber {
    override def toString: String = value.toString
  }

  implicit class JsonLong(val value: Long) extends JsonNumber {
    override def toString: String = value.toString
  }

  implicit class JsonFloat(val value: Float) extends JsonNumber {
    override def toString: String = value.toString
  }

  implicit class JsonDouble(val value: Double) extends JsonNumber {
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

  trait Writer[T] {
    def makeJson(value: T): JsonValue
  }

  trait Reader[T] {
    def extractJson(value: JsonValue): T
  }

  object Converter {

    implicit val stringWriter: Writer[String] = JsonString.apply
    implicit val intWriter: Writer[Int] = JsonInt
    implicit val floatWriter: Writer[Float] = JsonFloat
    implicit val doubleWriter: Writer[Double] = JsonDouble
    implicit val longWriter: Writer[Long] = JsonLong
    implicit def seqWriter[T](implicit wjs: Writer[T]): Writer[Seq[T]] = (seq: Seq[T]) => JsonArray(seq.map(wjs.makeJson))
    implicit def listWriter[T](implicit wjs: Writer[T]): Writer[List[T]] = (list: List[T]) => JsonArray(list.map(wjs.makeJson))
    implicit def arrayWriter[T](implicit wjs: Writer[T]): Writer[Array[T]] = (array: Array[T]) => JsonArray(array.map(wjs.makeJson))

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
