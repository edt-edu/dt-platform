package vacuum_gripper.VacuumGripperTypes;

public class EncoderPulses extends EncoderPulsesTOP {

  public EncoderPulses(boolean stopped,boolean signal1Leading,boolean rising) {
    super(stopped, signal1Leading, rising);
    setStopped(stopped);
    setSignal1Leading(signal1Leading);
    setRising(rising);
  }

  @Override
  public boolean equals(Object obj) {
    if(!(obj instanceof EncoderPulses)){
      return false;
    }

    EncoderPulses other = (EncoderPulses) obj;

    if(!stopped && other.isStopped()){
      return true;
    }

    return signal1Leading == other.isSignal1Leading()
        && rising == other.isRising();
  }

  @Override
  public String toString() {
    return "EncoderPulses{" +
        "stopped=" + stopped +
        ", signal1Leading=" + signal1Leading +
        ", rising=" + rising +
        '}';
  }
}
