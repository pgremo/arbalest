package arbalest;

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.RepositoryEvent;

import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static java.util.logging.Logger.getLogger;

public class LoggingRepositoryListener extends AbstractRepositoryListener {

  private Logger logger = getLogger(LoggingRepositoryListener.class.getName());

  public void artifactDeployed(RepositoryEvent event) {
    logger.log(FINE, "Deployed {0} to {1}", new Object[]{event.getArtifact(), event.getRepository()});
  }

  public void artifactDeploying(RepositoryEvent event) {
    logger.log(FINE, "Deploying {0} to {1}", new Object[]{event.getArtifact(), event.getRepository()});
  }

  @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
  public void artifactDescriptorInvalid(RepositoryEvent event) {
    logger.log(FINE, "Invalid artifact descriptor for {0} : {1}", new Object[]{event.getArtifact(), event.getException().getMessage()});
  }

  public void artifactDescriptorMissing(RepositoryEvent event) {
    logger.log(FINE, "Missing artifact descriptor for {0}", new Object[]{event.getArtifact()});
  }

  public void artifactInstalled(RepositoryEvent event) {
    logger.log(FINE, "Installed {0} to {1}", new Object[]{event.getArtifact(), event.getRepository()});
  }

  public void artifactInstalling(RepositoryEvent event) {
    logger.log(FINE, "Installing {0} to {1}", new Object[]{event.getArtifact(), event.getRepository()});
  }

  public void artifactResolved(RepositoryEvent event) {
    logger.log(FINE, "Resolved {0} from {1}", new Object[]{event.getArtifact(), event.getRepository()});
  }

  public void artifactDownloading(RepositoryEvent event) {
    logger.log(FINE, "Downloading {0} from {1}", new Object[]{event.getArtifact(), event.getRepository()});
  }

  public void artifactDownloaded(RepositoryEvent event) {
    logger.log(FINE, "Downloaded {0} from {1}", new Object[]{event.getArtifact(), event.getRepository()});
  }

  public void artifactResolving(RepositoryEvent event) {
    logger.log(FINE, "Resolving {0}", new Object[]{event.getArtifact()});
  }

  public void metadataDeployed(RepositoryEvent event) {
    logger.log(FINE, "Deployed {0} to {1}", new Object[]{event.getMetadata(), event.getRepository()});
  }

  public void metadataDeploying(RepositoryEvent event) {
    logger.log(FINE, "Deploying {0} to {1}", new Object[]{event.getMetadata(), event.getRepository()});
  }

  public void metadataInstalled(RepositoryEvent event) {
    logger.log(FINE, "Installed {0} to {1}", new Object[]{event.getMetadata(), event.getFile()});
  }

  public void metadataInstalling(RepositoryEvent event) {
    logger.log(FINE, "Installing {0} to {1}", new Object[]{event.getMetadata(), event.getFile()});
  }

  public void metadataInvalid(RepositoryEvent event) {
    logger.log(FINE, "Invalid {0}", new Object[]{event.getMetadata()});
  }

  public void metadataResolved(RepositoryEvent event) {
    logger.log(FINE, "Resolved {0} from {1}", new Object[]{event.getMetadata(), event.getRepository()});
  }

  public void metadataResolving(RepositoryEvent event) {
    logger.log(FINE, "Resolving {0} from {1}", new Object[]{event.getMetadata(), event.getRepository()});
  }

}
