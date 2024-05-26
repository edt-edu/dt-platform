package vacuumgripperdashboard;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import arcbasis._ast.ASTComponentType;
import de.monticore.io.paths.MCPath;
import ma2fenix.MA2FenixTransformer;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._symboltable.IMontiArcArtifactScope;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import umlp.backendrte.common.NotificationScope;
import vacuumgripperdashboard.commands.StepSimulator;

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


    VacuumGripperDashboardManager.addObserver(new VacuumGripperDashboardObserver(){
      @Override
      public void maybeNotifySimulatorStepAdded(SimulatorStep simulatorStep, NotificationScope scope) {
        new StepSimulator().doAction();
      }
    });
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