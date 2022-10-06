package akdl.graph.gen.proc;

import akdl.code.gen.java.CodeBeanForParser;
import akdl.code.gen.java.CodeBeanForRuntime;
import akdl.code.gen.java.persist.PlaceHolder;
import akdl.graph.nodes.PredefinedSymbol;
import akdl.sheet.parse.ParseResult;

public class HandlerForPredefined {

  private static final HandlerForPredefined instance = new HandlerForPredefined();

  static public HandlerForPredefined getInstance() {
    return instance;
  }

  /**
   * add Parser For None
   * @param level
   * @param pbean
   * @param rbean
   * @param sym
   * @param template 
   * @param result
   */
  public void addParserForAll(int level, CodeBeanForParser pbean, CodeBeanForRuntime rbean, PredefinedSymbol sym,
      String template, ParseResult result) {
    pbean.insert(level, template, result);
    pbean.addToImports("java.io.", "StreamTokenizer");
    pbean.replaceAll(PlaceHolder.NAME, rbean.getName(), result);
    pbean.replaceAll(PlaceHolder.TYPE, sym.upper(sym.getName()), result);
  }
}
