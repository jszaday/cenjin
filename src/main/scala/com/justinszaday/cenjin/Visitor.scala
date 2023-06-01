package com.justinszaday.cenjin

import com.justinszaday.cenjin.ast._
import com.justinszaday.cenjin.ast.cpp._
import com.justinszaday.cenjin.ast.cpp.declarators._
import com.justinszaday.cenjin.ast.cpp.preprocessors._
import com.justinszaday.cenjin.ast.cpp.types._

object Visitor {
  val unrecognized: String = "message"
}

abstract class Visitor[Context, Result] {

  import Visitor._

  def error(node: Node, msg: Option[String])(implicit ctx: Context): Result

  def visit(node: Node)(implicit ctx: Context): Result = {
    node match {
      case declarator: Declarator     => visitDeclarator(declarator)
      case preprocessor: Preprocessor => visitPreprocessor(preprocessor)
      case expression: Expression     => visitExpression(expression)
      case ty: Type                   => visitType(ty)
      case block: Block               => visitBlock(block)
      case templateArgument: TemplateArgument =>
        visitTemplateArgument(templateArgument)
      case _ => error(node, Some(unrecognized))
    }
  }

  def visitType(`type`: Type)(implicit ctx: Context): Result = {
    `type` match {
      case text: Text => visitText(text)
    }
  }

  def visitDeclarator(declarator: Declarator)(implicit ctx: Context): Result = {
    declarator match {
      case classLike: ClassLike           => visitClassLike(classLike)
      case extern: Extern                 => visitExtern(extern)
      case function: Function             => visitFunction(function)
      case namespace: Namespace           => visitNamespace(namespace)
      case namespaceAlias: NamespaceAlias => visitNamespaceAlias(namespaceAlias)
      case template: Template             => visitTemplate(template)
      case value: Value                   => visitValue(value)
      case text: Text                     => visitText(text)
    }
  }

  def visitClassLike(classLike: ClassLike)(implicit ctx: Context): Result = {
    classLike match {
      case cls: Class     => visitClass(cls)
      case struct: Struct => visitStruct(struct)
      case union: Union   => visitUnion(union)
    }
  }

  def visitExpression(expression: Expression)(implicit ctx: Context): Result = {
    expression match {
      case text: Text => visitText(text)
    }
  }

  def visitPreprocessor(
      preprocessor: Preprocessor
  )(implicit ctx: Context): Result = {
    preprocessor match {
      case comment: Comment => visitComment(comment)
      case define: Define   => visitDefine(define)
      case include: Include => visitInclude(include)
      case pragma: Pragma   => visitPragma(pragma)
    }
  }

  def visitBlock(block: Block)(implicit ctx: Context): Result

  def visitText(text: Text)(implicit ctx: Context): Result

  def visitExtern(extern: Extern)(implicit ctx: Context): Result

  def visitFunction(function: Function)(implicit ctx: Context): Result

  def visitTemplate(template: Template)(implicit ctx: Context): Result

  def visitTemplateArgument(templateArgument: TemplateArgument)(implicit
      ctx: Context
  ): Result

  def visitValue(value: Value)(implicit ctx: Context): Result

  def visitClass(`class`: Class)(implicit ctx: Context): Result

  def visitStruct(struct: Struct)(implicit ctx: Context): Result

  def visitUnion(union: Union)(implicit ctx: Context): Result

  def visitComment(comment: Comment)(implicit ctx: Context): Result

  def visitDefine(define: Define)(implicit ctx: Context): Result

  def visitInclude(include: Include)(implicit ctx: Context): Result

  def visitPragma(pragma: Pragma)(implicit ctx: Context): Result

  def visitNamespace(namespace: Namespace)(implicit ctx: Context): Result

  def visitNamespaceAlias(namespaceAlias: NamespaceAlias)(implicit
      ctx: Context
  ): Result
}
