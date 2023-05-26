package com.justinszaday.cenjin

import com.justinszaday.cenjin.ast._

object Visitor {
    val unrecognized: String = "message"
}

abstract class Visitor[Context, Result] {
    import Visitor._

    def error(node: Node, msg: Option[String])(implicit ctx: Context): Result

    def visit(node: Node)(implicit ctx: Context): Result = {
        node match {
            case declarator: Declarator => visitDeclarator(declarator)
            case expression: Expression => visitExpression(expression)
            case `type`: Type => visitType(`type`)
            case text: Text => visitText(text)
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
            case classLike: ClassLike => visitClassLike(classLike)
            case value: Value => visitValue(value)
            case text: Text => visitText(text)
        }
    }

    def visitClassLike(classLike: ClassLike)(implicit ctx: Context): Result = {
        classLike match {
            case `class`: Class => visitClass(`class`)
            case struct: Struct => visitStruct(struct)
            case union: Union => visitUnion(union)
        }
    }

    def visitClass(`class`: Class)(implicit ctx: Context): Result
    def visitStruct(struct: Struct)(implicit ctx: Context): Result
    def visitUnion(union: Union)(implicit ctx: Context): Result

    def visitExpression(expression: Expression)(implicit ctx: Context): Result = {
        expression match {
            case text: Text => visitText(text)
        }
    }

    def visitText(text: Text)(implicit ctx: Context): Result
    def visitValue(value: Value)(implicit ctx: Context): Result
}
