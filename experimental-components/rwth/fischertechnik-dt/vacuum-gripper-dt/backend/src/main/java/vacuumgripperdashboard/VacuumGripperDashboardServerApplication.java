package vacuumgripperdashboard;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
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

  private Gateway gateway;
  private PositionService positionService;
  private ModelManager modelManager;
  private LogicProcessor logicProcessor;

  @javax.annotation.PostConstruct
  public void init() throws IOException {
    super.init();
    gateway = new Gateway();
    modelManager = new ModelManager();
    positionService = new PositionService();
    logicProcessor = new LogicProcessor(modelManager.getSimulator());

    positionService.connectTo(modelManager.getPhysicalTwinOutput());

    try {
      gateway.initMqttConnector(modelManager.getSimulator());
    } catch (MqttException e) {
      Log.warn("Can not connect to MQTT", e);
    }
  }
}