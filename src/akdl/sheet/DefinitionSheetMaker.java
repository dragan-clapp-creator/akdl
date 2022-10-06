package akdl.sheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.HashMap;

import akdl.graph.Graph;
import akdl.graph.GraphMaker;
import akdl.graph.nodes.DefinitionNode;
import akdl.graph.nodes.GroupOfSymbols;
import akdl.graph.nodes.elts.ASymbol;
import akdl.sheet.keys.DefKeys;
import akdl.sheet.keys.ItemKeys;
import akdl.sheet.keys.Parser;
import akdl.sheet.parse.MessageWriter;
import akdl.sheet.parse.ParseResult;

public class DefinitionSheetMaker extends StreamTokenizer {

  private Reader reader;
  private DefinitionSheet definitionSheet;
  private Parser parser;
  private Graph graph;
  private boolean isVisual;


  /**
   * constructor
   * 
   * @param r    reader
   * @param b    isVisual
   */
  public DefinitionSheetMaker(Reader r, boolean b) {
    super(r);
    setup();
    reader = r;
    isVisual = b;
    definitionSheet = new DefinitionSheet();
    parser = new Parser();
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
    slashSlashComments(true);
    slashStarComments(true);
  }

  /**
   * create internal Definition Sheet representation
   * 
   * @param result
   * @throws IOException
   */
  public void createDefinitionSheet(ParseResult result) throws IOException {
    int token = nextToken();
    String cst;
    while (token != TT_EOF) {
      // HEADER INFORMATION
      if (token == TT_WORD) {
        DefKeys key = parser.getDefKey(sval);
        if (key == null) {
          MessageWriter.E001.store(this, result);
        }
        else {
          switch(key) {
            case KEY_SAVE_DEF:
              cst = getConstant(result, "full qualified Path name");
              if (cst == null) {
                MessageWriter.E002.store(this, result);
              }
              else {
                definitionSheet.setPathToSaveDefinitions( cst );
              }
              break;
            case KEY_LOAD_DEF:
              cst = getConstant(result, "full qualified Path name");
              if (cst == null) {
                MessageWriter.E002.store(this, result);
              }
              else {
                definitionSheet.setLoadDefinitionsFrom( cst );
               }
              break;
            case KEY_JAVA_DEF:
              break;
            case KEY_DESTINATION:
              cst = getConstant(result, "full qualified Path name");
              if (cst == null) {
                MessageWriter.E002.store(this, result);
              }
              else {
                definitionSheet.setDestination( cst );
              }
              break;
            case KEY_VERBOSITY:
              token = nextToken();
              if (token != TT_NUMBER) {
                pushBack();
                result.setIsVerbose();
              }
              else {
                result.setVerbosity((int) nval);
              }
              break;
            case KEY_DEBUG:
              result.setIsDebug(true);
              break;
            default:
              break;
          }
        }
      }
      // GRAPH DEFINITION
      else {
        ItemKeys ikey = parser.getKey(token);
        switch(ikey) {
          case C_BRACE:
            result.setBaseLine(lineno()-1);
            GraphMaker gm = new GraphMaker(reader, parser, result, isVisual);
            graph = gm.getGraph();
            deserialize();
            gm.createGraph();
            break;
          default:
            MessageWriter.E005.store(this, result, Character.toString((char) token));
            break;
        }
      }
      token = nextToken();
    }
  }

  //
  private String getConstant(ParseResult res, String waitings) throws IOException {
    int token = nextToken();
    ItemKeys ikey = parser.getKey(token);
    switch(ikey) {
      case C_BRACE:
        MessageWriter.E003.store(this, res);
        pushBack();
        break;
      case C_2QUOTES:
        break;
      default:
        MessageWriter.E004.store(this, res, waitings);
        break;
    }
    return sval;
  }

  //
  @SuppressWarnings("unchecked")
  private void deserialize() {
    String src = definitionSheet.getLoadDefinitionsFrom();
    if (src == null) {
      return;
    }
    Object obj = null;
    try {
      File rtf = new File(src);
      FileInputStream fis = new FileInputStream(rtf);
      ObjectInputStream in = new ObjectInputStream(fis);
      obj = in.readObject();
      in.close();
      graph.setMap( (HashMap<String, DefinitionNode>) obj );
      ParseResult.getInstance().addDefinedKeys(graph.getMap().keySet());
    }
    catch(IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * used eventually by top-down approach
   * @return
   */
  public boolean serializeLL() {
    String destination = definitionSheet.getPathToSaveDefinitions();
    HashMap<String, DefinitionNode> map = graph.getMap();
    if (destination != null && map != null) {
      markAsSerialized(map);
      return serialize(map, destination, null);
    }
    return true;
  }

  //
  private boolean serialize(HashMap<String, DefinitionNode> map, String destination, DefinitionNode root) {
    File f = new File(destination);
    try {
      f.getParentFile().mkdir();
      FileOutputStream fos = new FileOutputStream(f);
      ObjectOutputStream out = new ObjectOutputStream(fos);
      if (root != null) {
        out.writeObject(root);
      }
      else {
        out.writeObject(map);
      }
      out.close();
      return true;
    }
    catch(IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  //
  private void markAsSerialized(HashMap<String, DefinitionNode> map) {
    for (String key : map.keySet()) {
      DefinitionNode def = map.get(key);
      def.setSerialized(true);
      for (ASymbol sym : def.getSyntax()) {
        sym.setSerialized(true);
        if (sym instanceof GroupOfSymbols) {
          for (ASymbol s : ((GroupOfSymbols)sym).getSyntax()) {
            s.setSerialized(true);
          }
        }
      }
    }
  }

  /**
   * @return the definitionSheet
   */
  public DefinitionSheet getDefinitionSheet() {
    return definitionSheet;
  }

  /**
   * @return the graph
   */
  public Graph getGraph() {
    return graph;
  }
}
