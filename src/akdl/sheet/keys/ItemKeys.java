package akdl.sheet.keys;

import java.io.StreamTokenizer;


public enum ItemKeys {
  C_EQUAL('='),
  C_INF('<'),
  C_SUP('>'),
  C_PLUS('+'),
  C_UNDERSCORE('_'),
  C_AMPERS('&'),
  C_QUEST('?'),
  C_EXLAM('!'),
  C_SEMI(';'),
  C_COLON(':'),
  C_PAR('('),
  C_PAR_END(')'),
  C_VBAR('|'),
  C_STAR('*'),
  C_BRACKET('['),
  C_BRACKET_END(']'),
  C_QUOTE('\''),
  C_2QUOTES('\"'),
  C_BACKSLASH('\\'),
  C_HUT('^'),
  C_TILD('~'),
  C_BRACE('{'),
  C_BRACE_END('}'),
  C_COMMA(','),
  C_SHARP('#'),
  C_DOLLAR('$'),
  C_DOT('.'),
  TT_WORD((char)StreamTokenizer.TT_WORD),
  TT_NUMBER((char)StreamTokenizer.TT_NUMBER);

  private char name;

  ItemKeys(char n) {
    this.name = n;
  }

  public char getKey() {
    return name;
  }
}
