package arbalest;

import java.util.Map;
import java.util.function.Function;

import static java.util.jar.Attributes.Name.MAIN_CLASS;

enum Symbol {
  application("Application"),
  logLevel("arbalest.log.level"),
  m2Repo("M2_REPO"),
  mainClass(MAIN_CLASS.toString()),
  userHome("user.home"),
  treeView("arbalest.dependency.tree", value -> value != null && !"false".equals(value));

  private String key;
  private Function converter;

  Symbol(String value) {
    this(value, x -> x);
  }

  Symbol(String value, Function converter) {
    this.key = value;
    this.converter = converter;
  }

  @SuppressWarnings("unchecked")
  public <T> T get(Map<String, String> values) {
    return (T) converter.apply(values.get(key));
  }

  @Override
  public String toString() {
    return key;
  }

}
