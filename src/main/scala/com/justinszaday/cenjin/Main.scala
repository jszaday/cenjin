package com.justinszaday.cenjin

import com.justinszaday.cenjin.ast._
import com.justinszaday.cenjin.ast.cpp._
import com.justinszaday.cenjin.ast.cpp.declarators._
import com.justinszaday.cenjin.ast.cpp.preprocessors._
import com.justinszaday.cenjin.ast.cpp.types._

object Main {
  def main(args: Array[String]): Unit = {
    val visitor = new CppCodeGenerator
    implicit val ctx: CppCodeGenerator.Context = new CppCodeGenerator.Context

    val nodes = List(
      Include("cstdio", system = true), // => #include <cstdio>
      Value("int", "x", Some("42")) // => int x = 42
    )

    for (node <- nodes) {
      println(visitor.visit(node))
    }
  }
}
