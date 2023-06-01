package com.justinszaday.cenjin.ast.cpp

import com.justinszaday.cenjin.ast.cpp.Expression
import com.justinszaday.cenjin.ast.cpp.Preprocessor

package object preprocessors {
  case class Comment(var text: String, var skipSpace: Boolean = false)
      extends Preprocessor

  case class Define(var symbol: String, var value: Expression)
      extends Preprocessor

  case class Include(var filename: String, var system: Boolean = true)
      extends Preprocessor

  case class Pragma(var value: String) extends Preprocessor
}
