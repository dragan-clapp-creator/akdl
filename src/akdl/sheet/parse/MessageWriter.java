package akdl.sheet.parse;

import java.io.StreamTokenizer;

import akdl.code.gen.java.persist.PlaceHolder;
import akdl.graph.GraphMaker;
import akdl.sheet.DefinitionSheetMaker;

public enum MessageWriter {
  E001("wrong syntax for header information", ISeverity.ERROR),
  E002("wrong syntax for destination name", ISeverity.ERROR),
  E003("definition should begin and end with a brace", ISeverity.ERROR),
  E004("a constant value should be given: ", ISeverity.ERROR),
  E005("incorrect character %0%", ISeverity.ERROR),

  E100("incorrect token", ISeverity.ERROR),
  E101("a braquet ([) or an equal (=) sign was awaited here", ISeverity.ERROR),
  E102("incorrect node name", ISeverity.ERROR),
  E103("an end braquet (]) was awaited here", ISeverity.ERROR),
  E104("incorrect attribute", ISeverity.ERROR),
  E105("a word was awaited here after the colon (:) sign", ISeverity.ERROR),
  E106("wrong keyword name", ISeverity.ERROR),
  E107("wrong item after operator", ISeverity.ERROR),
  E108("wrong constant string", ISeverity.ERROR),
  E109("a pattern constant was awaited here after the colon (:) sign", ISeverity.ERROR),

  E200("wrong node found: %0%", ISeverity.ERROR),
  E201("wrong symbol found: %0%", ISeverity.ERROR),
  E202("wrong operator found: %0% for symbol %1%", ISeverity.ERROR),

  E300("Check the errors before generating the code", ISeverity.ERROR),
  E301("The following keywords are not defined: %0%\nPlease define them before generating the code", ISeverity.ERROR),

  W001("No %0% found on %1% skeleton", ISeverity.WARNING),
  W002("Could not perform %0% addition", ISeverity.WARNING);

  private String message;
  private ISeverity severity;

  private MessageWriter(String msg, ISeverity s) {
    message = msg;
    severity = s;
  }

  public void store(GraphMaker gm) {
    gm.getResult().addMessageText(severity, message, gm.lineno());
  }

  public void store(GraphMaker gm, String msg) {
    gm.getResult().addMessageText(severity, message.replaceAll("%0%", msg), gm.lineno());
  }

  public void store(DefinitionSheetMaker dm, ParseResult res) {
    res.addMessageText(severity, message, dm.lineno());
  }

  public void store(StreamTokenizer dm, ParseResult res, String msg) {
    res.addMessageText(severity, message.replaceAll("%0%", msg), dm.lineno());
  }

  public void store(ParseResult res, PlaceHolder ph, String arg1) {
    store(res, ph.name(), arg1);
  }

  public void store(ParseResult res, String arg0, String arg1) {
    if (res != null) {
      String msg = message.replaceAll("%0%", arg0);
      msg = msg.replaceAll("%1%", arg1);
      res.addMessageText(severity, msg);
    }
  }

  public void store(ParseResult res, String msg) {
    res.addMessageText(severity, message.replaceAll("%0%", msg));
  }
}
