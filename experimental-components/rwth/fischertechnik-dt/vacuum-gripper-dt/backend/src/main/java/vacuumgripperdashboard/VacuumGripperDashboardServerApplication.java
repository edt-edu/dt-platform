package vacuumgripperdashboard;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;

import arcbasis._ast.ASTComponentType;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import ma2fenix.MA2FenixTransformer;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcArtifactScope;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import umlp.backendrte.common.NotificationScope;
import vacuumgripperdashboard.util.SimpleMqtt;

import static vacuumgripperdashboard.VacuumGripperDashboardManager.*;

@SpringBootApplication(
    // We have our own data management
    exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages = {"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest", "vacuumgripperdashboard.service"}
)
@jsweet.lang.Erased
public class VacuumGripperDashboardServerApplication extends VacuumGripperDashboardServerApplicationTOP {
  public static void main(String[] args) {
    SpringApplication.run(VacuumGripperDashboardServerApplication.class, args);
  }

  @javax.annotation.PostConstruct
  public void init() throws IOException {
    super.init();
    initSimulator();
  }

  private void initSimulator() throws IOException {
    createFunctionsFromSimulationModel();

    FFunction vacuumGripperFunction = VacuumGripperDashboardManager.getFFunctionList().stream()
        .filter(f -> f.getName().equals("vacuumgripper"))
        .findFirst().get();

    VacuumGripperInput input = vacuumGripperInputBuilder()
        .verticalUp(booleanValueBuilder().content(false).build().get())
        .verticalDown(booleanValueBuilder().content(false).build().get())
        .horizontalForward(booleanValueBuilder().content(false).build().get())
        .horizontalBack(booleanValueBuilder().content(false).build().get())
        .rotationClockwise(booleanValueBuilder().content(false).build().get())
        .rotationCounterclockwise(booleanValueBuilder().content(false).build().get())
        .build().get();

    VacuumGripperOutput output = vacuumGripperOutputBuilder()
        .positionRotate(floatValueBuilder().content(0.0f).build().get())
        .positionHorizontal(floatValueBuilder().content(0.0f).build().get())
        .positionVertical(floatValueBuilder().content(0.0f).build().get())
        .build().get();

    getApp().setSimulationInput(input);
    getApp().setSimulationOutput(output);
    getApp().setVacuumGripperSimulation(vacuumGripperFunction);
    StepSimulator simulator = new StepSimulator(vacuumGripperFunction);

    VacuumGripperDashboardManager.addObserver(new VacuumGripperDashboardObserver() {
      @Override
      public void maybeNotifySimulatorStepAdded(SimulatorStep simulatorStep, NotificationScope scope) {
        simulator.step();
      }
    });

    PositionService positionService = new PositionService();

    output.getPositionRotate().addObserver(new FloatValueObserver() {
      @Override
      public void notifySetContent(FloatValue floatValue, Float oldValue, Float o) {
        positionService.updatePayloadPosition();
      }
    });

    output.getPositionVertical().addObserver(new FloatValueObserver() {
      @Override
      public void notifySetContent(FloatValue floatValue, Float oldValue, Float o) {
        positionService.updatePayloadPosition();
      }
    });

    output.getPositionHorizontal().addObserver(new FloatValueObserver() {
      @Override
      public void notifySetContent(FloatValue floatValue, Float oldValue, Float o) {
        positionService.updatePayloadPosition();
      }
    });

    //getApp().addObserver(new AppObserver() {
    //  @Override
    //  public void notifySetPayloadPosition(App app, PayloadPosition oldValue, PayloadPosition o) {
    //    System.out.println("### Changed payload position from " + oldValue + " to " + o);
    //  }
    //});

    try {
      initMqttConnector(simulator);
    } catch (MqttException e) {
      Log.warn("Can not connect to MQTT", e);
    }
  }

  private void initMqttConnector(StepSimulator simulator) throws MqttException {
    MqttClient client = new MqttClient(
        System.getenv().getOrDefault("MQTT_BROKER_ADDRESS", "tcp://localhost:1883"),
        "VacuumGripperDt-" + Math.abs(new Random().nextInt())
    );

    SimpleMqtt callbacks = new SimpleMqtt(client);
    client.setCallback(callbacks);
    client.connect();

    App app = getApp();
    VacuumGripperInput input = app.getSimulationInput();
    VacuumGripperOutput output = app.getSimulationOutput();

    String prefix = "/vacuum-gripper/2.5-Vac";
    callbacks.subscribeBool(prefix + "/verticalUp", b -> input.getVerticalUp().setContent(b));
    callbacks.subscribeBool(prefix + "/verticalDown", b -> input.getVerticalDown().setContent(b));
    callbacks.subscribeBool(prefix + "/horizontalForward", b -> input.getHorizontalForward().setContent(b));
    callbacks.subscribeBool(prefix + "/horizontalBack", b -> input.getHorizontalBack().setContent(b));
    callbacks.subscribeBool(prefix + "/rotationClockwise", b -> input.getRotationClockwise().setContent(b));
    callbacks.subscribeBool(prefix + "/rotationCounterclockwise", b -> input.getRotationCounterclockwise().setContent(b));

    callbacks.subscribeLong(prefix + "/counterHorizontal", i -> {
      float content = i / 20f;
      output.getPositionHorizontal().setContent(content);
      simulator.setHorizontalPosition(content);
    });
    callbacks.subscribeLong(prefix + "/counterVertical", i -> {
      float content = 100f - (i / 20f);
      output.getPositionVertical().setContent(content);
      simulator.setVerticalPosition(content);
    });
    callbacks.subscribeLong(prefix + "/counterRotation", i -> {
      float content = 180f - (i / 10f);
      content = Math.abs(content);
      content = content % 360f;
      output.getPositionRotate().setContent(content);
      simulator.setRotationPosition(content);
    });

    // TODO: if position is in either
    // - loading zone
    // - dropoff zone
    // the dt should send status packages to topic /vacuum-gripper/payload/position:
    // - "loading zone",
    // - "dropoff zone",
    // - "in transit"
  }

  private void createFunctionsFromSimulationModel() throws IOException {
    MontiArcTool montiArcTool = new MontiArcTool();
    montiArcTool.init();
    montiArcTool.initializeBasicTypes();
    montiArcTool.initializeTickEvent();

    Path symbolPath = getCDSymbolPath();
    MontiArcMill.globalScope().setSymbolPath(new MCPath(symbolPath));

    List<ASTMACompilationUnit> asts = loadAllMontiArcModelsFromResources();
    montiArcTool.runAfterParsingTrafos(asts);
    Collection<IMontiArcArtifactScope> artifactScopes = montiArcTool.createSymbolTable(asts);
    for (IMontiArcArtifactScope as : artifactScopes) {
      MontiArcMill.globalScope().addSubScope(as);
    }

    montiArcTool.runSymbolTablePhase2(asts);
    montiArcTool.runSymbolTablePhase3(asts);
    montiArcTool.runAfterSymbolTablePhase3Trafos(asts);
    montiArcTool.runDefaultCoCos(asts);
    montiArcTool.runAdditionalCoCos(asts);

    ASTComponentType componentType = asts.stream()
        .map(ASTMACompilationUnit::getComponentType)
        .filter(ct -> ct.getName().equals("VacuumGripper"))
        .findFirst().get();

    new MA2FenixTransformer().transform(componentType);

    assert !VacuumGripperDashboardManager.getFFunctionList().isEmpty() : "No functions where created from Simulation model!";
  }

  private Path getCDSymbolPath() {
    try {
      URI uri = getClass().getResource("/vacuum_gripper/EncoderMotor.arc").toURI();
      System.out.println(uri);
      if (uri.toString().contains("!")) {
        final String[] parts = uri.toString().split("!");
        final FileSystem fs = FileSystems.newFileSystem(URI.create(parts[0]), Collections.emptyMap());
        Path path = fs.getPath(parts[1]);
        return path;
      } else {
        return Path.of(uri).resolve("../../").toAbsolutePath();
      }
    } catch (Exception nonRecoverable) {
      throw new RuntimeException(nonRecoverable);
    }
  }

  private List<ASTMACompilationUnit> loadAllMontiArcModelsFromResources() throws IOException {
    return List.of(
        loadMontiArcModelFromResources("/vacuum_gripper/EncoderMotor.arc"),
        loadMontiArcModelFromResources("/vacuum_gripper/LinearAxis.arc"),
        loadMontiArcModelFromResources("/vacuum_gripper/LinearAxisState.arc"),
        loadMontiArcModelFromResources("/vacuum_gripper/RotationalAxis.arc"),
        loadMontiArcModelFromResources("/vacuum_gripper/RotationalAxisState.arc"),
        loadMontiArcModelFromResources("/vacuum_gripper/SignalToMotorDirection.arc"),
        loadMontiArcModelFromResources("/vacuum_gripper/VacuumGripper.arc")
    );
  }

  private ASTMACompilationUnit loadMontiArcModelFromResources(String modelLocation) throws IOException {
    InputStream is = Objects.requireNonNull(getClass().getResourceAsStream(modelLocation));
    return MontiArcMill.parser().parse(new InputStreamReader(is)).orElseThrow(() -> new IllegalStateException("Can not parse Model from resources: " + modelLocation));
  }
}