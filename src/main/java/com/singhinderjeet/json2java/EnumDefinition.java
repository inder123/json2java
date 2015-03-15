package com.singhinderjeet.json2java;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Definition of an enum class.
 *
 * @author Inderjeet Singh
 */
public class EnumDefinition extends ClassDefinition {

  private final List<String> javaNames = new ArrayList<>();
  private final List<String> jsonNames = new ArrayList<>();

  public EnumDefinition(String pkgName, String enumClassName) {
    super(pkgName, enumClassName);
  }

  public EnumDefinition addEnumValue(String javaName, String jsonName) {
    javaNames.add(javaName);
    jsonNames.add(jsonName);
    return this;
  }

  @Override
  protected boolean needSerializedNameImport() {
    for (int i = 0; i < javaNames.size(); ++i) {
      String javaName = javaNames.get(i);
      String jsonName = jsonNames.get(i);
      if (jsonName != null && !jsonName.equals(javaName)) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected void writeClassBody(Writer writer, String indent) throws IOException {
    writer.append("public enum ").append(className);
    if (baseClass != null) writer.append(" extends ").append(baseClass.getClassName());
    writer.append(" {\n");
    writeEnumValues(writer, indent, 1);
    writer.append("}\n");
  }

  private void writeEnumValues(Writer writer, String indent, int indentLevel) throws IOException {
    int count = javaNames.size();
    for (int i = 0; i < count; ++i) {
      String javaName = javaNames.get(i);
      String jsonName = jsonNames.get(i);
      for (int j = 0; j < indentLevel; ++j) writer.append(indent);
      if (jsonName != null && !jsonName.equals(javaName)) {
        writer.append("@SerializedName(\"").append(jsonName).append("\") ");
      }
      writer.append(javaName);
      if (i != count-1) writer.append(",");
      writer.append("\n");
    }
  }
}
