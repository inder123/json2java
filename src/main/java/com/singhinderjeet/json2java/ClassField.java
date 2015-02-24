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
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.singhinderjeet.json2java.CustomMappings.MappedFieldName;

/**
 * Definition of a class field. We assume it to be private final.
 *
 * @author Inderjeet Singh
 */
public class ClassField {

  private static final class InferredType {
    final String type;
    final boolean isArrayType;
    public InferredType(String type, boolean isArrayType) {
      this.type = type;
      this.isArrayType = isArrayType;
    }
  }
  private final String jsonName;
  private final Set<JsonElement> jsonValues = new HashSet<>();
  private String fieldName;
  private String type;
  private boolean mappedType;
  private boolean isArrayType;

  public ClassField(String jsonName, JsonElement jsonValue, String type, boolean isArrayType) {
    this.jsonName = jsonName;
    this.jsonValues.add(jsonValue);
    this.type = type;
    this.isArrayType = isArrayType;
  }

  public void merge(ClassField other) {
    if (this.jsonName.equals(other.jsonName)) {
      this.jsonValues.addAll(other.jsonValues);
    }
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
      this.mappedType = true;
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
    InferredType inferredType = inferType(jsonValues, type, isArrayType);
    StringBuilder sb = new StringBuilder();
    if (inferredType.isArrayType) sb.append("List<");
    sb.append(inferredType.type);
    if (inferredType.isArrayType) sb.append(">");
    return sb.toString();
  }

  /**
   * Based on the seen json values, infer a type for this field.
   * Strings are mapped to String. numbers are preferred mapped to ints, then longs and finally as doubles.
   */
  private InferredType inferType(Iterable<JsonElement> jsonValues, String type, boolean isArrayType) {
    if (mappedType) return new InferredType(type, isArrayType);
    InferredType inferredType = new InferredType(type, isArrayType);
    for (JsonElement jsonValue : jsonValues) {
      if (jsonValue instanceof JsonPrimitive) {
        JsonPrimitive primitive = jsonValue.getAsJsonPrimitive();
        if (isBooleanValue(primitive)) {
          inferredType = new InferredType("boolean", false);
        } else if (primitive.isString()) {
          inferredType = new InferredType("String", false);
        } else if (primitive.isNumber()) {
          double number = primitive.getAsDouble();
          boolean isWholeNumber = number - Math.ceil(number) == 0;
          if (isWholeNumber) { // int is preferred over long so look for that
            long longValue = (long) number;
            boolean isLargerThanInt = longValue > Integer.MAX_VALUE || longValue < Integer.MIN_VALUE;
            if (isLargerThanInt && !inferredType.type.equals("double")) { // some other value was a floating point
              inferredType = new InferredType("long", false);
            } else { // some other jsonValue was big enough to fit in long
              if (!inferredType.equals("long") && !inferredType.equals("double")) {
                inferredType = new InferredType("int", false);
              }
            }
          } else { // double is preferred over float
            inferredType = new InferredType("double", false);
          }
        }
      } else if (jsonValue instanceof JsonArray) {
        this.isArrayType = true;
        inferredType = new InferredType(inferType(jsonValue.getAsJsonArray(), type, false).type, true);
      }
    }
    return inferredType;
  }

  private boolean isBooleanValue(JsonPrimitive primitive) {
    boolean isBoolean = primitive.isBoolean();
    if (!isBoolean && primitive.isString()) {
      String value = primitive.getAsString();
      isBoolean = value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
    }
    return isBoolean;
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

  public void appendParameterName(Appendable appendable) throws IOException {
    appendable.append(getFieldName());
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
