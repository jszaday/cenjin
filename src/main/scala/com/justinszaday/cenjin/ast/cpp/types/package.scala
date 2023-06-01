package com.justinszaday.cenjin.ast.cpp

import com.justinszaday.cenjin.ast.cpp.Type

package object types {
  case class ArrayOf(
      var subtype: Type,
      val count: Option[Expression] = None
  ) extends Type

  case class Const(var subtype: Type) extends Type

  case class Pointer(var subtype: Type) extends Type

  case class Reference(var subtype: Type) extends Type
}
