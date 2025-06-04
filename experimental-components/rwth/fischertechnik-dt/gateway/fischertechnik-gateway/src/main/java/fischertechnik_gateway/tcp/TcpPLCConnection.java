package fischertechnik_gateway.tcp;

import de.monticore.symboltable.serialization.JsonParser;
import fischertechnik_gateway.message.Command;
import fischertechnik_gateway.notifications.Notification;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class TcpPLCConnection {
  protected final InetSocketAddress commandAddress;
  protected final InetSocketAddress notificationAddress;
  protected Socket notificationSocket;
  protected Socket commandSocket;
  protected List<Consumer<Notification>> notificationListeners = new ArrayList<>();
  protected Thread workerThread;
  protected AtomicBoolean workerRunning = new AtomicBoolean(false);

  public TcpPLCConnection(InetSocketAddress commandAddress, InetSocketAddress notificationAddress) {
    this.commandAddress = commandAddress;
    this.notificationAddress = notificationAddress;
  }

  public void connect() throws IOException {
    notificationSocket = new Socket(notificationAddress.getAddress(), notificationAddress.getPort());
    commandSocket = new Socket(commandAddress.getAddress(), commandAddress.getPort());
    startWorker();
  }

  private void startWorker() throws IOException {
    if(workerThread != null && workerThread.isAlive()){
      workerRunning.set(false);
      while (workerThread.isAlive()){
          try {
              Thread.sleep(1L);
          } catch (InterruptedException e) {
              throw new IOException(e);
          }
      }
    }

    workerThread = new Thread(() -> {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(notificationSocket.getInputStream()))) {
        String line;
        while (((line = reader.readLine()) != null) && workerRunning.get()) {
          Notification n;
          try{
            n = Notification.fromJson(JsonParser.parseJsonObject(line));
          } catch (Exception e){
            System.out.println("Can not parse notification from following line\n" + line);
            continue;
          }

          for (Consumer<Notification> notificationListener : notificationListeners) {
            notificationListener.accept(n);
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

    workerRunning.set(true);
    workerThread.start();
  }

  public void close() throws IOException {
    if(workerThread != null && workerThread.isAlive()){
      workerRunning.set(false);
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
    if(!s.endsWith("\n")){
      s = s + "\n";
    }

    synchronized (commandSocket){
      IOUtils.copy(new StringReader(s), commandSocket.getOutputStream(), StandardCharsets.UTF_8);
    }
  }

  public void addNotificationListener(Consumer<Notification> onNotification){
    notificationListeners.add(onNotification);
  }
}
