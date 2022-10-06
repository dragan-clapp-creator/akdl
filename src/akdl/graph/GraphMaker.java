package akdl.graph;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

import akdl.sheet.keys.Parser;
import akdl.sheet.parse.ParseResult;

public class GraphMaker extends StreamTokenizer {

  private Graph graph;
  private Parser parser;
  private ParseResult result;

  /**
   * constructor
   * 
   * @param rd
   * @param prs
   * @param res
   * @param isVisual
   */
  public GraphMaker(Reader rd, Parser prs, ParseResult res, boolean isVisual) {
    super(rd);
    setup();
    parser = prs;
    result = res;
    graph = new Graph(isVisual);
  }

  //
  private void setup() {
    eolIsSignificant(false);
    lowerCaseMode(false);
    quoteChar('\'');
    ordinaryChar('~');
    wordChars('<', '<');
    wordChars('>', '>');
    wordChars('_', '_');
    wordChars('-', '-');
    ordinaryChar('.');
    slashSlashComments(true);
    slashStarComments(true);
  }

  public void createGraph() throws IOException {
    new Automaton(this).process(true);
    pushBack();
  }

  public Graph getGraph() {
    return graph;
  }

  /**
   * @return the result
   */
  public ParseResult getResult() {
    return result;
  }

  /**
   * @return the parser
   */
  public Parser getParser() {
    return parser;
  }
}
