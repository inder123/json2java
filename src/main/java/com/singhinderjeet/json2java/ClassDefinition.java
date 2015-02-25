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
  private boolean generateFile;
  private ClassDefinition baseClass;

  public ClassDefinition(String pkg, String className) {
    this.pkg = pkg;
    this.className = className;
    this.generateFile = !pkg.startsWith("java");
  }

  public void setBaseClass(ClassDefinition baseClass) {
    this.baseClass = baseClass;
  }

  public void setGenerateFile(boolean generate) {
    this.generateFile = generate;
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
    this.imports.addAll(other.imports);
    if (this.baseClass != null) {
      this.baseClass.merge(other.baseClass);
    } else {
      this.baseClass = other.baseClass;
    }
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

  public int getFieldsCount() {
    int count = fields.size();
    if (baseClass != null) {
      count += baseClass.getFieldsCount();
    }
    return count;
  }

  public ClassDefinition getBaseClass() {
    return baseClass;
  }

  public boolean present(String fieldJsonName) {
    return find(fieldJsonName) != null;
  }

  public ClassField find(String fieldJsonName) {
    for (ClassField field : fields) {
      if (field.getJsonName().equals(fieldJsonName)) return field;
    }
    if (baseClass != null) {
      return baseClass.find(fieldJsonName);
    }
    return null;
  }

  public boolean isSame(ClassDefinition other) {
    return this.className.equals(other.className);
  }

  public void mapFieldName(MappedFieldName mapping) {
    ClassField field = find(mapping.jsonName);
    if (field != null) field.mapFieldName(mapping);
  }

  public void writeClassFile(File dir, String indent, String copyrightNotice,
      String classComment) throws IOException {
    if (!generateFile) return;
    dir = new File(dir, pkg.replaceAll("\\.", File.separator));
    dir.mkdirs();
    updateImports();
    File classFile = new File(dir, className + ".java");
    try (Writer writer = new FileWriter(classFile)) {
      System.out.println("Writing " + classFile.getAbsolutePath());
      writeClassFile(writer, indent, copyrightNotice, classComment);
    }
  }

  void writeClassFile(Writer writer, String indent, String copyrightNotice,
      String classComment) throws IOException {
    if (copyrightNotice != null) writer.append(copyrightNotice);
    writer.append("package " + pkg + ";\n");
    writeImports(writer);
    if (classComment != null) {
      classComment = classComment.replaceAll("\\$className", className);
      writer.append("\n").append(classComment);
    }
    writer.append("public class ").append(className);
    if (baseClass != null) writer.append(" extends ").append(baseClass.getClassName());
    writer.append(" {\n");
    writeFieldDeclarations(writer, indent);
    writeConstructor(writer, indent);
    writeAccessorMethods(writer, indent);
    writer.append("}\n");
  }

  private void updateImports() {
    boolean needSerializedNameImport = false;
    List<ClassField> fields = this.fields;
    needSerializedNameImport = importClasses(fields);
    if (baseClass != null) {
      needSerializedNameImport |= importClasses(baseClass.fields);
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

  private boolean importClasses(List<ClassField> fields) {
    boolean needSerializedNameImport = false;
    for (ClassField field : fields) {
      if (field.getTypeName().equals("Date")) {
        addImport("java.util.Date");
      }
      if (field.needsSerializedNameAnnotation()) {
        needSerializedNameImport = true;
      }
    }
    return needSerializedNameImport;
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
        if (!isBaseClassField(field)) {
            field.appendtDeclaration(writer, 1, indent);
        }
    }
  }

  private boolean isBaseClassField(ClassField field) {
    return isBaseClassField(field.getJsonName());
  }

  boolean isBaseClassField(String fieldJsonName) {
    return baseClass != null && baseClass.find(fieldJsonName) != null;
  }

  void writeConstructor(Writer writer, String indent) throws IOException {
      writer.append("\n").append(indent);
      writer.append("public " + className + "(");
    boolean first = true;
    if (baseClass != null) {
      first = baseClass.appendParameters(writer, first);
    }
    first = appendParameters(writer, first);
    writer.append(") {\n");
    if (baseClass != null) {
      baseClass.appendSuperCallInSubclass(writer, 2, indent);
    }
    first = true;
    for (ClassField field : fields) {
      if (isBaseClassField(field)) continue;
      if (first) first = false; else writer.append("\n");
      field.appendConstructorAssignment(writer, 2, indent);
    }
    writer.append("\n").append(indent).append("}\n");
  }

  private boolean appendParameters(Writer writer, boolean first) throws IOException {
    for (ClassField field : fields) {
      if (isBaseClassField(field)) continue;
      if (first) first = false; else writer.append(", ");
      field.appendParameter(writer);
    }
    return first;
  }

  private void appendSuperCallInSubclass(Writer writer, int indentLevel, String indent)
      throws IOException {
    if (fields.size() == 0) return;
    for (int i = 0; i < indentLevel; ++i) writer.append(indent);
    writer.append("super(");
    boolean first = true;
    for (ClassField field : fields) {
      if (first) first = false; else writer.append(", ");
      field.appendParameterName(writer);
    }
    writer.append(");\n");
  }

  private void writeAccessorMethods(Writer writer, String indent) throws IOException {
    for (ClassField field : fields) {
      if (isBaseClassField(field)) continue;
      writer.append("\n");
      field.appendAccessorMethods(writer, 1, indent);
    }
  }

  @Override
  public String toString() {
    return "ClassDefinition [pkg=" + pkg + ", className=" + className + ", imports=" + imports
        + ", fields=" + fields + ", generateFile=" + generateFile + ", baseClass=" + baseClass
        + "]";
  }
}
