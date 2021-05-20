package akdl.code.gen.java.persist;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileGenerator {

  private static FileGenerator instance;

  private String destination;

  static public FileGenerator getInstance(String dest) {
    if (instance == null) {
      instance = new FileGenerator(dest);
    }
    return instance;
  }

  private FileGenerator(String dest) {
    this.destination = dest;
  }

  public FileWriter persist(String pack, String name) throws IOException {
    String p = pack;
    if (pack != null) {
      p = destination + "/" + pack.replace(".", "/");
    }
    File f = new File(p);
    f.mkdirs();
    String dest = p + "/" + name + ".java";
    return new FileWriter(new File(dest));
  }
}
