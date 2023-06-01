package com.justinszaday.cenjin

import com.justinszaday.cenjin.ast._
import com.justinszaday.cenjin.ast.cpp._
import org.scalatest.funsuite.AnyFunSuite

abstract class UnitFunSuite extends AnyFunSuite {
  def fixture = new Fixture

  class Fixture {
    val codeGenerator = new CppCodeGenerator
    val ctx = new CppCodeGenerator.Context

    def visit(node: Node): String = {
      codeGenerator.visit(node)(ctx)
    }
  }
}
