package arbalest;

import org.apache.maven.settings.Settings;
import org.eclipse.aether.repository.*;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.eclipse.aether.util.repository.DefaultAuthenticationSelector;
import org.eclipse.aether.util.repository.DefaultMirrorSelector;
import org.eclipse.aether.util.repository.DefaultProxySelector;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static arbalest.Configuration.getProperties;
import static arbalest.Symbol.m2Repo;
import static arbalest.Symbol.userHome;
import static java.lang.System.getenv;
import static java.nio.file.Files.createDirectories;
import static java.util.Collections.singletonList;

public class Repositories {
  private static final RemoteRepository central = new RemoteRepository.Builder("central", "default", "http://central.maven.org/maven2/").build();

  public static AuthenticationSelector getAuthenticationSelector(Settings settings) {
    return settings.getServers().stream()
      .reduce(
        new DefaultAuthenticationSelector(),
        (x, y) -> x.add(y.getId(), new AuthenticationBuilder().addUsername(y.getUsername()).addPassword(y.getPassword()).addPrivateKey(y.getPrivateKey(), y.getPassphrase()).build()),
        (x, y) -> x);
  }

  public static ProxySelector getProxySelector(Settings settings) {
    return settings.getProxies().stream()
      .reduce(
        new DefaultProxySelector(),
        (x, y) -> x.add(new Proxy(y.getProtocol(), y.getHost(), y.getPort(), new AuthenticationBuilder().addUsername(y.getUsername()).addPassword(y.getPassword()).build()), y.getNonProxyHosts()),
        (x, y) -> x);
  }

  public static MirrorSelector getMirrorSelector(Settings settings) {
    return settings.getMirrors().stream()
      .reduce(
        new DefaultMirrorSelector(),
        (x, y) -> x.add(y.getId(), y.getUrl(), y.getLayout(), false, y.getMirrorOf(), y.getMirrorOfLayouts()),
        (x, y) -> x);
  }

  public static List<RemoteRepository> getRepositories() {
    return singletonList(getCentral());
  }

  public static RemoteRepository getCentral() {
    return central;
  }

  public static LocalRepository getLocalRepository(Settings settings) throws IOException {
    Path path = null;

    String home = settings.getLocalRepository();
    if (home != null) {
      path = Paths.get(home);
    }

    if (path == null) {
      home = m2Repo.get(getenv());
      if (home != null) {
        path = Paths.get(home);
      }
    }

    if (path == null) {
      home = userHome.get(getProperties());
      if (home != null) {
        path = Paths.get(home, ".m2", "repository");
      }
    }

    if (path == null) {
      throw new NullPointerException("can not determine local repository");
    }

    createDirectories(path);

    return new LocalRepository(path.toFile());
  }
}
