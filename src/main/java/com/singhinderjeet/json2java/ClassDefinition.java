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
import java.util.List;

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

  public ClassDefinition(String pkg, String className) {
    this.pkg = pkg;
    this.className = className;
  }

  public void addField(ClassField classField) {
    if (!present(classField.getName())) fields.add(classField);
  }

  public void addImport(String importedClass) {
    if (!imports.contains(importedClass)) {
      imports.add(importedClass);
    }
  }

  public void mapType(String type, String mappedType) {
    for (ClassField field : fields) {
      field.mapType(type, mappedType);
    }
  }

  /** Copies the additional fields of other that are not present in self */
  public void merge(ClassDefinition other) {
    if (other == null) return;
    for (ClassField field : other.fields) {
      if (!present(field.getName())) {
        addField(field);
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

  public boolean present(String fieldName) {
    for (ClassField field : fields) {
      if (field.getName().equals(fieldName)) return true;
    }
    return false;
  }

  public boolean isSame(ClassDefinition other) {
    return this.className.equals(other.className);
  }

  public void writeClassFile(File dir, String indent) throws IOException {
    dir = new File(dir, pkg.replaceAll("\\.", File.separator));
    dir.mkdirs();
    File classFile = new File(dir, className + ".java");
    try (Writer writer = new FileWriter(classFile)) {
      System.out.println("Writing " + classFile.getAbsolutePath());
      writer.append("package " + pkg + ";\n");
      writeImports(writer);
      writer.append("\n").append("public class " + className + " {\n");
      writeFieldDeclarations(writer, indent);
      writeAccessorMethods(writer, indent);
      writer.append("}\n");
    }
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

  private void writeAccessorMethods(Writer writer, String indent) throws IOException {
    for (ClassField field : fields) {
      writer.append("\n");
      field.appendAccessorMethods(writer, 1, indent);
    }
  }
}
