package akdl.code.gen.java.persist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.MissingResourceException;

import akdl.sheet.parse.MessageWriter;
import akdl.sheet.parse.ParseResult;

public class TemplatesReader {

  private static final TemplatesReader instance = new TemplatesReader();

  private static final String TEMPLATE_FILE = "templates.txt";

  private Hashtable<String, StringBuilder> keys;

  private TemplatesReader() {
    keys = new Hashtable<>();
    gatherKeys();
  }

  public static TemplatesReader getInstance() {
    return instance;
  }

  //
  private void gatherKeys() {
    try {
      InputStream in = TemplatesReader.class.getClassLoader().getResourceAsStream(TEMPLATE_FILE);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String line = br.readLine();
      while (line != null) {
        boolean isStart = false;
        boolean isEnd = false;
        String key = null;
        while (!isStart && line != null) {
          isStart = line.startsWith("PARSER_") || line.startsWith("RUNTIME_");
          if (isStart) {
            int index = line.indexOf("=");
            key = line.substring(0, index);
          }
          line = br.readLine();
        }
        if (line != null) {
          StringBuilder sb = new StringBuilder();
          do {
            sb.append(line+"\n");
            line = br.readLine();
            isEnd = line.endsWith(key);
          }
          while (!isEnd);
          keys.put(key, sb);
        }
      }
      in.close();

    }
    catch (MissingResourceException | IOException e) {
    }
  }

  public StringBuilder getContent(String key) {
    return keys.get(key);
  }

  public String replace(String key, String value, String string) {
    StringBuilder content = new StringBuilder(keys.get(key));
    int index = content.indexOf(value);
    if (index < 0) {
      MessageWriter.W001.store(ParseResult.getInstance(), value, string);
    }
    else {
      while (index > 0) {
        content = content.replace(index, index+value.length(), string);
        index = content.indexOf(value);
      }
    }
    return content.toString();
  }
}
