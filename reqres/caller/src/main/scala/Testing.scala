import macros.Macros

object Testing extends App {

  import json.Json._
  import language.experimental.macros
  def defaultWriter[T]: Writer[T] = macro Macros.defaultWriterImpl[T]
  def unitMacro[T](content: T): Unit = macro Macros.testMacroImpl[T]
}
