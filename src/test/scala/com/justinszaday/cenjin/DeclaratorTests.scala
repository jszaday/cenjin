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

  test("simple typename") {
    val f = fixture
    val arg = TemplateArgument("typename", Some("T"))
    f.visit(arg) shouldEqual "typename T"
  }

  test("simple typename with ellipses") {
    val f = fixture
    val arg = TemplateArgument("typename", Some("Ts"), ellipses = true)
    f.visit(arg) shouldEqual "typename... Ts"
  }

  test("simple typename with default") {
    val f = fixture
    val arg = TemplateArgument("size_t", Some("N"), default = Some("42"))
    f.visit(arg) shouldEqual "size_t N = 42"
  }

  test("simple typename with default and no name") {
    val f = fixture
    val arg = TemplateArgument("class", None, default = Some("void"))
    f.visit(arg) shouldEqual "class = void"
  }
}
