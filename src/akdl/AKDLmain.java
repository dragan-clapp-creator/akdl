package akdl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import akdl.sheet.DefinitionSheet;
import akdl.sheet.DefinitionSheetMaker;
import akdl.sheet.parse.ParseResult;




public class AKDLmain {

  public AKDLmain(String filename) {
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    if (args == null || args.length != 1) {
      printUsage();
    }
    else {
      String path = args[0];
      try {
        DefinitionSheetMaker mparser = new DefinitionSheetMaker(new FileReader(new File(path)), false);
        ParseResult result = ParseResult.getInstance();
        mparser.createDefinitionSheet(result);
        if (result.hasErrors()) {
          for (String msg : result.getMessages()) {
            System.err.println(msg);
          }
        }
        else {
          mparser.serializeLL();
          DefinitionSheet definitionSheet = mparser.getDefinitionSheet();
          definitionSheet.generateCode(mparser.getGraph().getRoot(), result, false);
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static void printUsage() {
    System.out.println("USAGE:");
    System.out.println("-----");
    System.out.println("\tjava akdl.AKDLmain <def>");
    System.out.println("\t\twhere <def> is/are your *.DEF file(s).");
  }
}
