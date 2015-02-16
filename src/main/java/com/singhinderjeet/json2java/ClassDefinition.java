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
 * This represents a class.
 *
 * @author Inderjeet Singh
 */
public class ClassDefinition {

  private final String rootPackage;
  private final String rootClassName;
  private final List<String> imports = new ArrayList<>();
  private final List<ClassField> fields = new ArrayList<>();

  public ClassDefinition(String rootPackage, String rootClassName) {
    this.rootPackage = rootPackage;
    this.rootClassName = rootClassName;
  }

  public String getRootPackage() {
    return rootPackage;
  }

  public String getRootClassName() {
    return rootClassName;
  }

  public boolean isSame(ClassDefinition other) {
    return this.rootClassName.equals(other.rootClassName);
  }

  public void writeClassFile(File dir, String indent) throws IOException {
    dir = new File(dir, rootPackage.replaceAll("\\.", File.separator));
    dir.mkdirs();
    File classFile = new File(dir, rootClassName + ".java");
    try (Writer writer = new FileWriter(classFile)) {
      System.out.println("Writing " + classFile.getAbsolutePath());
      writer.append("package " + rootPackage + ";\n");
      writeImports(writer);
      writer.append("\n").append("public class " + rootClassName + " {\n");
      writeFieldDeclarations(writer);
      writeAccessorMethods(writer);
      writer.append("}\n");
    }
  }

  private void writeImports(Writer writer) throws IOException {
    if (imports.size() > 0) writer.append("\n");
    for (String importedClass : imports) {
      writer.append("import ").append(importedClass).append(";\n");
    }
  }

  private void writeFieldDeclarations(Writer writer) throws IOException {
    writer.append("\n");
    for (ClassField field : fields) {
      field.appendtDeclaration(writer, 1);
    }
  }

  private void writeAccessorMethods(Writer writer) throws IOException {
    for (ClassField field : fields) {
      writer.append("\n");
      field.appendAccessorMethods(writer, 1);
    }
  }

  public void addField(ClassField classField) {
    fields.add(classField);
  }

  public void addImport(String importedClass) {
    if (!imports.contains(importedClass)) {
      imports.add(importedClass);
    }
  }
}
