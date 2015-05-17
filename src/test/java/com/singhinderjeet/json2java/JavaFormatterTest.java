package com.singhinderjeet.json2java;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JavaFormatterTest {

  @Test
  public void test() throws Exception {
    JavaFormatter formatter = new JavaFormatter(10, "  ");
    assertEquals("private final class A {\n}\n",
        formatter.format("private final class A {}"));
  }

  @Test
  public void testParametersAreWrapped() throws Exception {
    JavaFormatter formatter = new JavaFormatter(10, "  ");
    assertEquals("private final void method(\n    int a, int b, int c);\n\n",
        formatter.format("private final void method(int a, int b, int c);"));
  }
  @Test
  public void testLineComment() throws Exception {
    JavaFormatter formatter = new JavaFormatter(50, "  ");
    assertEquals("// comment line 1\n// comment line 2\n",
        formatter.format("// comment line 1\n// comment line 2\n"));
  }

  @Test
  public void testCopyrightNoticeIsUnmodified() throws Exception {
    JavaFormatter formatter = new JavaFormatter(100, "  ");
    String copyright = "/*\n"
    + " * Copyright (C) 2015 Inderjeet Singh\n"
    + " *\n"
    + " * Licensed under the Apache License, Version 2.0 (the \"License\");\n"
    + " * you may not use this file except in compliance with the License.\n"
    + " * You may obtain a copy of the License at\n"
    + " *\n"
    + " * http://www.apache.org/licenses/LICENSE-2.0\n"
    + " */\n";
    assertEquals(copyright, formatter.format(copyright));
  }
}
