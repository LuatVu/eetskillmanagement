import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import * as am5 from "@amcharts/amcharts5";
import * as am5wc from "@amcharts/amcharts5/wc";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";
import { TranslateService } from '@ngx-translate/core';
import { ReportUtils } from '../../../common/report.utils';
@Component({
  selector: 'eet-tag-cloud',
  templateUrl: './tag-cloud.component.html',
  styleUrls: ['./tag-cloud.component.scss']
})
export class TagCloudComponent implements OnInit, AfterViewInit {
  @Input() data: any = []
  @Input() categoryField:string
  @Input() valueField:string
  constructor(
    private translate:TranslateService
  ) { }
  createWrapperContainer(root:any) {
    const container = root.container.children.push(am5.Container.new(root, {
      width: am5.percent(100),
      height: am5.percent(100),
      layout: root.verticalLayout
    }));
    return container
  }
  addChartTitle(root:any,container:any) {
    const title = container.children.push(am5.Label.new(root, {
      // text: 'Text',
      fontSize: 19,
      fill: am5.color("rgba(0, 0, 0, 0)"), // Set transparent color (black with alpha 0)
      x: am5.percent(50),
      centerX: am5.percent(50)
  }));
  
    return title
  }
  createNewSeries(root:any,container:any) {
    const series = container.children.push(am5wc.WordCloud.new(root, {
      categoryField: this.categoryField,
      valueField: this.valueField,
      calculateAggregates: true, // this is needed for heat rules to work
      randomess:1,
      maxFontSize: am5.percent(60),
      minFontSize: am5.percent(5),
    }));
    return series
  }
  ngAfterViewInit(): void {
    let root = am5.Root.new("chartdiv");


    // Set themes
    // https://www.amcharts.com/docs/v5/concepts/themes/
    root.setThemes([
      am5themes_Animated.new(root)
    ]);

    // Add wrapper container
    const container = this.createWrapperContainer(root)


    // Add chart title
    this.addChartTitle(root,container)

    // Add series
    const series = this.createNewSeries(root,container)
    // Set up heat rules
    series.set("heatRules", [{
      target: series.labels.template,
      dataField: "value",
      customFunction: function(sprite:any, min:any, max:any, value:any) {
        for(let i=0;i<9;i++) {
          if(value<value/i) {
            sprite.set("fill", am5.color(ReportUtils.generateRandomColor()));
          }
        }
      }
    }]);


    // Configure labels
    series.labels.template.setAll({
      paddingTop: 5,
      paddingBottom: 5,
      paddingLeft: 5,
      paddingRight: 5,
      fontFamily: "Bosch-Sans",
      cursorOverStyle: "pointer"
    });
    series.labels.template.set("tooltipText", `{${this.categoryField}}: [bold]{value}[/]`);
    // Data from:
    series.data.setAll(this.data);
  }

  ngOnInit(): void {
  }

}
