package json

object Json {
  object CannotCastTypeException extends Exception("Cannot cast types")
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
    def extractJson[V <: JsonValue](value: V): T
  }

  object Converter {

    /**
     * Basic writers
     */
    implicit val stringWriter: Writer[String] = JsonString.apply
    implicit val intWriter: Writer[Int] = JsonInt
    implicit val floatWriter: Writer[Float] = JsonFloat
    implicit val doubleWriter: Writer[Double] = JsonDouble
    implicit val longWriter: Writer[Long] = JsonLong
    implicit def seqWriter[T](implicit wjs: Writer[T]): Writer[Seq[T]] = (seq: Seq[T]) => JsonArray(seq.map(wjs.makeJson))
    implicit def listWriter[T](implicit wjs: Writer[T]): Writer[List[T]] = (list: List[T]) => JsonArray(list.map(wjs.makeJson))
    implicit def arrayWriter[T](implicit wjs: Writer[T]): Writer[Array[T]] = (array: Array[T]) => JsonArray(array.map(wjs.makeJson))

    /**
     * Basic readers
     */
    implicit val stringReader: Reader[String] = new Reader[String] {
      override def extractJson[V <: JsonValue](value: V): String = {
        value match {
          case s: JsonString => s.value
          case _ => throw CannotCastTypeException
        }
      }
    }
    implicit val intReader: Reader[Int] = new Reader[Int] {
      override def extractJson[V <: JsonValue](value: V): Int = {
        value match {
          case someInt: JsonInt =>
            someInt.value
          case _ => throw CannotCastTypeException
        }
      }
    }
    implicit val doubleReader: Reader[Double] = new Reader[Double] {
      override def extractJson[V <: JsonValue](value: V): Double = {
        value match {
          case someDouble: JsonDouble =>
            someDouble.value
          case _ =>
            throw CannotCastTypeException
        }
      }
    }
    implicit val floatReader: Reader[Float] = new Reader[Float] {
      override def extractJson[V <: JsonValue](value: V): Float = {
        value match {
          case someFloat: JsonFloat =>
            someFloat.value
          case _ =>
            throw CannotCastTypeException
        }
      }
    }
    implicit def seqReader[T](implicit rjs: Reader[T]): Reader[Seq[T]] = new Reader[Seq[T]] {
      override def extractJson[V <: JsonValue](value: V): Seq[T] = {
        value match {
          case someArray: JsonArray =>
            someArray.value.map(rjs.extractJson)
          case _ => throw CannotCastTypeException
        }
      }
    }
    implicit def listReader[T](implicit rjs: Reader[T]): Reader[List[T]] = new Reader[List[T]] {
      override def extractJson[V <: JsonValue](value: V): List[T] = {
        value match {
          case someArray: JsonArray =>
            someArray.value.map(rjs.extractJson).toList
          case _ => throw CannotCastTypeException
        }
      }
    }

    implicit class ImplicitConverter[T](value: T) {
      def toJson(implicit wjs: Writer[T]): JsonValue = wjs.makeJson(value)
    }
    implicit class ImplicitReader[V <: JsonValue](value: V) {
      def extractAs[T](implicit rjs: Reader[T]): T = rjs.extractJson(value)
    }
  }

  sealed trait JsonValueWrapper
  implicit class JsonValueWrapperImpl[T](val value: T)(implicit val wjs: Writer[T]) extends JsonValueWrapper

  object JsonValueWrapperImpl {
    def unapply[T](arg: JsonValueWrapperImpl[T]): Option[JsonValue] = Some(arg.wjs.makeJson(arg.value))
  }

}
