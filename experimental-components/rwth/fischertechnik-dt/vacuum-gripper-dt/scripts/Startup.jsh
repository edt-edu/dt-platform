import vacuumgripperdashboard.*;
import umlp.backendrte.command.*;

VacuumGripperDashboardManagerClientImpl.init();
var com = new umlp.backendrte.service.websocket.ClientCommandWebsocketCommunication("ws://localhost:8081/umlp/api/command");
CommandManager.initMe(new ClientCommandManager(com));