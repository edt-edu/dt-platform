package vacuumgripperdashboard.util;

import vacuumgripperdashboard.BooleanValue;
import vacuumgripperdashboard.BooleanValueObserver;

@FunctionalInterface
public interface BooleanValueChangeObserver extends BooleanValueObserver {
  @Override
  void notifySetContent(BooleanValue booleanValue, Boolean oldValue, Boolean o);
}
