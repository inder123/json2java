package com.singhinderjeet.json2java;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class JavaFormatter {
  private final String indent;
  private final int indentSize;
  private final int maxCol;
  private enum TextMode {
    SOURCE, LINE_COMMENT, BLOCK_COMMENT
  }
  private TextMode textMode = TextMode.SOURCE;

  private IndentationMode indentationMode = IndentationMode.NORMAL_INDENT;
  private enum IndentationMode {
    NORMAL_INDENT,
    /** When a line is long and is wrapped, the indentation is doubled. */
    WRAPPED_INDENT
  }

  private Appendable out;
  private int indentLevel;
  private int row;
  private int col;
  public JavaFormatter(int maxLineLength, String indent) {
    this.maxCol = maxLineLength;
    this.indent = indent;
    this.indentSize = indent.length();
  }

  public String format(String contents) throws IOException {
    StringWriter writer = new StringWriter();
    format(contents, writer);
    return writer.toString();
  }

  public void format(String contents, Appendable output) throws IOException {
    this.out = output;
    List<String> lines = Arrays.asList(contents.split("\\n"));
    indentLevel = 0;
    int j = 0;
    for (j = 0; j < lines.size(); ++j) {
      String line = lines.get(j);
      if (line.trim().isEmpty()) {
        lineBreak();
        continue;
      }
      col = indentLine();
      char[] chars = line.toCharArray();
      for (int i = 0; i < chars.length; ++i) {
        char ch = chars[i];
        ++col;
        if (ch == '{') {
          leftBrace(ch);
        } else if (ch == '}') {
          rightBrace(ch);
        } else if (ch == '(') {
          leftParen(ch);
        } else if (ch == ')') {
          rightParen(ch);
        } else if (ch == ';') {
          semicolon(ch);
        } else if (ch == '\n') {
        } else {
          if (col == 1) {
            indentLine();
          }
          out.append(ch);
        }
      }
      lineBreak();
    }
  }

  private void semicolon(char ch) throws IOException {
    out.append(ch);
    if (col >= maxCol) {
      lineBreak();
    }
  }

  private void rightParen(char ch) throws IOException {
    out.append(ch);
    if (indentationMode == IndentationMode.WRAPPED_INDENT) {
      indentationMode = IndentationMode.NORMAL_INDENT;
      --indentLevel;
    }
  }

  private void leftParen(char ch) throws IOException {
    out.append(ch);
    if (col >= maxCol) {
      lineBreak();
      indentationMode = IndentationMode.WRAPPED_INDENT;
      indentLevel += 2;
    }
  }

  private void rightBrace(char ch) throws IOException {
    --indentLevel;
    out.append(ch);
  }

  private void leftBrace(char ch) throws IOException {
    out.append(ch);
    ++indentLevel;
    if (col >= maxCol) lineBreak();
  }

  private void lineBreak() throws IOException {
    out.append("\n");
    ++row;
    col = 0;
  }

  private int indentLine() throws IOException {
    for (int i = 0; i < indentLevel; ++i) {
      out.append(indent);
      col += indentSize;
    }
    return col;
  }
}
