import { Component, Input, ViewChild, ElementRef, OnChanges, SimpleChanges } from '@angular/core';
import { ComponentObserverManager } from '@umlp/commonj2ts';
import { ApexTopicBooleanVisualizationComponent, config } from 'components/ApexTopicBooleanVisualizationComponent';
import { OnDestroy } from '@angular/core';
import ApexCharts from 'apexcharts'
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { BooleanTopicObserver } from 'fischertechnikvisualization/BooleanTopicObserver'
import { BooleanTopic } from 'fischertechnikvisualization/BooleanTopic'
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
export class ApexTopicBooleanVisualization extends ApexTopicBooleanVisualizationComponent implements OnDestroy {
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
        size: 1,
        style: 'hollow',
      },
      stroke: {
        curve: 'stepline',
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
      this._topic.addObserver(new (class implements BooleanTopicObserver {
                                notifySetTopicName(booleanTopic: BooleanTopic, oldValue: string, o: string){ }
                               
                                   maybeNotifySetTopicName(booleanTopic: BooleanTopic, oldValue: string, o: string, scope: NotificationScope){ }
                               
                                   notifyAddValues(booleanTopic: BooleanTopic, arg: number, indexInList: number) { }
                               
                                   maybeNotifyAddValues(booleanTopic: BooleanTopic, arg: number, indexInList: number, scope: NotificationScope){
                                     booleanTopic.getValues(indexInList).then(res => {
                                       thisRef.options.series[0].data.push([res._timestamp, res._content ? 1 : 0]);
                                          thisRef.chart.updateSeries(thisRef.options.series, false);
                                     });
                                   }
                               
                                   notifyRemoveValues(booleanTopic: BooleanTopic, arg: number, idx: number) { }
                               
                                   maybeNotifyRemoveValues(booleanTopic: BooleanTopic, arg: number, idx: number, scope: NotificationScope){ }
                               
                                   notifySetValues(booleanTopic: BooleanTopic, idx: number, oldValue: number, o: number) { }
                               
                                   maybeNotifySetValues(booleanTopic: BooleanTopic, idx: number, oldValue: number, o: number, scope: NotificationScope){ }
                               
                                   getScope(): NotificationScope { return NotificationScope.ALL; }

                               
                                   notifyBuild(booleanTopic: BooleanTopic) { }
                               
                                   maybeNotifyBuild(booleanTopic: BooleanTopic, scope: NotificationScope){ }
                              })());
    }
  }

  public ngOnDestroy(): void {
    super.ngOnDestroy();
    ComponentObserverManager.removeObservers("components.ApexTopicBooleanVisualization" + this.uniqueIdentifier);
  }
}