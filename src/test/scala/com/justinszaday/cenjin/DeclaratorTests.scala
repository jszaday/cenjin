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

  test("nested namespaces") {
    val f = fixture
    val baz = Namespace(Some("baz"), Nil)
    val bar = Namespace(Some("bar"), List(baz))
    val foo = Namespace(Some("foo"), List(bar))
    f.visit(foo) shouldEqual
      """namespace foo {
        |  namespace bar {
        |    namespace baz {}
        |  }
        |}""".stripMargin
  }

  test("namespace alias") {
    val f = fixture
    val foo = NamespaceAlias("foo", "bar")
    f.visit(foo) shouldEqual "namespace foo = bar"
  }

  test("extern with multiple members and linkage") {
    val f = fixture
    val foo = Function("void", "foo", Nil, None)
    val bar = Function("void", "bar", Nil, None)
    val extern = Extern(List(foo, bar), Some("C"))
    f.visit(extern) shouldEqual
      """extern "C" {
        |  void foo();
        |  void bar();
        |}""".stripMargin
  }

  test("extern with multiple members must have linkage") {
    val f = fixture
    val foo = Function("void", "foo", Nil, None)
    val bar = Function("void", "bar", Nil, None)
    val extern = Extern(List(foo, bar))
    assertThrows[IllegalArgumentException] {
      f.visit(extern)
    }
  }

  test("extern with one member") {
    val f = fixture
    val foo = Function("void", "foo", Nil, None)
    val extern = Extern(List(foo))
    f.visit(extern) shouldEqual "extern void foo()"
  }

  test("extern with linkage and one member") {
    val f = fixture
    val foo = Function("void", "foo", Nil, None)
    val extern = Extern(List(foo), Some("C"))
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

  test("template parameter (trivial)") {
    val f = fixture
    val arg = TemplateArgument("typename", Some("T"))
    f.visit(arg) shouldEqual "typename T"
  }

  test("template parameter with ellipses") {
    val f = fixture
    val arg = TemplateArgument("typename", Some("Ts"), ellipses = true)
    f.visit(arg) shouldEqual "typename... Ts"
  }

  test("template parameter with default") {
    val f = fixture
    val arg = TemplateArgument("size_t", Some("N"), default = Some("42"))
    f.visit(arg) shouldEqual "size_t N = 42"
  }

  test("template parameter with default and no name") {
    val f = fixture
    val arg = TemplateArgument("class", None, default = Some("void"))
    f.visit(arg) shouldEqual "class = void"
  }

  test("template class with no members") {
    val f = fixture
    val arg = TemplateArgument("typename", Some("T"))
    val `class` = Class("Array", Nil, Nil)
    val template = Template(List(arg), `class`)
    f.visit(template) shouldEqual
      """template <typename T>
        |class Array {};""".stripMargin
  }
}
