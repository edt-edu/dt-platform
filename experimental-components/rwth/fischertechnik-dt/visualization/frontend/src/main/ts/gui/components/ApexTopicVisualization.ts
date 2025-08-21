import { Component, Input, ViewChild, ElementRef, OnChanges, SimpleChanges } from '@angular/core';
import { ComponentObserverManager } from '@umlp/commonj2ts';
import { ApexTopicVisualizationComponent, config } from 'components/ApexTopicVisualizationComponent';
import { OnDestroy } from '@angular/core';
import ApexCharts from 'apexcharts'
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { DoubleTopicObserver } from 'fischertechnikvisualization/DoubleTopicObserver'
import { DoubleTopic } from 'fischertechnikvisualization/DoubleTopic'
import { NotificationScope } from '@umlp/commonj2ts';
import { IUMLPList } from '@umlp/commonj2ts';

@Component({
  standalone: true,
  imports: [...config.imports],
  selector: config.selector,
  templateUrl: config.templateUrl,
  styles: config.styles,
  styleUrls: [...config.styleUrls]
})
export class ApexTopicVisualization extends ApexTopicVisualizationComponent implements OnDestroy {
  options: any;
  chart: ApexCharts;

  @ViewChild('diagramContainer', { static: true })
  public diagramContainer!: ElementRef;

  constructor(private sanitizer: DomSanitizer) {
    super();
  }

  public async init() {
    this.options = {
      animations: {
        enabled: false,
        speed: 800,
        animateGradually: {
          enabled: false,
          delay: 150
        },
        dynamicAnimation: {
          enabled: false,
          speed: 350
        }
      },
      noData: {
        text: true ? "Loading..." : "No Data present in the graph!",
        align: 'center',
        verticalAlign: 'middle',
        offsetX: 0,
        offsetY: 0,
        style: {
          color: "#000000",
          fontSize: '14px',
          fontFamily: "Helvetica"
        }
      },
      chart: {
        id: 'area-datetime',
        type: 'area',
        height: 350,
        width: '75%',/**/
        zoom: {
          autoScaleYaxis: true
        },
        events: {
          legendClick: function(chartContext, seriesIndex, opts) {
            this.options.series[seriesIndex].hidden = !this.options.series[seriesIndex].hidden;
          }.bind(this)
        }
      },
      series: [
        {
            name: "Test",
            data: []
        }
      ],
      xaxis: {
        type: 'datetime',
        // min: new Date('01 Mar 2012').getTime(),
        tickAmount: 6,
      },
      annotations: {
        yaxis: [{
          y: 30,
          borderColor: '#999',
          label: {
            show: true,
            text: 'Support',
            style: {
              color: "#fff",
              background: '#00E396'
            }
          }
        }],
        xaxis: [{
          borderColor: '#999',
          yAxisIndex: 0,
          label: {
            show: true,
            text: 'Rally',
            style: {
              color: "#fff",
              background: '#775DD0'
            }
          }
        }]
      },

      dataLabels: {
        enabled: false
      },
      markers: {
        size: 0,
        style: 'hollow',
      },
      /* tooltip: {
        x: {
          format: 'dd MMM yyyy HH:mm:ss'
        }
      },*/
      fill: {
              type: 'gradient',
              gradient: {
                shadeIntensity: 1,
                opacityFrom: 0.7,
                opacityTo: 0.9,
                stops: [0, 100]
              }
            },
    };

    this.chart = new ApexCharts(this.diagramContainer.nativeElement, this.options);
    this.chart.render();
  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log(changes);
  }

  private renderDiagram(): void {
  }

  public override getTopic__update(): void {
    super.getTopic__update();
    console.log("GetTopicUpdate", this._topic);
    if (this._topic){
      const thisRef = this;
      this._topic.addObserver(new (class implements DoubleTopicObserver {
                                      notifySetTopicName(doubleTopic: DoubleTopic, oldValue: string, o: string){ }

                                         maybeNotifySetTopicName(doubleTopic: DoubleTopic, oldValue: string, o: string, scope: NotificationScope){ }

                                         notifyAddValues(doubleTopic: DoubleTopic, arg: number, indexInList: number) { }

                                         maybeNotifyAddValues(doubleTopic: DoubleTopic, arg: number, indexInList: number, scope: NotificationScope){
                                           doubleTopic.getValues(indexInList).then(res => {
                                             thisRef.options.series[0].data.push([res._timestamp, res._content]);
                                             thisRef.chart.updateSeries(thisRef.options.series, false);
                                           });
                                         }

                                         notifyRemoveValues(doubleTopic: DoubleTopic, arg: number, idx: number) { }

                                         maybeNotifyRemoveValues(doubleTopic: DoubleTopic, arg: number, idx: number, scope: NotificationScope){ }

                                         notifySetValues(doubleTopic: DoubleTopic, idx: number, oldValue: number, o: number) { }

                                         maybeNotifySetValues(doubleTopic: DoubleTopic, idx: number, oldValue: number, o: number, scope: NotificationScope){ }

                                         getScope(): NotificationScope { return NotificationScope.ALL; }


                                         notifyBuild(doubleTopic: DoubleTopic) { }

                                         maybeNotifyBuild(doubleTopic: DoubleTopic, scope: NotificationScope){ }
                                    })());
          }
  }

  public ngOnDestroy(): void {
    super.ngOnDestroy();
    ComponentObserverManager.removeObservers("components.ApexTopicVisualization" + this.uniqueIdentifier);
  }
}