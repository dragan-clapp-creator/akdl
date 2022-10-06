package akdl.code.gen.java.persist;

public enum PlaceHolder {
  PACK, PACK0, IMPORTS, RT_DECL, IS_GRAPH,
  CODELINE, LEVEL,
  SUBBLOCK,
  TYPE, RT_TYPE, NAME, VARNAME, MNAME, CAR,
  TRANSIENT,
  BOOL, ATT,
  METHOD, ACCEPT,
  PARENT_NAME, PARENT_TYPE,
  PARSER,
  RET_TRUE_OPTION, OPTION, INDEX,
  BOOL_DECL, BOOL_TEST,
  REF_DECL, REF_BLOCK,
  ENUM_A, ENUM_B,
  METHOD_DECL,
  INTERFACE,
  VALUE, NR,
  INDENT,
  TRAVERSE, ITRAVERSE, FTRAVERSE,
  TNAME, UTNAME, EXTEND, IMPLEMENT;


  private PlaceHolder() {
  }

  /**
   * @return the string
   */
  public String getString() {
    return "%" + name() + "%";
  }
}
