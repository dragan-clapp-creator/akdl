package akdl.sheet.keys;


public enum DefKeys {
  KEY_SAVE_DEF("savedef"),        // save (serialize) definitions to a file
  KEY_LOAD_DEF("loaddef"),        // load (deserialize) definitions from another file
  KEY_USE_DEF("usedef"),          // marks a definition set to pick-up for current graph (but not to integrate in extenso)
  KEY_JAVA_DEF("java_def"),       // marks a java definition
  KEY_DESTINATION("destination"), // generated code destination
  KEY_VERBOSITY("verbosity"),
  KEY_DEBUG("debug");

  private String name;

  DefKeys(String n) {
    this.name = n;
  }

  public String getName() {
    return name;
  }
}
