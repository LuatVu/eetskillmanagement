import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ChartConfiguration, ChartData, ChartEvent, ChartType } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
@Component({
  selector: 'eet-pie-chart-common',
  templateUrl: './pie-chart-common.component.html',
  styleUrls: ['./pie-chart-common.component.scss']
})
export class PieChartCommonComponent implements OnInit {
  @Input() chartInfo: any;

  ngOnInit(): void {
    this.pieChartOptions!.plugins!.title!.text = this.chartInfo.title;
    this.pieChartOptions!.plugins!.title!.color = this.chartInfo.titleColor;

    this.pieChartData.labels = this.chartInfo.chartLabels;
    this.pieChartData.datasets[0] = {
      data: this.chartInfo.chartData,
      backgroundColor: this.chartInfo.chartItemColor,
      hoverBackgroundColor: this.chartInfo.chartItemColor,
      hoverBorderWidth: 0
    };

  }
  @ViewChild(BaseChartDirective) chart: BaseChartDirective | undefined;

  public pieChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    // We use these empty structures as placeholders for dynamic theming.
    scales: {
      y: {
        min: 0,
        display: false
      }
    },
    plugins: {
      legend: {
        display: true
      },
      title: {
        display: true,
        font: {
          size: 16
        }
      },

    }
  };
  public pieChartType: ChartType = 'pie';
  public pieChartData: ChartData<'pie'> = {
    labels: [],
    datasets: []
  };

  // events
  public chartClicked({ event, active }: { event?: ChartEvent, active?: {}[] }): void {
  }

  public chartHovered({ event, active }: { event?: ChartEvent, active?: {}[] }): void {
  }
}
