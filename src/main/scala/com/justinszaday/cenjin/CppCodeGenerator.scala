package com.justinszaday.cenjin

import com.justinszaday.cenjin.CppCodeGenerator.Context
import com.justinszaday.cenjin.ast.cpp._
import com.justinszaday.cenjin.ast.cpp.declarators._
import com.justinszaday.cenjin.ast.cpp.preprocessors._
import com.justinszaday.cenjin.ast.cpp.types._

import scala.collection.SeqView

object CppCodeGenerator {
  class Context {
    var indentation: Int = 0
    var accessSpecifier: List[AccessSpecifier.Value] = Nil
  }
}

class CppCodeGenerator extends Visitor[Context, String] {
  override def error(node: Node, msg: Option[String])(implicit
      ctx: Context
  ): String = ???

  override def visitClass(`class`: Class)(implicit ctx: Context): String = ???

  override def visitStruct(struct: Struct)(implicit ctx: Context): String = ???

  override def visitUnion(union: Union)(implicit ctx: Context): String = ???

  def visitMember[A <: Node](
      member: Member[A]
  )(implicit ctx: Context): String = {
    val accessSpecifier = member.accessSpecifier match {
      case Some(x) if x != ctx.accessSpecifier.head => {
        ctx.accessSpecifier = x :: ctx.accessSpecifier.tail
        x.toString + ":" + System.lineSeparator + (" " * ctx.indentation)
      }
      case _ => ""
    }
    accessSpecifier + visit(member.value)
  }

  override def visitClassLike(
      classLike: ClassLike
  )(implicit ctx: Context): String = {
    val (keyword, defaultAccessSpecifier) = classLike match {
      case _: Class  => ("class", AccessSpecifier.Private)
      case _: Struct => ("struct", AccessSpecifier.Public)
      case _: Union  => ("union", AccessSpecifier.Public)
    }
    ctx.accessSpecifier = defaultAccessSpecifier :: ctx.accessSpecifier
    // TODO(jszaday): implement the handling for extends
    val alignas = classLike.alignas match {
      case Some(expression) => s"alignas(${visitExpression(expression)}) "
      case None             => ""
    }
    val result =
      s"$keyword $alignas${classLike.name} ${visitBlockLike(classLike.fields.view.map(visitMember))};"
    ctx.accessSpecifier = ctx.accessSpecifier.tail
    result
  }

  override def visitText(text: Text)(implicit ctx: Context): String = text.text

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

  override def visitPragma(pragma: Pragma)(implicit ctx: Context): String =
    s"#pragma ${pragma.value}"

  override def visitExtern(extern: Extern)(implicit ctx: Context): String = {
    if (extern.fields.lengthIs != 1 && extern.linkage.isEmpty) {
      throw new IllegalArgumentException(
        "extern with multiple members must have linkage"
      )
    }

    "extern " + {
      extern.linkage match {
        case Some(value) => s"\"$value\" "
        case None        => ""
      }
    } + {
      extern.fields match {
        case field :: Nil => visitDeclarator(field)
        case fields       => visitBlockLike(fields.view.map(visitDeclarator))
      }
    }
  }

  override def visitFunction(
      function: Function
  )(implicit ctx: Context): String = {
    val returnType = visitType(function.returnType)
    val prefixType = if (function.arrowReturnType) "auto" else returnType
    val postfixType = if (function.arrowReturnType) s" -> $returnType" else ""
    prefixType + " " + function.name + "(" + {
      function.args.map(visitValue).mkString(", ")
    } + ")" + postfixType + {
      function.body match {
        case Some(block) => s" ${visitBlock(block)}"
        case None        => ""
      }
    }
  }

  override def visitNamespace(
      namespace: Namespace
  )(implicit ctx: Context): String = {
    val name = namespace.name match {
      case Some(name) => s" $name"
      case None       => ""
    }
    val fields = namespace.fields.view.map(visitDeclarator)
    s"namespace$name ${visitBlockLike(fields)}"
  }

  override def visitNamespaceAlias(
      namespaceAlias: NamespaceAlias
  )(implicit ctx: Context): String = {
    s"namespace ${namespaceAlias.aliasName} = ${namespaceAlias.nsName}"
  }

  override def visitValue(value: Value)(implicit ctx: Context): String = {
    s"${visitType(value.`type`)} ${value.name}" + (value.default match {
      case Some(node: Expression) => s" = ${visitExpression(node)}"
      case None                   => ""
    })
  }

  override def visitBlock(block: Block)(implicit ctx: Context): String = {
    visitBlockLike(block.statements.view.map(visit))
  }

  def visitBlockLike(lines: SeqView[String])(implicit ctx: Context): String = {
    if (lines.isEmpty) {
      "{}"
    } else {
      ctx.indentation += 2

      val prefix = " " * ctx.indentation
      val suffix = " " * (ctx.indentation - 2)
      val lineSep = System.lineSeparator

      def formatLine(line: String): String = {
        if (line.endsWith("{") || line.endsWith("}") || line.endsWith(";")) {
          s"$prefix$line$lineSep"
        } else {
          s"$prefix$line;$lineSep"
        }
      }

      val result = s"{\n${lines.map(formatLine).mkString("")}$suffix}"

      ctx.indentation -= 2

      result
    }
  }

  override def visitTemplateArgument(
      templateArgument: TemplateArgument
  )(implicit ctx: Context): String = {
    {
      templateArgument.template match {
        case Some(template) => s"${visitTemplate(template)} "
        case None           => ""
      }
    } + visitType(templateArgument.`type`) + {
      if (templateArgument.ellipses) "..." else ""
    } + {
      templateArgument.name match {
        case Some(name) => s" $name"
        case None       => ""
      }
    } + {
      templateArgument.default match {
        case Some(expression) => s" = ${visitExpression(expression)}"
        case None             => ""
      }
    }
  }

  override def visitTemplate(
      template: Template
  )(implicit ctx: Context): String = {
    s"template <${template.args.map(visitTemplateArgument).mkString(", ")}>\n${visitDeclarator(template.target)}"
  }
}
