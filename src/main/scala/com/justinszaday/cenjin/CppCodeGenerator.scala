package com.justinszaday.cenjin

import com.justinszaday.cenjin.CppCodeGenerator.Context
import com.justinszaday.cenjin.ast._

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
    s"${visitType(value.`type`)} ${value.name}" + (value.default match {
      case Some(node: Expression) => s" = ${visitExpression(node)}"
      case None => ""
    })
  }

  override def visitComment(comment: Comment)(implicit ctx: Context): String = {
    s"//${if (comment.skipSpace) "" else " "}${comment.text}"
  }

  override def visitDefine(define: Define)(implicit ctx: Context): String = {
    s"#define ${define.symbol} ${visitExpression(define.value)}"
  }

  override def visitInclude(include: Include)(implicit ctx: Context): String = {
    "#include " + (if (include.system) {
      s"<${include.filename}>"
    } else {
      s"\"${include.filename}\""
    })
  }

  override def visitPragma(pragma: Pragma)(implicit ctx: Context): String = s"#pragma ${pragma.value}"

  override def visitBlock(block: Block)(implicit ctx: Context): String = ???

  override def visitExtern(extern: Extern)(implicit ctx: Context): String = {
    "extern " + (if (extern.c) "\"C\" " else "") + (extern.fields match {
      case field :: Nil => visitDeclarator(field)
      case fields => "{\n" + fields.map(visitDeclarator(_) + ";\n").mkString("") + "}"
    })
  }

  override def visitFunction(function: Function)(implicit ctx: Context): String = {
    val returnType = visitType(function.returnType)
    val prefixType = if (function.arrowReturnType) "auto" else returnType
    val postfixType = if (function.arrowReturnType) s" -> $returnType" else ""
    prefixType + " " + function.name + "()" + postfixType
  }
}
