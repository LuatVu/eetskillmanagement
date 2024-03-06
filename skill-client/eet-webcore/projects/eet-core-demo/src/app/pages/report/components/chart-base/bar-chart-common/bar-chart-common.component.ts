import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ChartConfiguration, ChartData, ChartEvent, ChartType} from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import DataLabelsPlugin from 'chartjs-plugin-datalabels';
@Component({
  selector: 'eet-bar-chart-common',
  templateUrl: './bar-chart-common.component.html',
  styleUrls: ['./bar-chart-common.component.scss']
})
export class BarChartCommonComponent implements OnInit {
  @Input() chartInfo: any;
  ngOnInit(): void {
    this.barChartOptions!.plugins!.title!.text = this.chartInfo.title;
    this.barChartOptions!.plugins!.title!.color = this.chartInfo.titleColor;

    this.barChartData.labels = this.chartInfo.chartLabels;
    this.barChartData.datasets[0] = {
      data: this.chartInfo.chartData,
      backgroundColor: this.chartInfo.chartItemColor,
      hoverBackgroundColor:"#cb467e"
    };
  }
  @ViewChild(BaseChartDirective) chart: BaseChartDirective | undefined;
  public barChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: {
        min: 0
      }
    },
    plugins: {
      legend: {
        display: false,
      },
      title: {
        display: true,
        font: {
          size: 16
        }
      },
      datalabels: {
        labels: {
          title: {
            font: {
              weight: 'bold'
            }
          },
          value: {
            color: '#505254'
          },
        },
        anchor: 'end',
        align: 'end',
        formatter(value, context) {
          return Number(value);
        },
      }
    }
  };
  public barChartType: ChartType = 'bar';
  public barChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [
      {
        data: []
      }
    ]
  };
  public barChartPlugin = [DataLabelsPlugin] 
  // events
  public chartClicked({ event, active }: { event?: ChartEvent, active?: {}[] }): void {
  }

  public chartHovered({ event, active }: { event?: ChartEvent, active?: {}[] }): void {
  }
}
