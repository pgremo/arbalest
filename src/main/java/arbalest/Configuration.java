package arbalest;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.jar.JarInputStream;

import static java.util.stream.Collectors.toMap;

public class Configuration {
  public static Map<String, String> getAttributes(URL url) throws IOException {
    return new JarInputStream(url.openStream()).getManifest().getMainAttributes().entrySet().stream().collect(toMap(k -> k.getKey().toString(), v -> (String) v.getValue()));
  }

  @SuppressWarnings("unchecked")
  public static Map<String, String> getProperties() {
    return (Map<String, String>) (Object) System.getProperties();
  }
}
