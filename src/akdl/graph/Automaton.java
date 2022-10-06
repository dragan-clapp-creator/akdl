package akdl.graph;

import java.io.IOException;
import java.io.StreamTokenizer;

import akdl.eval.EvaluationInfo.EvalType;
import akdl.graph.nodes.CharSymbol;
import akdl.graph.nodes.DefinitionNode;
import akdl.graph.nodes.EnumSymbol;
import akdl.graph.nodes.GroupOfSymbols;
import akdl.graph.nodes.KeySymbol;
import akdl.graph.nodes.PredefinedSymbol;
import akdl.graph.nodes.RegExpSymbol;
import akdl.graph.nodes.StringSymbol;
import akdl.graph.nodes.VoidKeySymbol;
import akdl.graph.nodes.elts.Operator;
import akdl.sheet.keys.AttKeys;
import akdl.sheet.keys.ItemKeys;
import akdl.sheet.keys.LeafType;
import akdl.sheet.parse.MessageWriter;

public class Automaton {

  private GraphMaker graphMaker;
  private State state;
  private DefinitionNode node;
  private Action action;

  /**
   * primary constructor
   * 
   * @param gm
   */
  public Automaton(GraphMaker gm) {
    graphMaker = gm;
  }

  /**
   * secondary constructor
   * 
   * @param gm
   */
  public Automaton(GraphMaker gm, DefinitionNode n) {
    graphMaker = gm;
    node = n;
    state = new DefinitionState();
  }

  /**
   * @return the state
   */
  public State getState() {
    return state;
  }

  public boolean isFinished() {
    return state instanceof ErrorState || state instanceof FinalState;
  }

  public boolean process(boolean isInit) throws IOException {
    if (isInit) {
      node = null;
      state = new InitialState();
    }
    while (state != null) {
      state = state.perform();
    }

    return !graphMaker.getResult().hasErrors();
  }

  public boolean processOneSentence() throws IOException {
    do {
      state = state.perform();
    }
    while (state != null && !(state instanceof InitialState) && !(state instanceof ErrorState));

    return (state instanceof InitialState) && !graphMaker.getResult().hasErrors();
  }

  //
  private String extractPath(ItemKeys separator) throws IOException {
    StringBuilder path = new StringBuilder();
    ItemKeys ikey = ItemKeys.C_DOT;
    boolean iseval;
    int token;
    do {
      iseval = false;
      token = graphMaker.nextToken();
      if (token == StreamTokenizer.TT_WORD) {
        String string = graphMaker.sval;
        if (separator == ItemKeys.C_COMMA) {
          EvalType eval = graphMaker.getParser().getEvaluationType(string);
          if (eval != null) {
            if (!setEvaluator(eval)) {
              return null;
            }
            iseval = true;
            continue;
          }
        }
        path.append(string);
      }
      else {
        ikey = graphMaker.getParser().getKey(token);
        if (ikey == ItemKeys.C_DOT) {
          path.append(ikey.getKey());
        }
      }
    }
    while (iseval ||
        ikey != null &&  ikey != separator
        && ikey != ItemKeys.C_BRACKET_END);

    if (ikey == null) {
      MessageWriter.E005.store(graphMaker, graphMaker.getResult(), Character.toString((char) token));
      return null;
    }
    if (ikey == ItemKeys.C_EQUAL) {
      graphMaker.pushBack();
    }
    return path.length() > 0 ? path.toString() : null;
  }

  //
  private boolean setEvaluator(EvalType eval) throws IOException {
    node.setEval(eval);
    int token = graphMaker.nextToken();
    ItemKeys ikey = graphMaker.getParser().getKey(token);
    if (ikey != ItemKeys.C_COMMA && ikey != ItemKeys.C_BRACKET_END) {
      MessageWriter.E005.store(graphMaker, graphMaker.getResult(), Character.toString((char) token));
      return false;
    }
    if (ikey == ItemKeys.C_BRACKET_END) {
      graphMaker.pushBack();
    }
    return true;
  }

  //========================================================================

  public interface State {
    public State perform() throws IOException; 
  }

  /**
   * Initial state
   * 
   * @author dragan
   *
   */
  public class InitialState implements State {

    @Override
    public State perform() throws IOException {
      action = Action.ADD;
      int token = graphMaker.nextToken();
      if (token == StreamTokenizer.TT_WORD) {
        node = new DefinitionNode(graphMaker.sval);
        token = graphMaker.nextToken();
        ItemKeys ikey = graphMaker.getParser().getKey(token);
        if (ikey == null) {
          MessageWriter.E100.store(graphMaker);
        }
        else {
          switch (ikey) {
            case C_BRACKET:
              return new JavaAttributeState();
            case C_EQUAL:
              return new DefinitionState();
            case C_BRACE_END:
              return new FinalState(true);
            default:
              MessageWriter.E101.store(graphMaker);
          }
        }
      }
      else {
        ItemKeys ikey = graphMaker.getParser().getKey(token);
        switch (ikey) {
          case C_PLUS:
            action = Action.INSERT;
            break;
          case C_BRACE_END:
            return new FinalState(true);
          default:
            MessageWriter.E102.store(graphMaker);
        }
        return new DeltaState();
      }
      return new ErrorState();
    }
  }

  /**
   * Delta state
   * 
   * @author dragan
   *
   */
  public class DeltaState implements State {

    @Override
    public State perform() throws IOException {
      int token = graphMaker.nextToken();
      if (token == StreamTokenizer.TT_WORD) {
        node = new DefinitionNode(graphMaker.sval);
        token = graphMaker.nextToken();
        ItemKeys ikey = graphMaker.getParser().getKey(token);
        if (ikey == null) {
          MessageWriter.E100.store(graphMaker);
        }
        else {
          switch (ikey) {
            case C_BRACKET:
              return new JavaAttributeState();
            case C_EQUAL:
              return new DefinitionState();
            case C_BRACE_END:
              return new FinalState(true);
            default:
              MessageWriter.E101.store(graphMaker);
          }
        }
      }
      return new ErrorState();
    }
  }

  /**
   * Java Attribute state
   * 
   * @author dragan
   *
   */
  public class JavaAttributeState implements State {

    @Override
    public State perform() throws IOException {
      String path = extractPath(ItemKeys.C_COMMA);
      if (path != null) {
        node.setParserPath(path);
      }
      path = extractPath(ItemKeys.C_EQUAL);
      if (path != null) {
        node.setRuntimerPath(path);
      }
      return new EndAttributeState();
    }
  }

  /**
   * Path Attribute state
   * 
   * @author dragan
   *
   */
  public class PathAttributeState implements State {

    @Override
    public State perform() throws IOException {
      int token = graphMaker.nextToken();
      ItemKeys ikey = graphMaker.getParser().getKey(token);
      if (ikey == null) {
        MessageWriter.E005.store(graphMaker, graphMaker.getResult(), Character.toString((char) token));
      }
      else {
        switch (ikey) {
          case C_BRACKET_END:
            if (checkEquals()) {
              return new DefinitionState();
            }
            break;
          case C_COLON:
            String path = extractPath(ItemKeys.C_COMMA);
            if (path != null) {
              node.setParserPath(path);
            }
            path = extractPath(ItemKeys.C_EQUAL);
            if (path != null) {
              node.setRuntimerPath(path);
            }
            if (checkEquals()) {
              return new DefinitionState();
            }
          default:
            MessageWriter.E005.store(graphMaker, graphMaker.getResult(), Character.toString((char) token));
        }
      }
      return new ErrorState();
    }

    //
    private boolean checkEquals() throws IOException {
      int token = graphMaker.nextToken();
      ItemKeys ikey = graphMaker.getParser().getKey(token);
      if (ikey == ItemKeys.C_EQUAL) {
        return true;
      }
      MessageWriter.E103.store(graphMaker);
      return false;
    }
  }

  /**
   * End Attribute state
   * 
   * @author dragan
   *
   */
  public class EndAttributeState implements State {

    @Override
    public State perform() throws IOException {
      int token = graphMaker.nextToken();
      ItemKeys ikey = graphMaker.getParser().getKey(token);
      if (ikey == null) {
        MessageWriter.E005.store(graphMaker, graphMaker.getResult(), Character.toString((char) token));
      }
      else {
        switch (ikey) {
          case C_BRACKET_END:
            token = graphMaker.nextToken();
            ikey = graphMaker.getParser().getKey(token);
            if (ikey == null || ikey != ItemKeys.C_EQUAL) {
              MessageWriter.E103.store(graphMaker);
              break;
            }
          case C_EQUAL:
            return new DefinitionState();
          default:
            MessageWriter.E005.store(graphMaker, graphMaker.getResult(), Character.toString((char) token));
        }
      }
      return new ErrorState();
    }
  }

  /**
   * Definition state
   * 
   * @author dragan
   *
   */
  public class DefinitionState implements State {

    @Override
    public State perform() throws IOException {
      ItemKeys ikey;
      do {
        int token = graphMaker.nextToken();
        ikey = graphMaker.getParser().getKey(token);
        if (ikey == null) {
          if (token == StreamTokenizer.TT_WORD) {
            handleNodeOrLeaf(token, Operator.NONE);
          }
          else {
            MessageWriter.E005.store(graphMaker, graphMaker.getResult(), Character.toString((char) token));
            return new ErrorState();
          }
        }
        else {
          switch (ikey) {
            case C_2QUOTES:
              if (!handleNodeString(Operator.NONE)) {
                return new ErrorState();
              }
              break;
            case C_QUOTE:
              CharSymbol leaf = new CharSymbol(graphMaker.sval.charAt(0), Operator.NONE);
              node.addToSyntax(leaf);
              break;
            case C_TILD:
              if (!handleVoidKey(token, Operator.NONE)) {
                return new ErrorState();
              }
              break;
            case C_HUT:
            case C_AMPERS:
            case C_SHARP:
            case C_STAR:
            case C_PLUS:
              if (!handleNodeOperator(ikey)) {
                return new ErrorState();
              }
              break;
            case C_PAR:
              handleGroup(Operator.NONE);
              break;
            case C_PAR_END:
              return new FinalState(false);
            case C_SEMI:
              return new EndOfDefinitionState();

            default:
              MessageWriter.E005.store(graphMaker, graphMaker.getResult(), Character.toString((char) token));
              return new ErrorState();
          }
        }
      }
      while (ikey != ItemKeys.C_SEMI);

      return new ErrorState();
    }

    //
    private boolean handleNodeOrLeaf(int token, Operator op) throws IOException {
      ItemKeys ikey = graphMaker.getParser().getKey(token);
      if (ikey != null) {
        switch (ikey) {
          case C_2QUOTES:
            return handleNodeString(op);
          case C_QUOTE:
            CharSymbol leaf = new CharSymbol(graphMaker.sval.charAt(0), op);
            node.addToSyntax(leaf);
            return true;
          case C_TILD:
            token = graphMaker.nextToken();
            return handleVoidKey(token, op);

          default:
            break;
        }
      }
      boolean isRef = "REFERENCE".equals(graphMaker.sval);
      if (isRef) {
        token = graphMaker.nextToken();
        ItemKeys ik = graphMaker.getParser().getKey(token);
        if (ik == ItemKeys.C_COLON) {
          token = graphMaker.nextToken();
        }        
      }
      if (token == StreamTokenizer.TT_WORD) {
        LeafType lt = graphMaker.getParser().getLeaf(graphMaker.sval);
        if (lt == null) {
          KeySymbol n = new KeySymbol(graphMaker.sval, op, isRef);
          if (setKeySymbol(n)) {
            node.addToSyntax(n);
          }
        }
        else {
          String name = extractName();
          String pattern = extractPattern();
          PredefinedSymbol leaf;
          if (pattern != null) {
            leaf = new RegExpSymbol(lt, name, pattern, isRef, op);
          }
          else {
            leaf = new PredefinedSymbol(lt, name, isRef, op);
          }
          node.addToSyntax(leaf);
        }
        return true;
      }
      return false;
    }

    //
    private boolean handleVoidKey(int token, Operator op) throws IOException {
      token = graphMaker.nextToken();
      if (token == StreamTokenizer.TT_WORD) {
        LeafType lt = graphMaker.getParser().getLeaf(graphMaker.sval);
        if (lt != null) {
          MessageWriter.E201.store(graphMaker, graphMaker.getResult(), graphMaker.sval);
        }
        else {
          VoidKeySymbol leaf = new VoidKeySymbol(graphMaker.sval, op);
          node.addToSyntax(leaf);
          return true;
        }
      }
      else {
        MessageWriter.E005.store(graphMaker, graphMaker.getResult(), Character.toString((char) token));
      }
      return false;
    }

    //
    private String extractName() throws IOException {
      int token = graphMaker.nextToken();
      ItemKeys ik = graphMaker.getParser().getKey(token);
      if (ik == ItemKeys.C_COLON) {
        token = graphMaker.nextToken();
        if (token == StreamTokenizer.TT_WORD) {
          return graphMaker.sval;
        }
        MessageWriter.E105.store(graphMaker);
      }
      else {
        graphMaker.pushBack();
      }
      return null;
    }

    //
    private String extractPattern() throws IOException {
      int token = graphMaker.nextToken();
      ItemKeys ik = graphMaker.getParser().getKey(token);
      if (ik == ItemKeys.C_COLON) {
        token = graphMaker.nextToken();
        if (token == '\"') {
          return graphMaker.sval;
        }
        MessageWriter.E109.store(graphMaker);
      }
      else {
        graphMaker.pushBack();
      }
      return null;
    }

    //
    private boolean handleNodeString(Operator op) throws IOException {
      String k = graphMaker.sval;
      if (k != null) {
        StringSymbol n = new StringSymbol(k, op);
        node.addToSyntax(n);
        return true;
      }
      MessageWriter.E106.store(graphMaker);
      return false;
    }

    //
    private boolean handleNodeOperator(ItemKeys ikey) throws IOException {
      Operator op = Operator.valueOf(ikey.name().substring(2));
      int token = graphMaker.nextToken();
      if (handleNodeOrLeaf(token, op)) {
        return true;
      }
      ItemKeys ik = graphMaker.getParser().getKey(token);
      if (ik == ItemKeys.C_PAR) {
        handleGroup(op);
        return true;
      }
      MessageWriter.E107.store(graphMaker);
      return false;
    }

    //
    private boolean setKeySymbol(KeySymbol n) throws IOException {
      int token = graphMaker.nextToken();
      ItemKeys ik = graphMaker.getParser().getKey(token);
      if (ik == null) {
        graphMaker.pushBack();
        return true;
      }
      switch (ik) {
        case C_COLON:
          token = graphMaker.nextToken();
          if (token == StreamTokenizer.TT_WORD) {
            n.setVarName(graphMaker.sval);
            token = graphMaker.nextToken();
            ik = graphMaker.getParser().getKey(token);
            if (ik == ItemKeys.C_COLON) {
              token = graphMaker.nextToken();
              if (token == StreamTokenizer.TT_WORD
                  && graphMaker.sval.equalsIgnoreCase("ParseRef")) {
                n.setParseRef(true);
                return true;
              }
              MessageWriter.E108.store(graphMaker);
            }
            else {
              graphMaker.pushBack();
            }
            return true;
          }
          MessageWriter.E108.store(graphMaker);
          break;
        case C_BRACKET:
          EnumSymbol nn = new EnumSymbol(n.getName(), Operator.NONE);
          if (handleEnum(nn)) {
            node.addToSyntax(nn);
            node.setType(AttKeys.ATT_ENUM);
            return false;
          }
          break;

        default:
          graphMaker.pushBack();
          return true;
      }
      return false;
    }

    //
    private boolean handleEnum(EnumSymbol nn) throws IOException {
      int token = graphMaker.nextToken();
      ItemKeys ik = graphMaker.getParser().getKey(token);
      switch (ik) {
        case C_QUOTE:
          if (graphMaker.sval.length() != 1) {
            MessageWriter.E106.store(graphMaker);
            break;
          }
        case C_2QUOTES:
          nn.setVarName(graphMaker.sval);
          token = graphMaker.nextToken();
          ik = graphMaker.getParser().getKey(token);
          if (ik == ItemKeys.C_BRACKET_END) {
            return true;
          }
          MessageWriter.E104.store(graphMaker);
          break;
        case C_BRACKET_END:
          return true;

        default:
          MessageWriter.E106.store(graphMaker);
          break;
      }
      return false;
    }

    //
    private void handleGroup(Operator op) throws IOException {
      GroupOfSymbols g = new GroupOfSymbols(node.getName()+"_"+op.name(), op);
      node.addToSyntax(g);
      new Automaton(graphMaker, g).process(false);
      node.setType(g.getType());
    }
  }

  /**
   * End of Definition state
   * 
   * @author dragan
   *
   */
  public class EndOfDefinitionState implements State {

    @Override
    public State perform() throws IOException {
      graphMaker.getGraph().addNode( node, action, graphMaker );
      graphMaker.getResult().printNode(node);
      return new InitialState();
    }
  }

  /**
   * Error state
   * 
   * @author dragan
   *
   */
  public class ErrorState implements State {

    @Override
    public State perform() throws IOException {
      graphMaker.getResult().printErroneousNode(node);
      ItemKeys ikey;
      int token;
      do {
        token = graphMaker.nextToken();
        ikey = graphMaker.getParser().getKey(token);
      }
      while (token != StreamTokenizer.TT_EOF
          && ikey != ItemKeys.C_SEMI
          && ikey != ItemKeys.C_BRACE_END);

      if (token == StreamTokenizer.TT_EOF || ikey == ItemKeys.C_BRACE_END) {
        return null;
      }
      return new InitialState();
    }
  }

  /**
   * Final state
   * 
   * @author dragan
   *
   */
  public class FinalState implements State {

    private boolean isOuterFinal;

    public FinalState(boolean b) {
      isOuterFinal = b;
    }

    @Override
    public State perform() throws IOException {
      if (isOuterFinal) {
        if (!graphMaker.getGraph().buildup(graphMaker.getResult())) {
          return new ErrorState();
        }
      }
      return null;
    }
  }
}
