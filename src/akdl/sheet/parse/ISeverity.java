package akdl.sheet.parse;

public interface ISeverity {
  static final public ISeverity WARNING = new ISeverity() {
    public String toString() {
      return "WARNING";
    }
  };
  static final public ISeverity ERROR = new ISeverity() {
    public String toString() {
      return "ERROR";
    }
  };
}
