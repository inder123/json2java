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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Defines custom mappings of names for a project.
 * @author inder
 *
 */
public class CustomMappings {

  public static final class MappedTypeName {
    public final String name;
    public final boolean isArrayType;
    public MappedTypeName(String name, boolean isArrayType) {
      this.name = name;
      this.isArrayType = isArrayType;
    }
  }
  public static final class MovedFieldName {
    public final String baseType;
    public final String subType;
    public final String fieldJsonName;
    public MovedFieldName(String fieldName, String baseType, String subType) {
      this.baseType = baseType;
      this.subType = subType;
      this.fieldJsonName = fieldName;
    }
  }
  public static final class MappedFieldName {
    public final String className;
    public final String jsonName;
    public final String fieldName;
    public MappedFieldName(String className, String jsonName, String fieldName) {
      this.className = className;
      this.jsonName = jsonName;
      this.fieldName = fieldName;
    }
  }
  private final Map<String, MappedTypeName> mappedTypes = new HashMap<>();
  private final List<MappedFieldName> mappedFieldNames = new ArrayList<>();
  private final List<MovedFieldName> movedFieldNames = new ArrayList<>();
  private final Map<String, String> mappedSubTypes = new HashMap<>();
  private final List<MappedFieldName> deletedFieldNames = new ArrayList<>();

  /**
   * replaces all classes of type name with mappedName.
   */
  public CustomMappings mapType(String name, String mappedName) {
    mappedTypes.put(name, new MappedTypeName(mappedName, false));
    return this;
  }

  public CustomMappings mapToArrayType(String name, String mappedArrayElementName) {
    mappedTypes.put(name, new MappedTypeName(mappedArrayElementName, true));
    return this;
  }

  public CustomMappings mapFieldName(String className, String jsonName, String fieldName) {
    mappedFieldNames.add(new MappedFieldName(className, jsonName, fieldName));
    return this;
  }

  public CustomMappings deleteFieldName(String className, String jsonName) {
    deletedFieldNames.add(new MappedFieldName(className, jsonName, null));
    return this;
  }

  public CustomMappings moveFieldToSubType(String fieldJsonName, String baseType, String subType) {
    this.movedFieldNames.add(new MovedFieldName(fieldJsonName, baseType, subType));
    return this;
  }

  public Set<Map.Entry<String, MappedTypeName>> typesEntrySet() {
    return mappedTypes.entrySet();
  }

  public List<MappedFieldName> fieldNameMappings() {
    return mappedFieldNames;
  }

  public List<MappedFieldName> deletedFields() {
    return deletedFieldNames;
  }

  public List<MovedFieldName> movedFieldNames() {
    return movedFieldNames;
  }

  public CustomMappings addMappings(CustomMappings other) {
    if (other != null) {
      this.mappedTypes.putAll(other.mappedTypes);
      this.mappedFieldNames.addAll(other.mappedFieldNames);
      this.deletedFieldNames.addAll(other.deletedFieldNames);
      this.movedFieldNames.addAll(other.movedFieldNames);
    }
    return this;
  }

  public Set<Map.Entry<String,String>> subTypesEntrySet() {
      return mappedSubTypes.entrySet();
  }

  public CustomMappings mapSubType(String subType, String baseType) {
      this.mappedSubTypes.put(subType, baseType);
      return this;
  }
}
