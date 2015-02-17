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
import java.io.Writer;

/**
 * Definition of a class field. We assume it to be private final.
 *
 * @author Inderjeet Singh
 */
public class ClassField {

  private final String name;
  private String type;
  private final boolean isArrayType;

  public ClassField(String name, String type, boolean isArrayType) {
    this.name = name;
    this.type = type;
    this.isArrayType = isArrayType;
  }

  public String getName() {
    return name;
  }

  public void mapType(String type, String mappedType) {
    if (this.type.equals(type)) {
      this.type = mappedType;
    }
  }

  private String getTypeName() {
    StringBuilder sb = new StringBuilder();
    if (isArrayType) sb.append("List<");
    sb.append(type);
    if (isArrayType) sb.append(">");
    return sb.toString();
  }
  public void appendtDeclaration(Appendable appendable, int indentLevel, String indent)
      throws IOException {
    for (int i = 0; i < indentLevel; ++i) appendable.append(indent);
    appendable.append("private final " + getTypeName() + " " + name + ";\n");
  }

  public void appendAccessorMethods(Appendable appendable, int indentLevel, String indent)
      throws IOException {
    for (int i = 0; i < indentLevel; ++i) appendable.append(indent);
    appendable.append("public " + getTypeName() + " get" + Utils.firstLetterUpperCase(name) + "() {\n");
    for (int i = 0; i < indentLevel + 1; ++i) appendable.append(indent);
    appendable.append("return " + name + ";\n");
    for (int i = 0; i < indentLevel; ++i) appendable.append(indent);
    appendable.append("}\n");
  }

  public void appendParameter(Appendable appendable) throws IOException {
    appendable.append(getTypeName()).append(" ").append(name);
  }

  public void appendConstructorAssignment(Appendable appendable, int indentLevel, String indent)
      throws IOException {
    for (int i = 0; i < indentLevel; ++i) appendable.append(indent);
    appendable.append("this.").append(name).append(" = ").append(name).append(";");
  }
}
