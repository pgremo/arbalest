package arbalest;

import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;
import org.eclipse.aether.transfer.TransferResource;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class SCPTransferListenerTest {

  @Test
  public void shouldProgress() throws TransferCancelledException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);
    TransferListener listener = new SCPTransferListener(out);
    TransferEvent.Builder builder = new TransferEvent.Builder(new DefaultRepositorySystemSession(), new TransferResource("", "testResource", new File("/tmp/testResource"), RequestTrace.newChild(null, null)));
    listener.transferInitiated(builder.build());
    listener.transferProgressed(builder.build());
    System.out.println(new String(bytes.toByteArray()));
  }

}