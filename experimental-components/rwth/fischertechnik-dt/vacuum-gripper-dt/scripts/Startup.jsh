import vacuumgripperdashboard.*;
import umlp.backendrte.command.*;
import static vacuumgripperdashboard.VacuumGripperDashboardManager.*;

System.out.println("Initializing connection to server");
VacuumGripperDashboardManagerClientImpl.init();
var com = new util.AlternativeClientCommandWebsocketCommunication("ws://localhost:8081/umlp/api/command");
CommandManager.initMe(new ClientCommandManager(com));
System.out.println("Done");