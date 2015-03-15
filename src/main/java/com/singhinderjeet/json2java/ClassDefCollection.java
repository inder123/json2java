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
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.singhinderjeet.json2java.CustomMappings.MappedFieldName;
import com.singhinderjeet.json2java.CustomMappings.MappedTypeName;

/**
 * A list of class definitions.
 *
 * @author Inderjeet Singh
 */
public class ClassDefCollection {
  private final List<ClassDefinition> classes = Utils.<ClassDefinition>asList(
      new ClassDefinition("java.lang", "String"));
  private String fileCopyrightNotice;
  private String defaultClassComment;

  public void addEnumClass(EnumDefinition enumClass) {
    this.classes.add(enumClass);
  }

  public ClassDefinition addClassDefinition(String pkg, String className) {
    ClassDefinition clazz = findByTypeName(className);
    if (clazz == null) {
      clazz = new ClassDefinition(pkg, className);
      classes.add(clazz);
    }
    return clazz;
  }

  public void setFileCopyrightNotice(String fileCopyrightNotice) {
    this.fileCopyrightNotice = fileCopyrightNotice;
  }

  public void setDefaultClassComment(String defaultClassComment) {
    this.defaultClassComment = defaultClassComment;
  }

  public void setGenerateClassFile(String className, boolean generate) {
    ClassDefinition clazz = findByTypeName(className);
    if (clazz != null) clazz.setGenerateFile(generate);
  }

  public void merge(ClassDefCollection other) {
    for (ClassDefinition clazz : other.classes) {
      ClassDefinition existing = findByTypeName(clazz.getClassName());
      if (existing == null) {
        this.classes.add(clazz);
      } else {
        existing.merge(clazz);
      }
    }
  }

  public void generateClasses(File dir, String indent) throws IOException {
    for (ClassDefinition def : classes) {
      def.writeClassFile(dir, indent, fileCopyrightNotice, defaultClassComment);
    }
  }

  public ClassDefinition findByTypeName(String typeName) {
    for (ClassDefinition def : classes) {
      if (def.getClassName().equals(typeName)) return def;
    }
    return null;
  }

  /** Applies the specified mappings to all the classes */
  public void transform(CustomMappings mappings) {
    if (mappings == null) return;
    for (Map.Entry<String, String> mapping : mappings.subTypesEntrySet()) {
      String subType = mapping.getKey();
        String baseType = mapping.getValue();
        ClassDefinition baseClass = findByTypeName(baseType);
        ClassDefinition subClass = findByTypeName(subType);
        if (subClass != null) subClass.setBaseClass(baseClass);
    }

    for (Map.Entry<String, MappedTypeName> mapping : mappings.typesEntrySet()) {
      String origType = mapping.getKey();
      String mappedType = mapping.getValue().name;
      boolean isArrayType = mapping.getValue().isArrayType;
      ClassDefinition origClass = findByTypeName(origType);
      ClassDefinition mappedClass = findByTypeName(mappedType);
      if (mappedClass == null) {
        if (origClass != null) origClass.rename(mappedType);
      } else {
        mappedClass.merge(origClass);
        classes.remove(mappedClass);
      }
      for (ClassDefinition clazz : classes) {
        clazz.mapType(origType, mappedType, isArrayType);
        if (clazz.getClassName().equals(origType)) {
          clazz.rename(mappedType);
        }
      }
    }
    for (MappedFieldName mapping : mappings.fieldNameMappings()) {
      ClassDefinition origClass = findByTypeName(mapping.className);
      if (origClass != null) {
        origClass.mapFieldName(mapping);
      }
    }
  }
}
