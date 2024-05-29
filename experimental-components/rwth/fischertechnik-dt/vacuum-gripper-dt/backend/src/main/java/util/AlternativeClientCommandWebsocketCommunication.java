/* (c) https://github.com/MontiCore/monticore */
package util;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.MCFatalError;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import umlp.backendrte.command.*;
import umlp.backendrte.common.LoadingState;
import umlp.backendrte.common.Result;
import umlp.backendrte.service.websocket.WebSocketSessionManager;

import java.net.URI;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class AlternativeClientCommandWebsocketCommunication extends TextWebSocketHandler implements
    ClientCommandCommunication {
  private long maxWaitingTime = 6000; // 6 seconds
  private Optional<Long> participantId = Optional.empty();
  private WebSocketSession session;
  private final String uri;
  private final WebSocketHttpHeaders headers;
  private Map<Long, CommandResult<?>> messages = new HashMap<>();

  private List<CommandReceiveObserver> commandObserver = new ArrayList<>();
  private List<CommandResultReceiveObserver> commandResultObserver = new ArrayList<>();

  ExecutorService executor;
  private CountDownLatch initLatch;

  public AlternativeClientCommandWebsocketCommunication(String uri) {
    this(uri, new WebSocketHttpHeaders());
  }

  public AlternativeClientCommandWebsocketCommunication(String uri, WebSocketHttpHeaders headers) {
    this.uri = uri;
    this.headers = headers;
    executor = Executors.newFixedThreadPool(1);
  }

  public synchronized <T> CommandResult<T> sendCommandToServerAndReceiveResult(Command<T> command) {
    command.setParticipantId(participantId.get());
    CommandResult<T> result;
    try {
      String payload = CommandObjectMapper.serializeCommand(command);
      Log.info(String.format("Sending message to server: %s", payload), this.getClass().getName());
      boolean sendingSuccessful = WebSocketSessionManager.sendMessage(session, payload);
      if (!sendingSuccessful) {
        warnCouldNotReceive(command);
        return new SimpleResult<>(command.getId(), LoadingState.FAILED_TO_LOAD, command.getCommandType());
      }
      synchronized (this) {
        this.wait(maxWaitingTime);
      }

      if (messages.containsKey(command.getId())) {
        CommandResult<?> response = messages.get(command.getId());
        messages.remove(command.getId());
        result = (CommandResult<T>) response;
        Log.info(String.format("Received answer from server for command with id %s: %s", command.getId(), CommandObjectMapper.serializeResponse(result)), this.getClass().getName());
      } else {
        warnCouldNotReceive(command);
        return new SimpleResult<>(command.getId(), LoadingState.FAILED_TO_LOAD, command.getCommandType());
      }
    }
    catch (InterruptedException e) {
      warnCouldNotReceive(command);
      e.printStackTrace();
      return new SimpleResult<>(command.getId(), LoadingState.FAILED_TO_LOAD, command.getCommandType());
    }
    return result;
  }

  private static <T> void warnCouldNotReceive(Command<T> command) {
    Log.warn("Could not receive response from server for command id: " + command.getId());
  }

  public Result<Long> init() {
    initLatch = new CountDownLatch(2);
    StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
    try {
      this.session = WebSocketSessionManager.register(
          webSocketClient.doHandshake(this, headers, URI.create(uri)).get());
    }
    catch (InterruptedException e) {
      Log.warn(String.format("Could not establish a websocket connection to URI '%s'", uri));
      return Result.error();
    }
    catch (ExecutionException e) {
      Log.warn(String.format("Could not establish a websocket connection to URI '%s'", uri));
      return Result.error();
    }

    try {
      // Warte bis die Verbindung aufgebaut ist und eine Antwort eintrifft
      boolean await = initLatch.await(2, TimeUnit.SECONDS);
      if (await && this.participantId.isPresent()) {
        return Result.ok(this.participantId.get());
      }
      else {
        return Result.error();
      }
    }
    catch (InterruptedException e) {
      e.printStackTrace();
      return Result.error();
    }
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    initLatch.countDown();
  }

  @Override
  protected synchronized void handleTextMessage(WebSocketSession session, TextMessage message) {
    if (expectInitMessage()) {
      // Fall initialisierung der Verbindung
      try {
        this.participantId = Optional.of(Long.parseLong(message.getPayload()));
      }
      catch (NumberFormatException e) {
        this.participantId = Optional.empty();
      }
      initLatch.countDown();
    }
    else {
      // Fall Antwort eines Requests, oder Erhalt eines weitergeleiteten Commands zur
      // Datensynchronisierung.
      // TODO AHe, MH: Wasteful way of distinguishing commands and results
      Optional<CommandResult<?>> commandResultOptional = Optional.empty();
      try {
        commandResultOptional = CommandObjectMapper.deserializeResponse(message.getPayload());
      } catch (MCFatalError ex) {

      }
      Optional<Command<Object>> commandOptional = Optional.empty();
      try {
        commandOptional = CommandObjectMapper.deserializeCommand(message.getPayload());
      } catch (MCFatalError ex) {

      }

      if (commandResultOptional.isPresent()) { // TODO MH: Need to check for participantId
        // Fall Antwort eines Requests
        CommandResult<?> commandResult = commandResultOptional.get();
        messages.put(commandResult.getCommandId(), commandResult);
        for (CommandResultReceiveObserver observer : commandResultObserver) {
          observer.accept(commandResult);
        }
        synchronized (this) {
          notify();
        }
      }
      else if (commandOptional.isPresent()) {
        //Fall Antwort eines Erhalt eines weitergeleiteten Commands zur Datensynchronisierung
        Optional<Command<Object>> finalCommandOptional = commandOptional;
        executor.submit(() -> {
          for (Consumer<Command<Object>> consumer : commandObserver) {
            consumer.accept(finalCommandOptional.get());
          }
        });
      }
      else {
        synchronized (this) {
          notify();
        }
      }
    }
  }

  protected boolean expectInitMessage() {
    return initLatch.getCount() > 0;
  }

  public AlternativeClientCommandWebsocketCommunication onCommandReceived(CommandReceiveObserver observer) {
    this.commandObserver.add(observer);
    return this;
  }

  public AlternativeClientCommandWebsocketCommunication onCommandResultReceived(CommandResultReceiveObserver observer) {
    this.commandResultObserver.add(observer);
    return this;
  }

  public boolean isOpen() {
    return session.isOpen();
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    Log.warn(String.format("Websocket connection %s closed with status %s", session.getId(), status.toString()));
    WebSocketSessionManager.deregister(session);
    super.afterConnectionClosed(session, status);
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    Log.warn(String.format("Websocket %s transportation error", session.getId()));
    super.handleTransportError(session, exception);
  }
}
