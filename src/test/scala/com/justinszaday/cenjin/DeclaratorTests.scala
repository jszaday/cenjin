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

  test("empty function with one argument") {
    val f = fixture
    val foo = Function("void", "foo", List(Value("int", "bar", None)), None)
    f.visit(foo) shouldEqual "void foo(int bar)"
  }

  test("empty function with multiple arguments") {
    val f = fixture
    val foo = Function("void", "foo", List(Value("int", "bar", None), Value("int", "baz", None)), None)
    f.visit(foo) shouldEqual "void foo(int bar, int baz)"
  }

  test("empty function with no arguments and arrow return type") {
    val f = fixture
    val foo = Function("int", "foo", List(), None, arrowReturnType = true)
    f.visit(foo) shouldEqual "auto foo() -> int"
  }
}
