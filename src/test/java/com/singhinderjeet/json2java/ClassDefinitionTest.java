// Copyright (C) 2014 Peel Inc.
package com.singhinderjeet.json2java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;

import org.junit.Test;

import com.google.gson.JsonPrimitive;
import com.singhinderjeet.json2java.CustomMappings.MappedFieldName;

public class ClassDefinitionTest {

  @Test
  public void testIsBaseClassField() {
    ClassDefinition baseClass = new ClassDefinition("a.b", "BaseClass");
    ClassDefinition subClass = new ClassDefinition("a.b", "SubClass");
    baseClass.addField(new ClassField("a", new JsonPrimitive("a base"), "A", false));
    subClass.addField(new ClassField("a", new JsonPrimitive("a sub"), "A", false));
    subClass.addField(new ClassField("b", new JsonPrimitive("b value"), "B", false));
    subClass.setBaseClass(baseClass);
    assertTrue(subClass.isBaseClassField("a"));
    assertFalse(subClass.isBaseClassField("b"));
    assertEquals(baseClass, subClass.getBaseClass());
  }

  @Test
  public void testWriteConstructor() throws Exception {
    ClassDefinition baseClass = new ClassDefinition("a.b", "BaseClass");
    ClassDefinition subClass = new ClassDefinition("a.b", "SubClass");
    baseClass.addField(new ClassField("a", new JsonPrimitive("a base"), "A", false));
    subClass.addField(new ClassField("a", new JsonPrimitive("a sub"), "A", false));
    subClass.addField(new ClassField("b", new JsonPrimitive("b value"), "B", false));
    subClass.setBaseClass(baseClass);

    StringWriter writer = new StringWriter();
    subClass.writeConstructor(writer, "");
    String value = writer.toString().trim();
    assertTrue(value.startsWith("public SubClass(String a, String b)"));
  }

  @Test
  public void testWriteConstructorMappedName() throws Exception {
    ClassDefinition baseClass = new ClassDefinition("a.b", "BaseClass");
    ClassDefinition subClass = new ClassDefinition("a.b", "SubClass");
    baseClass.addField(new ClassField("aa", new JsonPrimitive("a base"), "A", false));
    subClass.addField(new ClassField("aa", new JsonPrimitive("a sub"), "A", false));
    subClass.addField(new ClassField("bb", new JsonPrimitive("b value"), "B", false));
    subClass.setBaseClass(baseClass);
    baseClass.mapFieldName(new MappedFieldName("BaseClass", "aa", "aA"));
    subClass.mapFieldName(new MappedFieldName("SubClass", "aa", "aA"));

    StringWriter writer = new StringWriter();
    subClass.writeConstructor(writer, "");
    String classContents = writer.toString().trim();
    assertTrue(classContents.startsWith("public SubClass(String aA, String bb)"));

    writer = new StringWriter();
    subClass.writeClassFile(writer, "  ", null, null);
    classContents = writer.toString();
    assertFalse(classContents.contains("String aA, String aA"));

    writer = new StringWriter();
    baseClass.writeClassFile(writer, "  ", null, null);
    classContents = writer.toString();
    assertTrue(classContents.contains("@SerializedName(\"aa\")"));
  }
}
