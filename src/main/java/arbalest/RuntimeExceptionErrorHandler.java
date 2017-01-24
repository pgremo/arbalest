package arbalest;

import org.eclipse.aether.impl.DefaultServiceLocator;

public class RuntimeExceptionErrorHandler extends DefaultServiceLocator.ErrorHandler {
  @Override
  public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
    throw new RuntimeException(exception);
  }
}
