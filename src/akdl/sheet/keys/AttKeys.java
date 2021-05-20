package akdl.sheet.keys;


public enum AttKeys {
  ATT_INTERFACE("INTERFACE"),
  ATT_CLASS("CLASS"),
  ATT_ENUM("ENUM");

  private String name;

  AttKeys(String n) {
    this.name = n;
  }

  public String getName() {
    return name;
  }
}
