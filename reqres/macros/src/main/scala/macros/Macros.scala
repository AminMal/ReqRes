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

  def optionalWriterImpl[A : c.WeakTypeTag]: c.Expr[Writer[A]] = {
    val tpe: Type = weakTypeOf[A]
    val members = tpe.decls

    val params = members.filter(_.isPrivateThis)

    def isOptional(sym: Symbol): Boolean = sym.typeSignature <:< weakTypeOf[Option[Any]]
    def isDefined(sym: Symbol): c.Expr[Boolean] = { // assuming isOptional =>
      if (!isOptional(sym)) c.Expr[Boolean](q"false")
      else {
      val fullName = sym.name.toString.filterNot(_ == ' ')
      val name = TermName(fullName)
      c.Expr[Boolean](
        q"""
           value.$name.isDefined
         """
      ) }
    }

    val optionals: Seq[Symbol] = params.filter(isOptional).toSeq
    val nonOptionals: Seq[Symbol] = params.filterNot(isOptional).toSeq
    val validNonOptionals: Seq[Tree] = nonOptionals.map { field =>
      val fullName = field.name.toString.filterNot(_ == ' ')
      val name = TermName(fullName)
      val mapKey: String = name.decodedName.toString
      q"$mapKey -> value.$name"
    }
    val validOptionals: Seq[Tree] = optionals.map { field =>
      val fullName = field.name.toString.filterNot(_ == ' ')
      val name = TermName(fullName)
      q"""
         if (isDefined(field)) {
           Seq(${name.decodedName.toString} -> value.$name)
         } else Nil
       """
    }


    c.Expr[Writer[A]] {
      q"""
         import json.Json.Converter._
         new Writer[$tpe] {
           def makeJson(value: $tpe): JsonValue = JsonObject(..$validNonOptionals, ..$validOptionals)
         }
       """
    }
  }
}
