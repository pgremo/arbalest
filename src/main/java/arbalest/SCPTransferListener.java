package arbalest;

import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferResource;

import java.io.PrintStream;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Math.*;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;

class SCPTransferListener extends AbstractTransferListener {

  private Clock clock;
  private PrintStream out;
  private Map<TransferResource, LocalDateTime> processing = new ConcurrentHashMap<>();

  SCPTransferListener(PrintStream out) {
    this(Clock.systemDefaultZone(), out);
  }

  private SCPTransferListener(Clock clock, PrintStream out) {
    this.clock = clock;
    this.out = out;
  }

  private static final String[] scale = {"K", "M", "G", "T", "P", "E"};
  private static final int unit = 1024;

  private static String humanReadableByteCount(long bytes) {
    if (bytes < unit) return bytes + " B";
    int exp = (int) (log(bytes) / log(unit));
    return format("%.1f %sB", bytes / pow(unit, exp), scale[exp - 1]);
  }

  @Override
  public void transferInitiated(TransferEvent event) throws TransferCancelledException {
    processing.put(event.getResource(), now(clock));
  }

  @Override
  public void transferStarted(TransferEvent event) throws TransferCancelledException {

  }

  @Override
  public void transferProgressed(TransferEvent event) throws TransferCancelledException {
    // Percent amount rate/s ETA
    long rate = event.getTransferredBytes() / max(1, SECONDS.between(processing.get(event.getResource()), now(clock)));
    out.printf("%1$s%% %2$s, %3$s/s, %4$2tM:%4$2tS ETA",
      event.getTransferredBytes() / event.getResource().getContentLength(),
      humanReadableByteCount(event.getTransferredBytes()),
      rate,
      rate == 0 ? Long.MAX_VALUE : event.getResource().getContentLength() / rate);
  }

  @Override
  public void transferCorrupted(TransferEvent event) throws TransferCancelledException {

  }

  @Override
  public void transferSucceeded(TransferEvent event) {
    processing.remove(event.getResource());
  }

  @Override
  public void transferFailed(TransferEvent event) {
    processing.remove(event.getResource());
  }
}
