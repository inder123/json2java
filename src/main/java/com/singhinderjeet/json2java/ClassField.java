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

import com.singhinderjeet.json2java.CustomMappings.MappedFieldName;

/**
 * Definition of a class field. We assume it to be private final.
 *
 * @author Inderjeet Singh
 */
public class ClassField {

  private final String jsonName;
  private String fieldName;
  private String type;
  private boolean isArrayType;

  public ClassField(String name, String type, boolean isArrayType) {
    this.jsonName = name;
    this.type = type;
    this.isArrayType = isArrayType;
  }

  public String getJsonName() {
    return jsonName;
  }

  public String getFieldName() {
    // explicitly specified fieldName shouldn't be touched
    String name = fieldName;
    if (fieldName == null) {
      // Check if the first character is upper-case
      char firstChar = jsonName.charAt(0);
      boolean isUpperCase = Character.isUpperCase(firstChar);
      name = isUpperCase ? Character.toLowerCase(firstChar) + jsonName.substring(1) : jsonName;
    }
    return name;
  }

  public boolean needsSerializedNameAnnotation() {
    boolean needAnnotation = jsonName != fieldName;
    if (fieldName == null) { // Or if the first character is uppercase
      char firstChar = jsonName.charAt(0);
      needAnnotation = Character.isUpperCase(firstChar);
    }
    return needAnnotation;
  }

  public void mapType(String type, String mappedType, boolean isArrayType) {
    if (this.type.equals(type)) {
      this.type = mappedType;
      this.isArrayType = isArrayType;
    }
  }

  public void mapFieldName(MappedFieldName mapped) {
    if (mapped.jsonName.equals(jsonName)) {
      this.fieldName = mapped.fieldName;
    }
  }

  public String getTypeName() {
    StringBuilder sb = new StringBuilder();
    if (isArrayType) sb.append("List<");
    sb.append(type);
    if (isArrayType) sb.append(">");
    return sb.toString();
  }
  public void appendtDeclaration(Appendable appendable, int indentLevel, String indent)
      throws IOException {
    for (int i = 0; i < indentLevel; ++i) appendable.append(indent);
    if (needsSerializedNameAnnotation()) {
      appendable.append("@SerializedName(\"").append(jsonName).append("\")\n");
      for (int i = 0; i < indentLevel; ++i) appendable.append(indent);
    }
    appendable.append("private final " + getTypeName() + " " + getFieldName() + ";\n");
  }

  public void appendAccessorMethods(Appendable appendable, int indentLevel, String indent)
      throws IOException {
    for (int i = 0; i < indentLevel; ++i) appendable.append(indent);
    appendable.append("public " + getTypeName() + " get" + Utils.firstLetterUpperCase(getFieldName()) + "() {\n");
    for (int i = 0; i < indentLevel + 1; ++i) appendable.append(indent);
    appendable.append("return " + getFieldName() + ";\n");
    for (int i = 0; i < indentLevel; ++i) appendable.append(indent);
    appendable.append("}\n");
  }

  public void appendParameter(Appendable appendable) throws IOException {
    appendable.append(getTypeName()).append(" ").append(getFieldName());
  }

  public void appendConstructorAssignment(Appendable appendable, int indentLevel, String indent)
      throws IOException {
    for (int i = 0; i < indentLevel; ++i) appendable.append(indent);
    appendable.append("this.").append(getFieldName()).append(" = ").append(getFieldName()).append(";");
  }
}
