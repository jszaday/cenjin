# cenjin
Work in progress C/C++ Code Generation Library for Scala; many TODO's remain:
- Enclose this library within a proper package (bazel, etc.).
- Add unit tests.
- Write documentation or, better yet, actually document code while writing it.
- Wrap up support for all C/C++ lang features.

Goal: be as to Scala as [cgen](https://documen.tician.de/cgen/index.html) is to Python.

Short-term non-goals:
- To parse C/C++ code and produce an AST; while feasible, not immediately practical.
- To have a restricted, C-only code generator; C++ is the focus; treat C as a subset.
- To implement a "concrete" syntax tree that preserves whitespace (a la Python's [libcst](https://libcst.readthedocs.io/en/latest)).
- To support various whitespace output formats (Google, LLVM, etc.); use [clang-format](https://clang.llvm.org/docs/ClangFormat.html).
