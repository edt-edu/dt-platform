import { CommonModule } from '@angular/common';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { Component, Input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSliderModule } from '@angular/material/slider';
import { TurtlebotState } from '../app.component';

@Component({
  selector: 'app-arm-movement',
  standalone: true,
  imports: [MatIconModule,
    MatGridListModule,
    MatButtonModule,
    MatSlideToggleModule,
    MatListModule,
    MatSliderModule,
    CommonModule,
    HttpClientModule],
  templateUrl: './arm-movement.component.html',
  styleUrl: './arm-movement.component.scss'
})
export class ArmMovementComponent {

  direction = "STOP"

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
  
  }

  moveUp(event: any) {
    this.direction = "UP"
    this.http.post(
      'http://localhost:7000/moveArm', 
      JSON.stringify({"armDirection": this.direction})).subscribe(data => {
      console.log(data)
    })
  }

  moveLeft(event: any) {
    this.direction = "LEFT"
    this.http.post(
      'http://localhost:7000/moveArm', 
      JSON.stringify({"armDirection": this.direction})).subscribe(data => {
      console.log(data)
    })
  }

  moveRight(event: any) {
    this.direction = "RIGHT"
    this.http.post(
      'http://localhost:7000/moveArm', 
      JSON.stringify({"armDirection": this.direction})).subscribe(data => {
      console.log(data)
    })
  }

  moveDown(event: any) {
    this.direction = "DOWN"
    this.http.post(
      'http://localhost:7000/moveArm', 
      JSON.stringify({"armDirection": this.direction})).subscribe(data => {
      console.log(data)
    })
  }

  moveBack(event: any) {
    this.direction = "BACK"
    this.http.post(
      'http://localhost:7000/moveArm', 
      JSON.stringify({"armDirection": this.direction})).subscribe(data => {
      console.log(data)
    })
  }

  moveFront(event: any) {
    this.direction = "FRONT"
    this.http.post(
      'http://localhost:7000/moveArm', 
      JSON.stringify({"armDirection": this.direction})).subscribe(data => {
      console.log(data)
    })
  }

  moveGripperDown(event: any) {
    this.direction = "GRIPPER_DOWN"
    this.http.post(
      'http://localhost:7000/moveArm', 
      JSON.stringify({"armDirection": this.direction})).subscribe(data => {
      console.log(data)
    })
  }

  moveGripperUp(event: any) {
    this.direction = "GRIPPER_UP"
    this.http.post(
      'http://localhost:7000/moveArm', 
      JSON.stringify({"armDirection": this.direction})).subscribe(data => {
      console.log(data)
    })
  }


}
