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

  public Json2Java(Reader reader, String rootPackage, String rootClassName) throws IOException {
    JsonElement root = new JsonParser().parse(reader);
    reader.close();
    while (!(root instanceof JsonObject)) {
      if (root instanceof JsonArray) {
        root = root.getAsJsonArray().get(0);
      } else if (root instanceof JsonPrimitive) {
        root = null;
      }
    }
    if (root instanceof JsonObject) {
      generateClasses(root.getAsJsonObject(), rootPackage, rootClassName);
    }
  }

  public void transform(CustomMappings mappings) {
    classes.transform(mappings);
  }

  private void generateClasses(JsonObject root, String rootPackage, String rootClassName) throws IOException {
    ClassDefinition classDef = classes.addClassDefinition(rootPackage, rootClassName);
    for (Map.Entry<String, JsonElement> element : root.entrySet()) {
      String name = element.getKey();
      JsonElement value = element.getValue();
      String type = Utils.lowerCaseUnderscoreToCamelCase(name, true);
      name = Utils.lowerCaseUnderscoreToCamelCase(name, false);
      if (value instanceof JsonPrimitive) {
        classDef.addField(new ClassField(name, type, false));
      } else if (value instanceof JsonArray) {
        classDef.addImport("java.util.List");
        classDef.addField(new ClassField(name, type, true));
        JsonArray array = value.getAsJsonArray();
        if (array.size() > 0) {
          value = array.get(0);
        }
      }
      if (value instanceof JsonObject) {
        classDef.addField(new ClassField(name, type, false));
        generateClasses(value.getAsJsonObject(), rootPackage, type);
      }
    }
  }

  public ClassDefCollection getClasses() {
    return classes;
  }
}
