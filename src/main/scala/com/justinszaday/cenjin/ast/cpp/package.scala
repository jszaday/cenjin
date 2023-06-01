package com.justinszaday.cenjin.ast

import scala.language.implicitConversions

package object cpp {

  trait Declarator extends Node

  trait Expression extends Node

  trait Type extends Node

  trait Preprocessor extends Node

  class Node

  case class Block(var statements: List[Node]) extends Node

  // Enables using text in place of most nodes.
  case class Text(var text: String)
      extends Node
      with Declarator
      with Expression
      with Preprocessor
      with Type

  implicit def string2text(text: String): Text = Text(text)
}
