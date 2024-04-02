import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSliderModule } from '@angular/material/slider';
import { TurtlebotState } from '../app.component';
import { FormsModule } from '@angular/forms';
import { Observable, interval } from 'rxjs';

@Component({
  selector: 'app-base-movement',
  standalone: true,
  imports: [ MatIconModule,
    MatGridListModule,
    MatButtonModule,
    MatSlideToggleModule,
    MatListModule,
    MatSliderModule,
    CommonModule,
    HttpClientModule,
    FormsModule],
  templateUrl: './base-movement.component.html',
  styleUrl: './base-movement.component.scss',
  changeDetection: ChangeDetectionStrategy.Default
})
export class BaseMovementComponent implements OnChanges{

  @Input() state?: TurtlebotState
  @Input() auto_stop: boolean = false


  con = false

  inputSpeed: number = 0

  direction = "STOP"

  stopped = false

  constructor(private http: HttpClient) {}


  ngOnChanges(changes: SimpleChanges): void {
    if (this.auto_stop && !this.stopped) {
      this.state?.wafflePiState.movement.angular == "STOP"
      this.state?.wafflePiState.movement.linear == "STOP"
      this.con = false
      this.http.post(
        'http://localhost:7000/continuous', 
        JSON.stringify({"continuous": this.con})).subscribe(data => {
        console.log(data)
      })
      this.stopped = true
    }

    if (!this.auto_stop) {
      this.stopped = false
    }
  }

  resetDirection() {
    if (!this.con) {
      this.state?.wafflePiState.movement.angular == "STOP"
      this.state?.wafflePiState.movement.linear == "STOP"
    }
  }


  ngOnInit(): void {
    this.http.post(
      'http://localhost:7000/speed', 
      JSON.stringify({"speed": this.inputSpeed})).subscribe(data => {
      console.log(data)
    })

    this.http.post(
      'http://localhost:7000/continuous', 
      JSON.stringify({"continuous": this.con})).subscribe(data => {
    })


  }

  moveUp(event: any) {
    this.direction = "UP"
    this.http.post(
      'http://localhost:7000/direction', 
      JSON.stringify({"direction": this.direction})).subscribe(data => {
      console.log(data)
    })
  }

  moveLeft(event: any) {
    this.direction = "LEFT"
    this.http.post(
      'http://localhost:7000/direction', 
      JSON.stringify({"direction": this.direction})).subscribe(data => {
      console.log(data)
    })
  }

  moveRight(event: any) {
    this.direction = "RIGHT"
    this.http.post(
      'http://localhost:7000/direction', 
      JSON.stringify({"direction": this.direction})).subscribe(data => {
      console.log(data)
    })
  }

  moveDown(event: any) {
    this.direction = "DOWN"
    this.http.post(
      'http://localhost:7000/direction', 
      JSON.stringify({"direction": this.direction})).subscribe(data => {
      console.log(data)
    })
  }

  stop(event: any) {
    this.direction = "STOP"
    this.http.post(
      'http://localhost:7000/direction', 
      JSON.stringify({"direction": this.direction})).subscribe(data => {
      console.log(data)
    })
  }

  continuous(event: any) {
    this.con = event.checked
      this.http.post(
        'http://localhost:7000/continuous', 
        JSON.stringify({"continuous": this.con})).subscribe(data => {
      })
  }

  setForwardSpeed(event: any) {
    this.http.post(
      'http://localhost:7000/speed', 
      JSON.stringify({"speed": this.inputSpeed})).subscribe(data => {
      console.log(data)
    })
  }

}
