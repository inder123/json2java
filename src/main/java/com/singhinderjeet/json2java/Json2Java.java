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
import java.io.Reader;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * Entry class to convert a JSON file to Java source code.
 *
 * @author Inderjeet Singh
 */
public class Json2Java {
  private final ClassDefCollection classes = new ClassDefCollection();

  public void processJson(Reader reader, String pkg, String className,
      CustomMappings mappings) throws IOException {
    JsonElement root = new JsonParser().parse(reader);
    reader.close();
    processJson(pkg, className, mappings, root);
  }

  private void processJson(String pkg, String className, CustomMappings mappings, JsonElement root)
      throws IOException {
    while (!(root instanceof JsonObject)) {
      if (root instanceof JsonArray) {
        for (JsonElement arrayElement : root.getAsJsonArray()) {
          processJson(pkg, className, mappings, arrayElement);
        }
        return; // nothing much needs to be done
      } else if (root instanceof JsonPrimitive) {
        return; // can't generate classes for a primitive
      }
    }
    if (root instanceof JsonObject) {
      ClassDefCollection classes = new ClassDefCollection();
      generateClasses(classes, root.getAsJsonObject(), pkg, className);
      classes.transform(mappings);
      this.classes.merge(classes);
    }
  }

  public void transform(CustomMappings mappings) {
    classes.transform(mappings);
  }

  private void generateClasses(ClassDefCollection classes, JsonObject root,
      String pkg, String className) throws IOException {
    ClassDefinition classDef = classes.addClassDefinition(pkg, className);
    for (Map.Entry<String, JsonElement> element : root.entrySet()) {
      String name = element.getKey();
      JsonElement value = element.getValue();
      String type = Utils.lowerCaseUnderscoreToCamelCase(name, true);
      name = Utils.lowerCaseUnderscoreToCamelCase(name, false);
      if (value instanceof JsonPrimitive) {
        classDef.addField(new ClassField(name, value, type, false));
      } else if (value instanceof JsonArray) {
        classDef.addImport("java.util.List");
        classDef.addField(new ClassField(name, value, type, true));
        JsonArray array = value.getAsJsonArray();
        for (JsonElement arrayElement : array) {
          if (arrayElement instanceof JsonObject) { // Use all elements of the array
            generateClasses(classes, arrayElement.getAsJsonObject(), pkg, type);
          }
        }
      }
      if (value instanceof JsonObject) {
        classDef.addField(new ClassField(name, value, type, false));
        generateClasses(classes, value.getAsJsonObject(), pkg, type);
      }
    }
  }

  public ClassDefCollection getClasses() {
    return classes;
  }

  public void addEnum(EnumDefinition enumClass) {
    this.classes.addEnumClass(enumClass);
  }
}
