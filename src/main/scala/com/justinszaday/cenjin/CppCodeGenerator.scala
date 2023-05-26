package com.justinszaday.cenjin

import com.justinszaday.cenjin.ast._
import com.justinszaday.cenjin.CppCodeGenerator.Context

object CppCodeGenerator {
  class Context
}

class CppCodeGenerator extends Visitor[Context, String] {
  override def error(node: Node, msg: Option[String])(implicit ctx: Context): String = ???

  override def visitClass(`class`: Class)(implicit ctx: Context): String = ???

  override def visitStruct(struct: Struct)(implicit ctx: Context): String = ???

  override def visitUnion(union: Union)(implicit ctx: Context): String = ???

  override def visitText(text: Text)(implicit ctx: Context): String = text.text

  override def visitValue(value: Value)(implicit ctx: Context): String = {
    s"${visitType(value.`type`)} ${value.name}" + (
      value.default match {
        case Some(node: Expression) => s" = ${visitExpression(node)}"
        case None => ""
      }
    )
  }
}
