import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ChartModule } from 'primeng/chart';

@Component({
  selector: 'app-line-chart',
  standalone: true,
  imports: [ChartModule],
  templateUrl: './line-chart.component.html',
  styleUrl: './line-chart.component.scss',
  changeDetection: ChangeDetectionStrategy.Default
})
export class LineChartComponent {

  @Input() batteryGraphData: any

  options: any

  constructor() {
    this.options = {
      animation: {
          duration: 0
      }
  }
  }

}
