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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ClassMergeTest {

  @Test
  public void testMergeSimple() throws Exception {
    ClassDefinition first = new ClassDefinition("a.b", "First");
    ClassDefinition second = new ClassDefinition("a.b", "Second");
    first.addField(new ClassField("aa", new JsonPrimitive("a first"), "A", false));
    second.addField(new ClassField("aa", new JsonPrimitive("a second"), "A", false));
    second.addField(new ClassField("bb", new JsonPrimitive("b second"), "B", false));

    first.merge(second);
    assertEquals(2, first.getFieldsCount());
    assertTrue(first.present("aa"));
    assertTrue(first.present("bb"));
    Set<JsonElement> jsonValues = first.find("aa").getJsonValues();
    assertEquals(2, jsonValues.size());
    assertTrue(jsonValues.contains(new JsonPrimitive("a first")));
    assertTrue(jsonValues.contains(new JsonPrimitive("a second")));
  }
}
