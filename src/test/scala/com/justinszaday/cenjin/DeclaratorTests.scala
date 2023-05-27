package com.justinszaday.cenjin

import com.justinszaday.cenjin.ast._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class DeclaratorTests extends AnyFunSuite {
  def fixture = new Fixture

  class Fixture {
    val codeGenerator = new CppCodeGenerator
    val ctx = new CppCodeGenerator.Context

    def visit(node: Node): String = {
      codeGenerator.visit(node)(ctx)
    }
  }

  test("extern with multiple members") {
    val f = fixture
    val foo = Function("void", "foo", Nil, None)
    val bar = Function("void", "bar", Nil, None)
    val extern = Extern(List(foo, bar))
    f.visit(extern) shouldEqual
      """extern {
        |void foo();
        |void bar();
        |}""".stripMargin
  }

  test("extern with one member") {
    val f = fixture
    val foo = Function("void", "foo", Nil, None)
    val extern = Extern(List(foo))
    f.visit(extern) shouldEqual "extern void foo()"
  }

  test("extern c with one member") {
    val f = fixture
    val foo = Function("void", "foo", Nil, None)
    val extern = Extern(List(foo), c = true)
    f.visit(extern) shouldEqual "extern \"C\" void foo()"
  }
}
