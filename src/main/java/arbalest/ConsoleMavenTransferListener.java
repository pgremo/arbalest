package arbalest;

import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferResource;

import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.joining;
import static org.eclipse.aether.transfer.TransferEvent.RequestType.PUT;

class ConsoleMavenTransferListener extends AbstractTransferListener {

  private PrintStream out;
  private Map<TransferResource, Long> downloads = new ConcurrentHashMap<>();

  ConsoleMavenTransferListener(PrintStream out) {
    this.out = out;
  }

  @Override
  public void transferInitiated(TransferEvent event) {
    String message = event.getRequestType() == PUT ? "Uploading" : "Downloading";
    out.printf("%s: %s%s%n", message, event.getResource().getRepositoryUrl(), event.getResource().getResourceName());
  }

  @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
  @Override
  public void transferCorrupted(TransferEvent event) throws TransferCancelledException {
    TransferResource resource = event.getResource();
    out.printf("[WARNING] %s for %s%s%n", event.getException().getMessage(), resource.getRepositoryUrl(), resource.getResourceName());
  }

  @Override
  public void transferProgressed(TransferEvent event) throws TransferCancelledException {
    downloads.put(event.getResource(), event.getTransferredBytes());
    String collected = downloads.entrySet().stream().filter(x -> x.getValue() != null).map(x -> getStatus(x.getValue(), x.getKey().getContentLength())).collect(joining(" "));
    out.printf("%-64s\r", collected);
  }

  private long toKB(long bytes) {
    return (bytes + 1023) / 1024;
  }

  private String getStatus(long complete, long total) {
    if (total >= 1024) return toKB(complete) + "/" + toKB(total) + " KB ";
    else if (total >= 0) return complete + "/" + total + " B ";
    else if (complete >= 1024) return toKB(complete) + " KB ";
    else return complete + " B ";
  }

  @Override
  public void transferSucceeded(TransferEvent event) {
    transferCompleted(event);

    TransferResource resource = event.getResource();
    long contentLength = event.getTransferredBytes();
    if (contentLength >= 0) {
      String type = event.getRequestType() == PUT ? "Uploaded" : "Downloaded";
      String len = contentLength >= 1024 ? toKB(contentLength) + " KB" : contentLength + " B";

      double kbPerSec = 0.0;
      long duration = currentTimeMillis() - resource.getTransferStartTime();
      if (duration > 0) kbPerSec = (contentLength / 1024.0) / (duration / 1000.0);

      out.printf("%s: %s%s (%s at %0.0f KB/sec)%n", type, resource.getRepositoryUrl(), resource.getResourceName(), len, kbPerSec);
    }
  }

  private void transferCompleted(TransferEvent event) {
    downloads.remove(event.getResource());
    out.printf("%-64s\r", " ");
  }

  @Override
  public void transferFailed(TransferEvent event) {
    transferCompleted(event);
  }
}
