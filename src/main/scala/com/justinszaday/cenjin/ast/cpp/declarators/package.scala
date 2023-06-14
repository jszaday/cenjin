package com.justinszaday.cenjin.ast.cpp

import com.justinszaday.cenjin.ast.cpp.Declarator
import com.justinszaday.cenjin.ast.cpp.Expression
import com.justinszaday.cenjin.ast.cpp.Type

package object declarators {

  object AccessSpecifier extends Enumeration {
    type AccessSpecifier = AccessSpecifier.Value
    val Private = AccessSpecifier.Value("private")
    val Protected = AccessSpecifier.Value("protected")
    val Public = AccessSpecifier.Value("public")
  }

  import AccessSpecifier._

  abstract class ClassLike extends Declarator {
    var name: String
    var fields: List[Member[Declarator]]
    var `extends`: List[Member[Type]]
    var alignas: Option[Expression]
  }

  case class Extern(
      var fields: List[Declarator],
      var linkage: Option[String] = None
  ) extends Declarator

  case class NamespaceAlias(var aliasName: String, var nsName: String)
      extends Declarator

  case class Namespace(var name: Option[String], var fields: List[Declarator])
      extends Declarator

  case class Member[+A <: Node](
      accessSpecifier: Option[AccessSpecifier.Value],
      value: A
  ) extends Node

  case class TemplateArgument(
      var `type`: Type, // class, typename, etc.
      var name: Option[String],
      var default: Option[Expression] = None,
      var ellipses: Boolean = false,
      var template: Option[Template] = None
  ) extends Node

  case class Template(var args: List[TemplateArgument], var target: Declarator)
      extends Declarator

  case class Value(
      var `type`: Type,
      var name: String,
      var default: Option[Expression] = None
  ) extends Declarator

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
}
