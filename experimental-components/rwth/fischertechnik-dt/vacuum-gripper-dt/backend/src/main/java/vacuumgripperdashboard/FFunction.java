package vacuumgripperdashboard;

import java.util.Optional;

public class FFunction extends FFunctionTOP {
  public Optional<Channel> getInChannel(String name){
    for (Port p : getIn()) {
      if (p.getName().equals(name)) {
        Channel channel = p.getChannel();
        return Optional.of(channel);
      }
    }
    return Optional.empty();
  }

  public Optional<Channel> getOutChannel(String name){
    for (Port p : getOut()) {
      if (p.getName().equals(name)) {
        Channel channel = p.getChannel();
        return Optional.of(channel);
      }
    }
    return Optional.empty();
  }
}

