import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatButtonModule } from '@angular/material/button';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatListModule } from '@angular/material/list';
import { CommonModule } from '@angular/common';
import { MatSliderModule } from '@angular/material/slider';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Observable, interval, of } from 'rxjs';
import { LineChartComponent } from './line-chart/line-chart.component';
import { BaseMovementComponent } from './base-movement/base-movement.component';
import { ArmMovementComponent } from './arm-movement/arm-movement.component';


export interface WafflePiState {
  batteryState: {
    percentage: number,
    time: string
  },
  movement: {
    angular: string,
    linear: string,
    speed:number,
    time: string
  },
  orientation: {
    w: number,
    x: number,
    y: number,
    z: number,
    time: string
  },
  ros: {
    msg: string,
    time: string
  },
  wheelState: {
    leftWheelVelocity: number,
    rightWheelVelocity: number,
    leftWheelPosition: number,
    rightWheelPosition: number
  },
  distance: number
}

export interface JointStates {
  joint: string,
  jointPosition: number,
  jointVelocity: number,
  jointEffort: number
}
export interface ArmState {
  jointStates: JointStates[]
}

export interface TurtlebotState {
  wafflePiState: WafflePiState,
  armState: ArmState
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,
    MatIconModule,
    MatGridListModule,
    MatButtonModule,
    MatSlideToggleModule,
    MatListModule,
    CommonModule,
    MatSliderModule,
    FormsModule,
    HttpClientModule,
    MatProgressBarModule,
    LineChartComponent,
    BaseMovementComponent,
    ArmMovementComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  changeDetection: ChangeDetectionStrategy.Default
})
export class AppComponent implements OnInit {

  title = 'dt-front-end';

  direction = "STOP"

  inputSpeed: number = 0

  distanceLimit = 0.1

  state?: TurtlebotState

  x: number = 0
  z: number = 0

  con: Boolean = false

  batteryGraphData: any

  batteryGraphDataInput: any

  stop = false


  constructor(private http: HttpClient) {
    
    this.batteryGraphData =  {
      labels: [],
      datasets: [
          {
              label: 'Battery Percentage',
              data: [],
              fill: false,
              tension: 0.4
          },
          {
              label: 'TurtleBot Speed',
              data: [],
              fill: false,
              tension: 0.4
          }
      ]
  };
  this.batteryGraphDataInput = {...this.batteryGraphData}
  }

  ngOnInit(): void {
    interval(100).subscribe(x => {
      this.getTurtleBotState();
      if (this.state?.wafflePiState.distance) {
        this.stop = this.state?.wafflePiState.distance < this.distanceLimit ? true : false
      }
      
    });
    this.http.post(
      'http://localhost:7000/speed', 
      JSON.stringify({"speed": this.inputSpeed})).subscribe(data => {
      console.log(data)
    })
  
  }

  getTurtleBotState() {
    this.http.get('http://localhost:7000/turtleBotState').subscribe(data => {
      this.state = data as TurtlebotState
      this.state.armState.jointStates.sort((a,b) => a.joint.localeCompare(b.joint))
      let d = new Date(+this.state.wafflePiState.batteryState.time).toLocaleTimeString("de-DE")
      this.batteryGraphData.labels.push(d)

      if (this.batteryGraphData.labels.length > 1500) {
        this.batteryGraphData.labels.splice(0,1)
        this.batteryGraphData.datasets[0].data.splice(0,1)
        this.batteryGraphData.datasets[1].data.splice(0,1)
      }
      this.batteryGraphData.datasets[0].data.push(this.state.wafflePiState.batteryState.percentage)
      this.batteryGraphData.datasets[1].data.push(this.state.wafflePiState.movement.speed * 100)
      this.batteryGraphDataInput = {...this.batteryGraphData}
    })
  }

  limitDistance(event: any) {
    this.http.post(
      'http://localhost:7000/distance', 
      JSON.stringify({"distance": this.distanceLimit})).subscribe(data => {
      console.log(data)
    })
  }
  
}

