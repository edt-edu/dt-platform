package fischertechnik_gateway.machines.tcp;

import fischertechnik_gateway.machines.message.Command;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TcpPLCConnection {
  protected final InetSocketAddress commandAddress;
  protected final InetSocketAddress notificationAddress;
  protected Socket notificationSocket;
  protected Socket commandSocket;
  protected List<Consumer<String>> notificationListeners = new ArrayList<>();
  protected Thread workerThread;

  public TcpPLCConnection(InetSocketAddress commandAddress, InetSocketAddress notificationAddress) {
    this.commandAddress = commandAddress;
    this.notificationAddress = notificationAddress;
  }

  public void connect() throws IOException {
    notificationSocket = new Socket(notificationAddress.getAddress(), notificationAddress.getPort());
    commandSocket = new Socket(commandAddress.getAddress(), commandAddress.getPort());
    startWorker();
  }

  private void startWorker() {
    if(workerThread != null && workerThread.isAlive()){
      workerThread.stop();
    }

    workerThread = new Thread(() -> {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(notificationSocket.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          for (Consumer<String> notificationListener : notificationListeners) {
            notificationListener.accept(line);
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
    workerThread.start();
  }

  public void close() throws IOException {
    if(workerThread != null && workerThread.isAlive()){
      workerThread.stop();
    }

    if(notificationSocket != null && !notificationSocket.isClosed()){
      notificationSocket.close();
    }

    if(commandSocket != null && !commandSocket.isClosed()){
      commandSocket.close();
    }
  }

  public void sendCommand(Command command) throws IOException {
    if(commandSocket == null || commandSocket.isClosed()){
      throw new IllegalStateException();
    }

    String s = command.toJson().toString();

    synchronized (commandSocket){
      IOUtils.copy(new StringReader(s), commandSocket.getOutputStream(), StandardCharsets.UTF_8);
    }
  }

  public void addNotificationListener(Consumer<String> onNotification){
    notificationListeners.add(onNotification);
  }
}
