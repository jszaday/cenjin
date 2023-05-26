package com.justinszaday.cenjin

import scala.language.implicitConversions

package object ast {
  class Node

  trait Declarator extends Node

  trait Expression extends Node

  trait Type extends Node

  trait Preprocessor extends Node

  object AccessSpecifier extends Enumeration {
    type AccessSpecifier = super.Value
    val Private, Protected, Public = super.Value
  }

  import AccessSpecifier._

  case class Comment(var text: String, var skipSpace: Boolean = false) extends Preprocessor

  case class Define(var symbol: String, var value: Expression) extends Preprocessor

  case class Include(var filename: String, var system: Boolean = true) extends Preprocessor

  case class Pragma(var value: String) extends Preprocessor

  case class Value(var `type`: Type, var name: String, var default: Option[Expression]) extends Declarator

  case class Member[A](accessSpecifier: Option[AccessSpecifier], value: A) extends Node

  case class Template(var args: Either[Expression, Type], var target: Declarator) extends Declarator

  abstract class ClassLike extends Declarator {
    var name: String
    var fields: List[Member[Declarator]]
    var `extends`: List[Member[Type]]
    var alignas: Option[Expression]
  }

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

  implicit def string2text(text: String): Text = Text(text)
}
