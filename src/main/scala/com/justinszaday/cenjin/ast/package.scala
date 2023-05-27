package com.justinszaday.cenjin

import scala.language.implicitConversions

package object ast {

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

  import AccessSpecifier._

  case class Block(var statements: List[Node]) extends Node

  case class Comment(var text: String, var skipSpace: Boolean = false) extends Preprocessor

  case class Define(var symbol: String, var value: Expression) extends Preprocessor

  case class Include(var filename: String, var system: Boolean = true) extends Preprocessor

  case class Pragma(var value: String) extends Preprocessor

  case class Extern(var fields: List[Declarator], var c: Boolean = false) extends Declarator

  case class Namespace(var fields: List[Declarator]) extends Declarator

  case class Member[A](accessSpecifier: Option[AccessSpecifier], value: A) extends Node

  case class Template(var args: Either[Expression, Type], var target: Declarator) extends Declarator

  case class Value(var `type`: Type, var name: String, var default: Option[Expression]) extends Declarator

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
                    var alignas: Option[Expression]
                  ) extends ClassLike

  case class Struct(
                     var name: String,
                     var fields: List[Member[Declarator]],
                     var `extends`: List[Member[Type]],
                     var alignas: Option[Expression]
                   ) extends ClassLike

  case class Union(
                    var name: String,
                    var fields: List[Member[Declarator]],
                    var `extends`: List[Member[Type]],
                    var alignas: Option[Expression]
                  ) extends ClassLike

  // Enables using text in place of most nodes.
  case class Text(var text: String) extends Node with Declarator with Expression with Preprocessor with Type

  object AccessSpecifier extends Enumeration {
    type AccessSpecifier = super.Value
    val Private, Protected, Public = super.Value
  }

  implicit def string2text(text: String): Text = Text(text)
}
