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
import java.util.ArrayList;
import java.util.List;

/**
 * A list of class definitions.
 *
 * @author Inderjeet Singh
 */
public class ClassDefCollection {
  private final List<ClassDefinition> classes = new ArrayList<>();

  public void generateClasses(File dir, String indent) throws IOException {
    for (ClassDefinition def : classes) {
      def.writeClassFile(dir, indent);
    }
  }

  public void add(ClassDefinition classDef) {
    if (!present(classDef)) {
      classes.add(classDef);
    }
  }

  private boolean present(ClassDefinition classDef) {
    for (ClassDefinition def : classes) {
      if (def.isSame(classDef)) return true;
    }
    return false;
  }
}
