package akdl.graph.gen.proc;

import java.util.Stack;

import akdl.graph.gen.proc.group.AGroupHandler;
import akdl.graph.gen.proc.group.AmpersAndGroupHandler;
import akdl.graph.gen.proc.group.HutGroupHandler;
import akdl.graph.gen.proc.group.PlusGroupHandler;
import akdl.graph.gen.proc.group.SimpleGroupHandler;
import akdl.graph.gen.proc.group.StarGroupHandler;
import akdl.graph.nodes.elts.Operator;
import akdl.sheet.keys.AttKeys;

public class SubContext {

  static private Stack<SubContext> handlers = new Stack<SubContext>();

  static public SubContext peekSubContext() {
    return handlers.peek();
  }

  static public boolean isSubContextEmpty() {
    return handlers.isEmpty();
  }

  static public SubContext popSubContext() {
    return handlers.pop();
  }

  static public void pushSubContext(SubContext h) {
    handlers.push(h);
  }

  static public Stack<SubContext> getSubContexts() {
    return handlers;
  }

  //=================================================================

  private AGroupHandler handler;
  private AttKeys type;
  private int index;

  public SubContext(Operator op) {
    selectHandler(op);
  }

  /**
   * @return the type
   */
  public AttKeys getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(AttKeys type) {
    this.type = type;
  }


  /**
   * @return the handler
   */
  public AGroupHandler getHandler() {
    return handler;
  }

  /**
   * @param handler the handler to set
   */
  public void setHandler(AGroupHandler handler) {
    this.handler = handler;
  }

  /**
   * processor is selected according to the {@link Operator}
   * 
   * @param handler the handler to set
   */
  public void selectHandler(Operator operator) {
    this.handler = null;
    switch (operator) {
      case NONE:
        handler = new SimpleGroupHandler();
        break;
      case AMPERS:
        handler = new AmpersAndGroupHandler();
        break;
      case HUT:
        handler = new HutGroupHandler();
        break;
      case PLUS:
        handler = new PlusGroupHandler();
        break;
      case STAR:
        handler = new StarGroupHandler();
        break;
      default:
        handler = new SimpleGroupHandler();
        break;
    }
  }

  /**
   * @return the index
   */
  public int getIndex() {
    return index;
  }

  /**
   * @param index the index to set
   */
  public void setIndex(int index) {
    this.index = index;
  }
}
