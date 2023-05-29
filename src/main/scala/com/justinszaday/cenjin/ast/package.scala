package com.justinszaday.cenjin

import scala.language.implicitConversions

package object ast {

  object AccessSpecifier extends Enumeration {
    type AccessSpecifier = super.Value
    val Private, Protected, Public = super.Value
  }

  import AccessSpecifier._

  trait Declarator extends Node

  trait Expression extends Node

  trait Type extends Node

  trait Preprocessor extends Node

  abstract class ClassLike extends Declarator {
    var name: String
    var fields: List[Member[Declarator]]
    var `extends`: List[Member[Type]]
    var alignas: Option[Expression]
  }

  class Node

  case class Block(var statements: List[Node]) extends Node

  case class Comment(var text: String, var skipSpace: Boolean = false) extends Preprocessor

  case class Define(var symbol: String, var value: Expression) extends Preprocessor

  case class Include(var filename: String, var system: Boolean = true) extends Preprocessor

  case class Pragma(var value: String) extends Preprocessor

  case class Extern(var fields: List[Declarator], var c: Boolean = false) extends Declarator

  case class Namespace(var name: Option[String], var fields: List[Declarator]) extends Declarator

  case class Member[A](accessSpecifier: Option[AccessSpecifier], value: A) extends Node

  case class TemplateArgument(
                               var `type`: Type, // class, typename, etc.
                               var name: Option[String],
                               var default: Option[Expression] = None,
                               var ellipses: Boolean = false,
                               var template: Option[Template] = None
                             ) extends Node

  case class Template(var args: List[TemplateArgument], var target: Declarator) extends Declarator

  case class Value(var `type`: Type, var name: String, var default: Option[Expression] = None) extends Declarator

  case class Function(
                       var returnType: Type,
                       var name: String,
                       var args: List[Value],
                       var body: Option[Block],
                       var arrowReturnType: Boolean = false
                     ) extends Declarator

  case class Class(
                    var name: String,
                    var fields: List[Member[Declarator]],
                    var `extends`: List[Member[Type]],
                    var alignas: Option[Expression] = None
                  ) extends ClassLike

  case class Struct(
                     var name: String,
                     var fields: List[Member[Declarator]],
                     var `extends`: List[Member[Type]],
                     var alignas: Option[Expression] = None
                   ) extends ClassLike

  case class Union(
                    var name: String,
                    var fields: List[Member[Declarator]],
                    var `extends`: List[Member[Type]],
                    var alignas: Option[Expression] = None
                  ) extends ClassLike

  // Enables using text in place of most nodes.
  case class Text(var text: String) extends Node with Declarator with Expression with Preprocessor with Type

  implicit def string2text(text: String): Text = Text(text)
}
