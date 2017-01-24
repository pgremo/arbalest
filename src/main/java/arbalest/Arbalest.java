package arbalest;

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static arbalest.Configuration.getAttributes;
import static arbalest.Configuration.getProperties;
import static arbalest.Repositories.*;
import static arbalest.Symbol.mainClass;
import static arbalest.Symbol.treeView;
import static arbalest.TreePrinter.print;
import static java.io.File.pathSeparator;
import static java.lang.String.format;
import static java.lang.System.*;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.apache.maven.repository.internal.MavenRepositorySystemUtils.newServiceLocator;
import static org.apache.maven.repository.internal.MavenRepositorySystemUtils.newSession;
import static org.eclipse.aether.util.artifact.JavaScopes.RUNTIME;
import static org.eclipse.aether.util.filter.DependencyFilterUtils.classpathFilter;

public class Arbalest {
  static {
    new LoggingConfiguration();
  }

  public static void main(String... args) throws Exception {
    List<String> arguments = new ArrayList<>(asList(args));
    Map<String, String> attributes = getAttributes(Arbalest.class.getProtectionDomain().getCodeSource().getLocation());
    File globalSettingsFile = Paths.get(getProperty("maven.home", getProperty("user.dir", "")), "conf", "settings.xml").toFile();
    File userSettingsFile = Paths.get(getProperty("user.home"), ".m2", "settings.xml").toFile();

    Properties systemProperties = new Properties();
    systemProperties.putAll(getProperties());

    SettingsBuildingRequest settingsRequest = new DefaultSettingsBuildingRequest();
    settingsRequest.setGlobalSettingsFile(globalSettingsFile);
    settingsRequest.setUserSettingsFile(userSettingsFile);
    settingsRequest.setSystemProperties(systemProperties);
    settingsRequest.setUserProperties(new Properties());

    DefaultServiceLocator locator = newServiceLocator();
    locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
    locator.addService(TransporterFactory.class, FileTransporterFactory.class);
    locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
    locator.setErrorHandler(new RuntimeExceptionErrorHandler());

    Settings settings = new DefaultSettingsBuilderFactory().newInstance().build(settingsRequest).getEffectiveSettings();

    RepositorySystem repositorySystem = locator.getService(RepositorySystem.class);

    DefaultRepositorySystemSession session = newSession();
    session.setMirrorSelector(getMirrorSelector(settings));
    session.setProxySelector(getProxySelector(settings));
    session.setAuthenticationSelector(getAuthenticationSelector(settings));
    session.setLocalRepositoryManager(repositorySystem.newLocalRepositoryManager(session, getLocalRepository(settings)));
    session.setTransferListener(new ConsoleMavenTransferListener(out));
    session.setRepositoryListener(new LoggingRepositoryListener());

    String application = Symbol.application.get(attributes);
    if (application == null) application = arguments.remove(0);
    Artifact artifact = new DefaultArtifact(application);

    CollectRequest collectRequest = new CollectRequest();
    collectRequest.setRoot(new Dependency(artifact, RUNTIME));
    collectRequest.setRepositories(repositorySystem.newResolutionRepositories(session, getRepositories()));

    if (treeView.get(getProperties())) {
      print(repositorySystem.collectDependencies(session, collectRequest).getRoot(), "", true);
    } else {
      DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, classpathFilter(RUNTIME));
      DependencyResult dependencyResult = repositorySystem.resolveDependencies(session, dependencyRequest);
      String classpath = dependencyResult.getArtifactResults().stream().map(x -> x.getArtifact().getFile().toPath().toString()).collect(joining(pathSeparator));

      Map<String, String> targetAttributes = getAttributes(dependencyResult.getRoot().getArtifact().getFile().toURI().toURL());
      String name = mainClass.get(targetAttributes);
      if (name == null)
        throw new IllegalArgumentException(format("No %s defined in %s", mainClass, dependencyResult.getRoot().getArtifact()));

      List<String> commandLine = new ArrayList<>();
      commandLine.add("java");
      commandLine.add("-cp");
      commandLine.add(classpath);
      commandLine.add(name);
      commandLine.addAll(arguments);

      exit(new ProcessBuilder().inheritIO().command(commandLine).start().waitFor());
    }
  }

}
