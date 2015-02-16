/*
 * Copyright (C) 2015 Inderjeet Singh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.singhinderjeet.json2java;

import java.io.IOException;

/**
 * This represents the field of a class.
 *
 * @author Inderjeet Singh
 */
public class ClassField {

  private final String name;
  private final String type;
  private final String indent;

  public ClassField(String name, String type, String indent) {
    this.name = name;
    this.type = type;
    this.indent = indent;
  }

  public void appendtDeclaration(Appendable appendable, int indentLevel) throws IOException {
    for (int i = 0; i < indentLevel; ++i) appendable.append(indent);
    appendable.append("private final " + type + " " + name + ";\n");
  }

  public void appendAccessorMethods(Appendable appendable, int indentLevel) throws IOException {
    for (int i = 0; i < indentLevel; ++i) appendable.append(indent);
    appendable.append("public " + type + " get" + Utils.firstLetterUpperCase(name) + "() {\n");
    for (int i = 0; i < indentLevel + 1; ++i) appendable.append(indent);
    appendable.append("return " + name + ";\n");
    for (int i = 0; i < indentLevel; ++i) appendable.append(indent);
    appendable.append("}\n");
  }
}
