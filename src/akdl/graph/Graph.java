package akdl.graph;

import java.util.ArrayList;
import java.util.HashMap;

import akdl.graph.nodes.DefinitionNode;
import akdl.graph.nodes.EnumSymbol;
import akdl.graph.nodes.GroupOfSymbols;
import akdl.graph.nodes.KeySymbol;
import akdl.graph.nodes.elts.ASymbol;
import akdl.graph.nodes.elts.Operator;
import akdl.sheet.keys.AttKeys;
import akdl.sheet.parse.ISeverity;
import akdl.sheet.parse.MessageWriter;
import akdl.sheet.parse.ParseResult;

public class Graph {

  static public String DEFAULT_PARSER_PACKAGE = "com.parse.";
  static public String DEFAULT_RUNTIME_PACKAGE = "com.run.";

  private HashMap<String, DefinitionNode> map;
  private DefinitionNode root;
  private boolean isVisual;

  /**
   * constructor
   * @param b isVisual
   */
  public Graph(boolean b) {
    isVisual = b;
    map = new HashMap<String, DefinitionNode>();
  }

  /**
   * @return the root
   */
  public DefinitionNode getRoot() {
    return root;
  }

  /**
   * @param root the root to set
   */
  public void setRoot(DefinitionNode root) {
    this.root = root;
  }

  /**
   * @return the map
   */
  public HashMap<String, DefinitionNode> getMap() {
    return map;
  }

  /**
   * @param map the map to set
   */
  public void setMap(HashMap<String, DefinitionNode> map) {
    this.map = map;
  }

  /**
   * add a newly defined node to the nodes list
   * 
   * @param definitionNode
   * @param action 
   * @param graphMaker 
   */
  public void addNode(DefinitionNode definitionNode, Action action, GraphMaker graphMaker) {
    switch (action) {
      case ADD:
        map.put(definitionNode.getName(), definitionNode);
        break;
      case INSERT:
        DefinitionNode node = map.get(definitionNode.getName());
        if (node.getType() == AttKeys.ATT_INTERFACE
            || node.getType() == AttKeys.ATT_ENUM) {
          ASymbol nodeGroup = node.getSyntax().get(0);
          if (nodeGroup instanceof DefinitionNode) {
            ((DefinitionNode) nodeGroup).getSyntax().addAll(definitionNode.getSyntax());
            break;
          }
        }
        MessageWriter.E100.store(graphMaker, node.getName());
        break;

      default:
        break;
    }
  }

  /*
   * goes through the nodes list in order to make a graph out of it.
   * at the end, if all went right, a root node is set.
   * all error/warning/info messages are stored in the given {@link ParseResult} object
   * 
   * @param result
   */
  protected boolean buildup(ParseResult result) {
    if (result.hasErrors()) {
      MessageWriter.E300.store(result, "");
      return false;
    }
    if (!result.getUndefinedKeys().isEmpty()) {
      MessageWriter.E301.store(result, result.getUndefinedKeys().toString());
      return false;
    }
    boolean isOk = false;
    ArrayList<DefinitionNode> rootCandidates = new ArrayList<>();
    boolean found;
    for (String key : map.keySet()) {
      found = false;
      DefinitionNode target = map.get(key);
      for (DefinitionNode n : map.values()) {
        if (!n.equals(target)) {
          if (isTargetUsed(n, target, key)) {
            found = true;
          }
        }
      }
      if (!found) {
        rootCandidates.add(target);
        ArrayList<ASymbol> definition = target.getSyntax();
        for (ASymbol sym : definition) {
          if (sym instanceof KeySymbol) {
            KeySymbol ksym = (KeySymbol) sym;
            if (ksym.getDefinition() == null) {
              ksym.setDefinition(map.get(ksym.getName()));
            }
          }
        }
      }
    }
    ArrayList<DefinitionNode> enumSymbols = extractEnums(rootCandidates);
    if (rootCandidates.isEmpty()) {
      result.addMessageText(ISeverity.ERROR, "No root found", 0);
    }
    else if (rootCandidates.size() == 1) {
      root = rootCandidates.get(0);
      if (isVisual) {
        for (DefinitionNode node : enumSymbols) {
          root.getSyntax().add(0, getDetachedWrapper(node));
        }
      }
      root.propagatePackagesAndSetType(DEFAULT_PARSER_PACKAGE, DEFAULT_RUNTIME_PACKAGE);
      isOk = true;
    }
    else {
      String msg = "There are " + rootCandidates.size() + " root candidates."
          + " Probably some of your nodes are erroneous or not declared.\n"
          + "\tHere is the list:";
      for (int i=0; i<rootCandidates.size(); i++) {
        msg += "\n\t\t" + rootCandidates.get(i).getName();
      }
      result.addMessageText(ISeverity.ERROR, msg, 0);
    }
    result.dump(root);
    for (ASymbol sym : root.getSyntax()) {
      sym.setParent(root);
      if (sym instanceof KeySymbol) {
        KeySymbol ksym = (KeySymbol) sym;
        backChain(ksym, ksym.getDefinition().getSyntax());
      }
    }
    return isOk;
  }

  //
  private void backChain(KeySymbol parent, ArrayList<ASymbol> syntax) {
    for (ASymbol sym : syntax) {
      if (sym.getParent() != null) {
        break;
      }
      sym.setParent(parent);
      if (sym instanceof GroupOfSymbols) {
        GroupOfSymbols gsym = (GroupOfSymbols)sym;
        backChain(parent, gsym.getSyntax());
      }
      else if (sym instanceof KeySymbol) {
        KeySymbol ksym = (KeySymbol) sym;
        if (ksym.getDefinition() != null) {
          backChain(ksym, ksym.getDefinition().getSyntax());
        }
      }
    }
  }

  //
  private ASymbol getDetachedWrapper(DefinitionNode node) {
    GroupOfSymbols detach = new GroupOfSymbols("DETACHED", Operator.DETACHED);
    KeySymbol ksym = new KeySymbol(node.getName(), Operator.NONE, false);
    ksym.setDefinition(node);
    detach.addToSyntax(ksym);
    return detach;
  }

  //
  private ArrayList<DefinitionNode> extractEnums(ArrayList<DefinitionNode> rootCandidates) {
    ArrayList<DefinitionNode> roots = new ArrayList<>();
    ArrayList<DefinitionNode> enumSymbols = new ArrayList<>();
    for (DefinitionNode node : rootCandidates) {
      if (isEnum(node)) {
        enumSymbols.add(node);
      }
      else {
        roots.add(node);
      }
    }
    if (!enumSymbols.isEmpty()) {
      rootCandidates.clear();
      rootCandidates.addAll(roots);
    }
    return enumSymbols;
  }

  //
  private boolean isTargetUsed(DefinitionNode n, DefinitionNode target, String key) {
    boolean found = false;
    ArrayList<ASymbol> definition = n.getSyntax();
    for (ASymbol sym : definition) {
      if (sym instanceof GroupOfSymbols) {
        found |= isTargetUsed((DefinitionNode)sym, target, key);
      }
      else if (sym instanceof KeySymbol && ((KeySymbol) sym).getName().equals(key)) {
        KeySymbol ksym = (KeySymbol) sym;
        if (!ksym.isReference() && !ksym.isParseRef() || !isEnum(target)) {
          found = true;
        } 
        ksym.setDefinition(target);
      }
    }
    return found;
  }

  //
  private boolean isEnum(DefinitionNode target) {
    ArrayList<ASymbol> definition = target.getSyntax();
    if (definition.size() > 1 || !(definition.get(0) instanceof GroupOfSymbols)) {
      return false;
    }
    ASymbol sym = ((GroupOfSymbols)definition.get(0)).getSyntax().get(0);
    return sym instanceof EnumSymbol;
  }
}
