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
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

/**
 * Unit tests for {@link Main}.
 *
 * @author Inderjeet Singh
 */
public class Json2JavaTest {

  @Test
  public void testReverseGeocodingJson() throws Exception {
    InputStream json = Json2JavaTest.class.getResourceAsStream("/reversegeocoding.json");
    InputStreamReader reader = new InputStreamReader(json, "UTF-8");
    File dir = File.createTempFile("json2java", "src").getParentFile();
    Json2Java converter = new Json2Java(reader, "com.reversegeocoding", "ReverseGeocodingResults");
    CustomMappings mappings = new CustomMappings()
      .mapType("Types", "String")
      .mapType("Northeast", "Location")
      .mapType("Southeast", "Location")
      .mapType("Southwest", "Location")
      .mapType("Northwest", "Location")
      .mapType("AddressComponents", "AddressComponent")
      .mapType("LongName", "String")
      .mapType("ShortName", "String");
    converter.transform(mappings);
    ClassDefCollection classes = converter.getClasses();
    classes.generateClasses(dir, "    ");
  }

  @Test
  public void testTmsLineupsJson() throws Exception {
    InputStream json = Json2JavaTest.class.getResourceAsStream("/tms-lineups.json");
    InputStreamReader reader = new InputStreamReader(json, "UTF-8");
    File dir = File.createTempFile("json2java", "src").getParentFile();
    Json2Java converter = new Json2Java(reader, "com.tms.lineups", "Lineup");
    CustomMappings mappings = new CustomMappings()
      .mapType("LineupId", "String")
      .mapType("Name", "String")
      .mapType("Type", "String");
    converter.transform(mappings);
    ClassDefCollection classes = converter.getClasses();
    classes.generateClasses(dir, "    ");
  }
}
