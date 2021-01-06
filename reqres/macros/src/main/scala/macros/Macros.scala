package macros

import scala.reflect.macros.blackbox

class Macros(val c: blackbox.Context) {
  import c.universe._
  import json.Json.Converter._
  import json.Json._

  def defaultWriterImpl[T](implicit atag: c.WeakTypeTag[T]): c.Expr[Writer[T]] = {
    val tpe: Type = weakTypeOf[T]
    val fields: List[String] = tpe.decls.filter(_.isTerm).filter(_.isPrivateThis).map(_.name.toString.filterNot(_ == ' ')).toList

    println("fields of class: \"" + tpe.typeSymbol.name + "\" are:\n" + fields.mkString(" "))

    val dealiased = atag.tpe.dealias
    println("dealiased: ")
    println(dealiased.members.filter(_.isPrivateThis).map(_.name.toString.filterNot(_ == ' ')))

    reify {
      (_: T) => JsonObject("name" -> 2, "family" -> "amin")
    }
  }

  def testMacroImpl[T : c.WeakTypeTag](content: c.Expr[T]): c.Expr[Unit] = {
    val u = c.universe

    val d: String = show(content.actualType)
    val f: String = show(content)
    println(d)
    println(f)
    reify {
      println("content: " + content.splice)
//      println("tree: " + content.value)
    }
  }

}
