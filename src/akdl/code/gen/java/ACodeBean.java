package akdl.code.gen.java;

import java.util.TreeSet;

import akdl.code.gen.java.persist.PlaceHolder;
import akdl.code.gen.java.persist.Template;
import akdl.sheet.parse.MessageWriter;
import akdl.sheet.parse.ParseResult;

abstract public class ACodeBean {

  private Template template;
  private String pack;
  private String clname;
  private StringBuilder skeleton;
  private TreeSet<String> imports;
  private TreeSet<PlaceHolder> left;
  private String name;

  /**
   * CONSTRUCTOR
   * @param p   package
   * @param n   class name
   * @param t   initial template to load to the skeleton
   */
  public ACodeBean(String p, String n, Template t) {
    setupClassInfo(p, n);
    imports = new TreeSet<String>();
    left = new TreeSet<PlaceHolder>();
    template = t;
    skeleton = new StringBuilder(template.getContent());
  }

  /**
   * CONSTRUCTOR
   * @param t
   * @param p
   * @param n
   */
  public ACodeBean(Template t, String p, String n) {
    template = t;
    pack = p;
    clname = n;
    name = n;
    skeleton = new StringBuilder(template.getContent());
    imports = new TreeSet<String>();
    left = new TreeSet<PlaceHolder>();
  }

  //
  private void setupClassInfo(String p, String n) {
    pack = p;
    int index = p.lastIndexOf('.');
    if (index > 0) {
      pack = p.substring(0, index);
    }
    if (p.length() == index+1) {
      clname = n;
    }
    else {
      String nm = p.substring(index+1);
      clname = nm;
    }
    name = n.toLowerCase();
  }

  /**
   * @return the skeleton
   */
  public StringBuilder getSkeleton() {
    return skeleton;
  }

  /**
   * @return the pack
   */
  public String getPack() {
    return pack;
  }

  /**
   * @return the clname
   */
  public String getClname() {
    return clname;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  public String getFullName() {
    return pack + "+ " + clname;
  }

  /**
   * add to left
   */
  public void addToLeft(PlaceHolder ph) {
    left.add(ph);
  }

  /**
   * replace all occurrences of given place holder by given code piece
   * 
   * @param placeHolder
   * @param code
   * @param result 
   */
  public void replaceAll(PlaceHolder ph, String code, ParseResult result) {
    if (code == null) {
      return;
    }
    int index = skeleton.indexOf(ph.getString());
    if (result != null && index < 0) {
      MessageWriter.W001.store(result, ph, clname);
    }
    else {
      while (index > 0) {
        skeleton = skeleton.replace(index, index+ph.getString().length(), code);
        index = skeleton.indexOf(ph.getString());
      }
    }
  }

  //
  private void replaceAll(String string, String code) {
    int index = skeleton.indexOf(string);
    while (index > 0) {
      skeleton = skeleton.replace(index, index+string.length(), code);
      index = skeleton.indexOf(string);
    }
  }

  public void removeCodeline(int level) {
    String str = PlaceHolder.CODELINE.getString()+level;
    int index = skeleton.indexOf(str);
    while (index > 0) {
      skeleton = skeleton.replace(index, index+str.length(), "");
      index = skeleton.indexOf(str);
    }
  }

  /**
   * insert given code piece before given place holder
   * 
   * @param placeHolder
   * @param code
   * @param result 
   */
  public void insert(PlaceHolder ph, String code, ParseResult result) {
    skeleton = insert(ph, code, result, skeleton);
  }

  /**
   * insert code to the code line of the given level
   * 
   * @param level
   * @param code
   * @param result
   */
  public void insert(int level, String code, ParseResult result) {
    replaceAll(PlaceHolder.LEVEL, ""+level, result);
    int index = skeleton.indexOf(PlaceHolder.CODELINE.getString()+level);
    if (index < 0) {
      MessageWriter.W001.store(result, PlaceHolder.CODELINE, clname);
    }
    else {
      skeleton = skeleton.insert(index, code);
    }
  }

  /**
   * replace the code line of the given level through code
   * 
   * @param level
   * @param code
   * @param result
   */
  public void replace(int level, String code, ParseResult result) {
    replaceAll(PlaceHolder.LEVEL, ""+level, result);
    String str = PlaceHolder.CODELINE.getString()+level;
    int index = skeleton.indexOf(str );
    if (index < 0) {
      MessageWriter.W001.store(result, PlaceHolder.CODELINE, clname);
    }
    else {
      skeleton = skeleton.replace(index, index+str.length(), code);
    }
  }

  //
  StringBuilder insert(PlaceHolder ph, String code, ParseResult result, StringBuilder skelet) {
    if (code == null || skelet == null) {
      return null;
    }
    int index = skelet.indexOf(ph.getString());
    if (index < 0) {
      MessageWriter.W001.store(result, ph, clname);
    }
    else {
      left.add(ph);
      return skelet.insert(index, code);
    }
    return skelet;
  }

  /**
   * 
   * @param p
   * @param n
   */
  public void addToImports(String p, String n) {
    if (!pack.equals(p.substring(0, p.length()-1))) {
      imports.add(p+n);
    }
    left.add(PlaceHolder.IMPORTS);
  }

  /**
   * removes all remained place holders left by {@link #insert(PlaceHolder, String, ParseResult)}
   */
  public void finalStep(ParseResult result) {
    for (String key : imports) {
      String imp = "import "+key+";\n";
      insert(PlaceHolder.IMPORTS, imp, null);
    }
    for (PlaceHolder ph : left) {
      replaceAll(ph, "", null);
    }
    replaceAll(PlaceHolder.IMPORTS, "", null);
    replaceAll(PlaceHolder.ATT, "", null);
    replaceAll(PlaceHolder.METHOD, "", null);
    replaceAll(PlaceHolder.IS_GRAPH, "", null);
    replaceAll(PlaceHolder.TRAVERSE, "", null);
    replaceAll(PlaceHolder.EXTEND, "", null);
    replaceAll(PlaceHolder.IMPLEMENT, "", null);
    replaceAll(PlaceHolder.INTERFACE.getString()+",", "");
    replace(0, "", result);
  }

  /**
   * @return the imports
   */
  public TreeSet<String> getImports() {
    return imports;
  }

  /**
   * @param imports the imports to set
   */
  public void setImports(TreeSet<String> imports) {
    this.imports = imports;
  }

  /**
   * @return the left
   */
  public TreeSet<PlaceHolder> getLeft() {
    return left;
  }

  /**
   * @param left the left to set
   */
  public void setLeft(TreeSet<PlaceHolder> left) {
    this.left = left;
  }

  /**
   * @return the template
   */
  public Template getTemplate() {
    return template;
  }

  /**
   * @param skeleton the skeleton to set
   */
  public void setSkeleton(StringBuilder skeleton) {
    this.skeleton = skeleton;
  }
}
