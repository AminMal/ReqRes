import macros.Macros

object JsonDefault extends App {

  import json.Json._
  import language.experimental.macros
  def writer[A]: Writer[A] = macro Macros.writerImpl[A]
}
