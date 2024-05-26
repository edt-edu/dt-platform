package vacuumgripperdashboard.util;

import vacuumgripperdashboard.FunctionStream;
import vacuumgripperdashboard.FunctionStreamObserver;

@FunctionalInterface
public interface ValueAddedObserver extends FunctionStreamObserver {
  void notifyAddValue(FunctionStream functionStream, long arg, int indexInList);
}
