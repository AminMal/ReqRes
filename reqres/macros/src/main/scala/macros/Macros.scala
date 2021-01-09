package macros

import scala.reflect.macros.blackbox

class Macros(val c: blackbox.Context) {
  import c.universe._
  import json.Json._

  def mWriteImpl[A : c.WeakTypeTag]: c.Expr[Writer[A]] = {
    val tpe: Type = weakTypeOf[A]
    val members = tpe.decls

    val params = members.filter(_.isPrivateThis)
    val writerParams: Seq[Tree] = params.map { field =>
      val fullName = field.name.toString.filterNot(_ == ' ')
      val name = TermName(fullName)
      val mapKey: String = name.decodedName.toString
      q"$mapKey -> value.$name"
    }.toSeq

    c.Expr[Writer[A]] {
      q"""
         import json.Json.Converter._
         new Writer[$tpe] {
           def makeJson(value: $tpe): JsonValue = JsonObject(..$writerParams)
         }
       """
    }
  }
}
