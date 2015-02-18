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
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * class to run this tool from command line.
 *
 * @author Inderjeet Singh
 */
public class Main {
  private final String packageName;
  private final String rootClassName;
  private final File outputDir;

  public Main(String packageName, String rootClassName, File outputDir) {
    this.packageName = packageName;
    this.rootClassName = rootClassName;
    this.outputDir = outputDir;
  }

  public void generate(Reader reader, CustomMappings mappings) throws IOException {
    Json2Java converter = new Json2Java();
    converter.processJson(reader, packageName, rootClassName, mappings);
    ClassDefCollection classes = converter.getClasses();
    classes.generateClasses(outputDir, "    ");
  }

  public static void main(String[] args) throws Exception {
    String inputJsonFileName = args[0];
    String packageName = args[1];
    String rootClassName = args[2];
    Reader reader = new FileReader(inputJsonFileName);
    File outputDir = File.createTempFile("json2java", "src").getParentFile();
    Main main = new Main(packageName, rootClassName, outputDir);
    main.generate(reader, null);
  }
}
