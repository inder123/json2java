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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.singhinderjeet.json2java.CustomMappings.MappedFieldName;

/**
 * Definition of a single class as derived from JSON data.
 *
 * @author Inderjeet Singh
 */
public class ClassDefinition {

  private final String pkg;
  private String className;
  private final List<String> imports = new ArrayList<>();
  private final List<ClassField> fields = new ArrayList<>();
  private final boolean generateFile;

  public ClassDefinition(String pkg, String className) {
    this.pkg = pkg;
    this.className = className;
    this.generateFile = !pkg.startsWith("java");
  }

  public void addField(ClassField classField) {
    if (!present(classField.getJsonName())) fields.add(classField);
  }

  public void addImport(String importedClass) {
    if (!imports.contains(importedClass)) {
      imports.add(importedClass);
    }
  }

  public void mapType(String type, String mappedType, boolean isArrayType) {
    for (ClassField field : fields) {
      field.mapType(type, mappedType, isArrayType);
    }
  }

  /** Copies the additional fields of other that are not present in self */
  public void merge(ClassDefinition other) {
    if (other == null) return;
    for (ClassField field : other.fields) {
      ClassField existing = find(field.getJsonName());
      if (existing == null) {
        addField(field);
      } else {
        existing.merge(field);
      }
    }
  }

  public void rename(String mappedType) {
    this.className = mappedType;
  }

  public String getPackage() {
    return pkg;
  }

  public String getClassName() {
    return className;
  }

  public boolean present(String fieldJsonName) {
    return find(fieldJsonName) != null;
  }

  public ClassField find(String fieldJsonName) {
    for (ClassField field : fields) {
      if (field.getJsonName().equals(fieldJsonName)) return field;
    }
    return null;
  }

  public boolean isSame(ClassDefinition other) {
    return this.className.equals(other.className);
  }

  public void mapFieldName(MappedFieldName mapping) {
    ClassField field = find(mapping.jsonName);
    field.mapFieldName(mapping);
  }

  public void writeClassFile(File dir, String indent) throws IOException {
    if (!generateFile) return;
    updateImports();
    dir = new File(dir, pkg.replaceAll("\\.", File.separator));
    dir.mkdirs();
    File classFile = new File(dir, className + ".java");
    try (Writer writer = new FileWriter(classFile)) {
      System.out.println("Writing " + classFile.getAbsolutePath());
      writer.append("package " + pkg + ";\n");
      writeImports(writer);
      writer.append("\n").append("public class " + className + " {\n");
      writeFieldDeclarations(writer, indent);
      writeConstructor(writer, indent);
      writeAccessorMethods(writer, indent);
      writer.append("}\n");
    }
  }

  private void updateImports() {
    boolean needSerializedNameImport = false;
    for (ClassField field : fields) {
      if (field.getTypeName().equals("Date")) {
        addImport("java.util.Date");
      }
      if (field.needsSerializedNameAnnotation()) {
        needSerializedNameImport = true;
      }
    }
    if (needSerializedNameImport) {
      addImport("com.google.gson.annotations.SerializedName");
    }
    // remove duplicates
    Set<String> set = new HashSet<>();
    set.addAll(imports);
    imports.clear();
    imports.addAll(set);
    Collections.sort(imports);
  }

  private void writeImports(Writer writer) throws IOException {
    if (imports.size() > 0) writer.append("\n");
    for (String importedClass : imports) {
      writer.append("import ").append(importedClass).append(";\n");
    }
  }

  private void writeFieldDeclarations(Writer writer, String indent) throws IOException {
    writer.append("\n");
    for (ClassField field : fields) {
      field.appendtDeclaration(writer, 1, indent);
    }
  }

  private void writeConstructor(Writer writer, String indent) throws IOException {
    writer.append("\n").append(indent);
    writer.append("public " + className + "(");
    boolean first = true;
    for (ClassField field : fields) {
      if (first) first = false; else writer.append(", ");
      field.appendParameter(writer);
    }
    writer.append(") {\n");
    first = true;
    for (ClassField field : fields) {
      if (first) first = false; else writer.append("\n");
      field.appendConstructorAssignment(writer, 2, indent);
    }
    writer.append("\n").append(indent).append("}\n");
  }

  private void writeAccessorMethods(Writer writer, String indent) throws IOException {
    for (ClassField field : fields) {
      writer.append("\n");
      field.appendAccessorMethods(writer, 1, indent);
    }
  }
}
