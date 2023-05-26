package com.justinszaday.cenjin

import com.justinszaday.cenjin.ast._

object Main {
  def main(args: Array[String]): Unit = {
    val value = Value("int", "x", Some("42"))
    val visitor = new CppCodeGenerator
    implicit val ctx = new CppCodeGenerator.Context
    println(visitor.visit(value))
  }
}
