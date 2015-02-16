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

import org.junit.Test;

/**
 * Unit tests for {@link Utils}.
 *
 * @author Inderjeet Singh
 */
public class UtilsTest {

  @Test
  public void testLowerCaseUnderscoreToCamelCase() throws Exception {
    assertEquals("AbraCaDabra", Utils.lowerCaseUnderscoreToCamelCase("AbraCaDabra", true));
    assertEquals("AbraCaDabra", Utils.lowerCaseUnderscoreToCamelCase("abraCaDabra", true));
    assertEquals("abraCaDabra", Utils.lowerCaseUnderscoreToCamelCase("abraCaDabra", false));
    assertEquals("A", Utils.lowerCaseUnderscoreToCamelCase("a", true));
    assertEquals("AbraCaDabra", Utils.lowerCaseUnderscoreToCamelCase("_Abra_Ca_Dabra_", true));
    assertEquals("AbraCaDabra", Utils.lowerCaseUnderscoreToCamelCase("_abra_Ca_Dabra_", true));
    assertEquals("AbraCaDabra", Utils.lowerCaseUnderscoreToCamelCase("Abra_caDabra", true));
    assertEquals("AbraCaDabra", Utils.lowerCaseUnderscoreToCamelCase("Abra_Ca_Dabra", true));
  }
}
