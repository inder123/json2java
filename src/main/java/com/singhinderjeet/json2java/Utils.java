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

/**
 * Utility methods.
 *
 * @author Inderjeet Singh
 */
public class Utils {
  static String lowerCaseUnderscoreToCamelCase(String name, boolean firstLetterUpperCase) {
    while (name.contains("_")) {
      int index = name.indexOf('_');
      if (index + 1 == name.length()) { // is last character
        name = name.substring(0, name.length() - 1);
      } else {
        String firstPart = index == 0 ? "" : name.substring(0, index);
        char camelCase = Character.toUpperCase(name.charAt(index + 1));
        String lastPart = name.substring(index + 2);
        name = firstPart + camelCase + lastPart;
      }
    }
    if (firstLetterUpperCase) name = firstLetterUpperCase(name);
    return name;
  }

  public static String firstLetterUpperCase(String name) {
    return Character.toUpperCase(name.charAt(0)) + (name.length() > 1 ? name.substring(1) : "");
  }
}
