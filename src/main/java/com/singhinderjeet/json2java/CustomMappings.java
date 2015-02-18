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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Defines custom mappings of names for a project.
 * @author inder
 *
 */
public class CustomMappings {

  public static final class MappedName {
    public final String name;
    public final boolean isArrayType;
    public MappedName(String name, boolean isArrayType) {
      this.name = name;
      this.isArrayType = isArrayType;
    }
  }
  private final Map<String, MappedName> mappedTypes = new HashMap<>();

  /**
   * replaces all classes of type name with mappedName.
   */
  public CustomMappings mapType(String name, String mappedName) {
    mappedTypes.put(name, new MappedName(mappedName, false));
    return this;
  }

  public CustomMappings mapToArrayType(String name, String mappedArrayElementName) {
    mappedTypes.put(name, new MappedName(mappedArrayElementName, true));
    return this;
  }

  public Set<Map.Entry<String,MappedName>> entrySet() {
    return mappedTypes.entrySet();
  }

  public CustomMappings addMappings(CustomMappings other) {
    if (other != null) {
      this.mappedTypes.putAll(other.mappedTypes);
    }
    return this;
  }
}
