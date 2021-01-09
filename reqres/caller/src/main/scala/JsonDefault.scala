import macros.Macros

object JsonDefault extends App {

  import json.Json._
  import language.experimental.macros
//  def defaultWriter[T]: Writer[T] = macro Macros.defaultWriterImpl[T]
  def writer[A]: Writer[A] = macro Macros.mWriteImpl[A]
  def optionalWriter[A]: Writer[A] = macro Macros.optionalWriterImpl[A]
}
