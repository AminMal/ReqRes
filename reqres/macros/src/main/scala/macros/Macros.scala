package macros

import scala.annotation.tailrec
import scala.reflect.macros.blackbox

class Macros(val c: blackbox.Context) {
  import c.universe._
  import json.Json._

  def writerImpl[A : c.WeakTypeTag]: c.Expr[Writer[A]] = {
    val tpe: Type = weakTypeOf[A]

    val params = tpe.decls.filter(_.isPrivateThis)

    def isOptional(sym: Symbol): Boolean = sym.typeSignature <:< weakTypeOf[Option[Any]]

    val optionalParams: Seq[Symbol] = params.filter(isOptional).toSeq
    val nonOptionalParams: Seq[Symbol] = params.filterNot(isOptional).toSeq
    val nonOptionalsTrees: Seq[Tree] = nonOptionalParams.map { field =>
      val fieldFullName = field.name.toString.filterNot(_ == ' ')
      val fieldName = TermName(fieldFullName)
      val key = fieldName.decodedName.toString
      q"$key -> value.$fieldName"
    }


    /** pimping Seq **/
    implicit class SeqOps(seq: Seq[Symbol]) {
      def getSomes: Seq[Tree] = {
        @tailrec
        def iterate(index: Int = 0, acc: Seq[Tree] = Seq()): Seq[Tree] = {
          if (index < 0 || index >= seq.length) acc
          else {
            val fullName: String = seq(index).name.toString.filterNot(_ == ' ')
            val name = TermName(fullName)
            val key: String = name.decodedName.toString
            val currentTree: Tree = {
              q"""
                 if (value.$name.isDefined)
                   $key -> value.$name.get
                 else $key -> JsonNull
               """
            }
            iterate(index + 1, acc :+ currentTree)
          }
        }
        iterate()
      }
    }

    val valueTrees: Seq[Tree] = nonOptionalsTrees ++ optionalParams.getSomes

    c.Expr[Writer[A]] {
      q"""
         import json.Json.Converter._
         new Writer[$tpe] {
           def makeJson(value: $tpe): JsonValue = JsonObject(..$valueTrees)
         }
       """
    }
  }
}
