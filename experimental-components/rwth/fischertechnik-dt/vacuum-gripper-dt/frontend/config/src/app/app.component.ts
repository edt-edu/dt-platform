import { Component } from '@angular/core';
import { VacuumGripperDashboardManagerClientImpl } from '@src/vacuumgripperdashboard/VacuumGripperDashboardManagerClientImpl';
import { ClientCommandManager, ClientCommandWebsocketCommunication, CommandManager } from '@umlp/common';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor() {
    VacuumGripperDashboardManagerClientImpl.init();
    const cmdManager = new ClientCommandManager(new ClientCommandWebsocketCommunication(
      `${document.baseURI.replace(/^http/, 'ws').replace(/\/$/, '')}` + '/umlp/api/command'
    ));
    CommandManager.initMe(cmdManager);
  }
}
