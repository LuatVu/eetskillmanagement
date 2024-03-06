import { AfterViewInit, Component, ElementRef, Input, OnInit, Renderer2, ViewChild } from '@angular/core';
import { CONSTANT_V_MODEL, LOCATION_CHILD } from './datamock';
import SvgPanZoom from 'svg-pan-zoom';
import { VModelService } from './v-model.service';
import { ChildVModel, PhaseModel, PointLike } from './v-model.model';
import { ProjectsService } from '../projects/services/projects.service';
import { BoschProjectDetailComponent } from '../projects/components/bosch-project-detail/bosch-project-detail.component';
import { DialogCommonService } from '../../shared/services/dialog-common.service';
import { TranslateService } from '@ngx-translate/core';
import { Helpers } from '../../shared/utils/helper';
import { ListProjectDialogComponent } from './dialog/list-project-dialog/list-project-dialog.component';
import { GB_UNIT, CUSTOMER_GB } from './constants/constants';
import { CONFIG } from '../../shared/constants/config.constants';
import { LoadingService } from '../../shared/services/loading.service';
import { finalize } from 'rxjs/operators';
import { PermisisonService } from '../../shared/services/permisison.service';
import { GBFilterModel } from '../projects/models/projects.model';
@Component({
  selector: 'eet-v-model',
  templateUrl: './v-model.component.html',
  styleUrls: ['./v-model.component.scss']
})

export class VModelComponent implements OnInit, AfterViewInit {
  @Input() dataFilters! : GBFilterModel;
  private amountOfChildInARow = 3;
  private amountOfChildInARowSmall = 4;

  private widthChildSmall = 170;
  private heightChildSmall = 23;
  public distanceChildSmall = 185;

  private xContentLeft = 210;
  private xContentRight = 1000;
  private xContentMiddle = 500;

  private widthChild = 215;
  private heightChild = 28;
  public distanceChild = 230;
  public threshold = {
    small: 15,
    normal: 20
  }

  public heightLeftChild = {
    oneLine: 60,
    twoLine: 100,
    threeLine: 155
  }
  public heightRightChild = {
    oneLine: 75,
    twoLine: 100,
    threeLine: 155
  }
  private gbUnitData: string
  private customerGbData: string
  private dataFilter: PhaseModel[] = []
  private dataSource: PhaseModel[] = []
  // For Zoom SVG
  private panZoom: SvgPanZoom.Instance;
  private zoomWindow = false;
  private boxElement: HTMLDivElement;
  private startPt: PointLike;
  private currentPt: PointLike;

  private controlState: {
    isControlIconsEnabled?: boolean;
    isDblClickZoomEnabled?: boolean;
    isMouseWheelZoomEnabled?: boolean;
    isPanEnabled?: boolean;
    isZoomEnabled?: boolean;
  } = {};
  public HAS_VIEW_PROJECT_DETAIL: boolean;

  @ViewChild('svg', { static: false }) svg: ElementRef<SVGSVGElement>;
  @ViewChild('tooltip') tooltip: ElementRef;
  constructor(
    private renderer: Renderer2,
    private el: ElementRef,
    private vModelService: VModelService,
    private dialogCommonService: DialogCommonService,
    private translate: TranslateService,
    private projectsService: ProjectsService,
    private comLoader: LoadingService,
    private permissionService: PermisisonService,) {
    this.HAS_VIEW_PROJECT_DETAIL = permissionService.hasPermission(CONFIG.PERMISSIONS.VIEW_PROJECT_DETAIL);
  }

  ngOnInit(): void {
    this.getPhaseData();
    this.projectsService.Gb$.subscribe((respone: any) => {
      if (respone) {
        this.watchOnFilterChange(respone.type, respone.data)
      }
    })

  }

  watchOnFilterChange(type: string, data: string) {
    if (type === GB_UNIT) {
      this.gbUnitData = data
    } else if (type === CUSTOMER_GB) {
      this.customerGbData = data
    }
    this.clearDataEachChildOfContent()

    // if both 2 filter is unselect
    if (!this.gbUnitData && !this.customerGbData) {
      this.generateData(this.dataSource)
      return
    }

    this.dataFilter = Helpers.cloneDeep(this.dataSource);
    this.dataFilter.forEach((phase: any) => {
      phase.listProjects = phase.listProjects.filter((project: any) => {
        if (this.gbUnitData && this.customerGbData) {
          return project.gb_unit === this.gbUnitData && project.customer_gb === this.customerGbData
        } else
          return project.gb_unit === this.gbUnitData || project.customer_gb === this.customerGbData
      });
    });
    this.generateData(this.dataFilter)
  }

  clearDataEachChildOfContent() {
    this.contentLeft.map((e) => {
      return e.children = []
    })
    this.contentRight.map((e) => {
      return e.children = []
    })
    this.locationManagement.map((e) => {
      return e.children = []
    })
  }

  ngAfterViewInit() {
    this.panZoom = SvgPanZoom(this.svg.nativeElement, {
      zoomEnabled: true,
      controlIconsEnabled: true,
      maxZoom: 1000,
    });
    this.panZoom.resize();
    this.panZoom.fit();
    this.panZoom.center();
    this.saveControlState();

    //fix controls in corner right bottom screen
    const controlsElement = document.getElementById('svg-pan-zoom-controls')
    const x = window.innerWidth - 410
    const y = window.innerHeight - 340
    if(controlsElement){
      controlsElement.setAttribute('transform',`translate(${x} ${y}) scale(0.65)`)
    }
  }
  generateData(data: any) {
    this.generateChildForContentLeft(data);
    this.generateChildForContentRight(data);
    this.generateChildForManagementSupportTools(data);
  }

  getPhaseData() {
    let phaseData: PhaseModel[] = [];
    const loader = this.comLoader.showProgressBar()
    this.vModelService.getPhaseData().pipe(finalize(() => {
      this.comLoader.hideProgressBar(loader);
    })).subscribe((result) => {
      if (result) {
        this.comLoader.hideProgressBar(loader)
        result?.data?.phases?.forEach((phase: any) => {
          let phaseModel = {} as PhaseModel;
          phaseModel.name = phase?.name;
          phaseModel.listProjects = [];
          phase?.projects?.forEach((project: any) => {
            phaseModel.listProjects.push({
              name: project?.name,
              project_id: project?.project_id,
              gb_unit: project?.gb_unit,
              customer_gb: project?.customer_gb
            });
          });
          phaseData.push(phaseModel);
        });
        this.dataSource = Helpers.cloneDeep(phaseData)
        this.dataFilter = Helpers.cloneDeep(phaseData)
        this.generateData(phaseData);
        if(this.dataFilters) this.watchOnFilterChange(this.dataFilters.type, this.dataFilters.data);
      }
    });
  }

  public locationPhase = [
    {
      class: 'rectangle',
      x: 180,
      y: 0,
      width: 1600,
      height: 260,
      text: {
        id: 'titleComponent',
        x: 975,
        y: 20,
        title: "vmodel.phase.systemTools"
      }
    },
    {
      class: 'rectangle',
      x: 180,
      y: 270,
      width: 1600,
      height: 450,
      text: {
        id: 'titleComponent',
        x: 975,
        y: 290,
        title: "vmodel.phase.softwareTools"
      }
    },
    {
      class: 'rectangle',
      x: 180,
      y: 725,
      width: 1600,
      height: 350,
      text: {
        id: 'titleComponent',
        x: 975,
        y: 745,
        title: "vmodel.phase.managementSupportTools"
      }
    },
  ]
  public contentLeft = [
    // System Tools
    {
      id: 'item_left_',
      x: 210,
      y: 40,
      class: 'rectangle',
      width: 750,
      height: this.heightLeftChild.twoLine,
      label: 'requirementEngineering',
      children: []
    },
    {
      id: 'item_left_',
      x: 210,
      y: 150,
      class: 'rectangle',
      width: 750,
      height: this.heightLeftChild.twoLine,
      label: 'systemArchitecture',
      children: []
    },
    // Software Tools
    {
      id: 'item_left_',
      x: 210,
      y: 310,
      class: 'rectangle',
      width: 750,
      height: this.heightLeftChild.oneLine,
      label: 'softwareArchitecture',
      children: []
    },
    {
      id: 'item_left_',
      x: 210,
      y: 395,
      class: 'rectangle',
      width: 750,
      height: this.heightLeftChild.oneLine,
      label: 'softwareDocumentation',
      children: []
    },
    {
      id: 'item_left_',
      x: 210,
      y: 480,
      class: 'rectangle',
      width: 750,
      height: this.heightLeftChild.oneLine,
      label: 'softwareModelBasedDevelopment',
      children: []
    },
    {
      id: 'item_left_',
      x: 210,
      y: 565,
      class: 'rectangle',
      width: 750,
      height: this.heightLeftChild.oneLine,
      label: 'softwareRapidPrototyping',
      children: []
    },
    {
      id: 'item_left_',
      x: 210,
      y: 650,
      class: 'rectangle',
      width: 750,
      height: this.heightLeftChild.oneLine,
      label: 'genericReview',
      children: []
    }
  ];
  public contentRight = [
    // System Tools
    {
      id: 'item_right_',
      class: 'rectangle',
      x: 1000,
      y: 40,
      width: 750,
      height: this.heightRightChild.twoLine,
      label: 'systemSoftwareTest',
      children: []
    },
    {
      id: 'item_right_',
      x: 1000,
      y: 150,
      class: 'rectangle',
      width: 750,
      height: this.heightRightChild.twoLine,
      label: 'systemIntegration',
      children: []
    },
    // Software Tools
    {
      id: 'item_right_',
      x: 1000,
      y: 310,
      class: 'rectangle',
      width: 750,
      height: this.heightRightChild.twoLine,
      label: 'softwareIntegration',
      children: []
    },
    {
      id: 'item_right_',
      x: 1000,
      y: 435,
      class: 'rectangle',
      width: 750,
      height: this.heightRightChild.oneLine,
      label: 'ciDashboard',
      children: []
    },
    {
      id: 'item_right_',
      x: 1000,
      y: 540,
      class: 'rectangle',
      width: 750,
      height: this.heightRightChild.oneLine,
      label: 'softwareUnitTest',
      children: []
    },
    {
      id: 'item_right_',
      x: 1000,
      y: 635,
      class: 'rectangle',
      width: 750,
      height: this.heightRightChild.oneLine,
      label: 'softwareConstruction',
      children: []
    }
  ]
  // Management/ Support Tools
  public locationManagement = [
    {
      id: 'item_management_',
      class: 'rectangle',
      x: 500,
      y: 760,
      width: 950,
      height: 70,
      text: {
        id: 'title_',
        x: 975,
        y: 775,
        title: "prjConfigManagement"
      },
      children: []
    },
    {
      id: 'item_management_',
      class: 'rectangle',
      x: 500,
      y: 835,
      width: 950,
      height: 70,
      text: {
        id: 'title_',
        x: 975,
        y: 855,
        title: "prjPlanCreationDeli"
      },
      children: []
    },
    {
      id: 'item_management_',
      class: 'rectangle',
      x: 500,
      y: 910,
      width: 950,
      height: 70,
      text: {
        id: 'title_',
        x: 975,
        y: 925,
        title: "swConfigManagement"
      },
      children: []
    },
    {
      id: 'item_management_',
      class: 'rectangle',
      x: 500,
      y: 985,
      width: 950,
      height: 70,
      text: {
        id: 'title_',
        x: 975,
        y: 1005,
        title: "changeRequest"
      },
      children: []
    },
  ]
  trimText(text: any, threshold = 15) {
    if (text.length <= threshold) return text;
    return text.substr(0, threshold).concat("...");
  }
  generateChildForContentLeft(data: Array<any>) {
    data.forEach(phase => {
      switch (phase.name) {
        case CONSTANT_V_MODEL.REQUIREMENT_ENGINEERING:
          const requirementEngineering = this.contentLeft.find(ele => ele.label === phase.name);
          if (phase?.listProjects?.length > 0) {
            phase?.listProjects.forEach((dataChild: any, index: number) => {
              let newChild;
              if (index < this.amountOfChildInARow) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChild * index),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else if (index >= this.amountOfChildInARow && index < (this.amountOfChildInARow * 2)) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_2.x + (this.distanceChild * (index - this.amountOfChildInARow)),
                  LOCATION_CHILD[phase.name]?.ROW_2.y,
                  'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_2.x + (this.distanceChild * (index - this.amountOfChildInARow)),
                  LOCATION_CHILD[phase.name]?.ROW_2.y,
                  'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              }

              if (requirementEngineering) {
                let arrChild: ChildVModel[] = requirementEngineering?.children as ChildVModel[];
                arrChild.push(newChild as ChildVModel)
              }
            });
          }
          break;
        case CONSTANT_V_MODEL.SYSTEM_ARCHITECTURE:
          const systemArchitecture = this.contentLeft.find(ele => ele.label === phase.name);
          if (phase?.listProjects?.length > 0) {
            phase?.listProjects.forEach((dataChild: any, index: number) => {
              let newChild;
              if (index < this.amountOfChildInARow) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChild * index),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else if (index >= this.amountOfChildInARow && index < (this.amountOfChildInARow * 2)) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_2.x + (this.distanceChild * (index - this.amountOfChildInARow)),
                  LOCATION_CHILD[phase.name]?.ROW_2.y,
                  'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_2.x + (this.distanceChild * (index - this.amountOfChildInARow)),
                  LOCATION_CHILD[phase.name]?.ROW_2.y,
                  'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              }

              if (systemArchitecture) {
                let arrChild: ChildVModel[] = systemArchitecture?.children as ChildVModel[];
                arrChild.push(newChild as ChildVModel)
              }
            });
          }
          break;
        case CONSTANT_V_MODEL.SOFTWARE_ARCHITECTURE:
          const softwareArchitecture = this.contentLeft.find(ele => ele.label === phase.name);
          if (phase?.listProjects?.length > 0) {
            phase?.listProjects.forEach((dataChild: any, index: number) => {
              let newChild;
              if (index < this.amountOfChildInARowSmall) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * index),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else if (index >= this.amountOfChildInARowSmall && index < (this.amountOfChildInARow * 2)) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_2.x + (this.distanceChildSmall * (index - this.amountOfChildInARowSmall)),
                  LOCATION_CHILD[phase.name]?.ROW_2.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_2.x + (this.distanceChildSmall * (index - this.amountOfChildInARowSmall)),
                  LOCATION_CHILD[phase.name]?.ROW_2.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              }

              if (softwareArchitecture) {
                let arrChild: ChildVModel[] = softwareArchitecture?.children as ChildVModel[];
                arrChild.push(newChild as ChildVModel)
              }
            });
          }
          break;
        case CONSTANT_V_MODEL.SOFTWARE_DOCUMENTATION:
          const softwareDocumentation = this.contentLeft.find(ele => ele.label === phase.name);
          if (phase?.listProjects?.length > 0) {
            phase?.listProjects.forEach((dataChild: any, index: number) => {
              let newChild;
              if (index < this.amountOfChildInARowSmall) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * index),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else if (index >= this.amountOfChildInARowSmall && index < (this.amountOfChildInARow * 2)) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * (index - this.amountOfChildInARowSmall)),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * (index - this.amountOfChildInARowSmall)),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              }

              if (softwareDocumentation) {
                let arrChild: ChildVModel[] = softwareDocumentation?.children as ChildVModel[];
                arrChild.push(newChild as ChildVModel)
              }
            });
          }
          break;
        case CONSTANT_V_MODEL.SOFTWARE_MODEL_BASED_DEVELOPMENT:
          const softwareModelBasedDevelopment = this.contentLeft.find(ele => ele.label === phase.name);
          if (phase?.listProjects?.length > 0) {
            phase?.listProjects.forEach((dataChild: any, index: number) => {
              let newChild;
              if (index < this.amountOfChildInARowSmall) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * index),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else if (index >= this.amountOfChildInARowSmall && index < (this.amountOfChildInARow * 2)) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * (index - this.amountOfChildInARowSmall)),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * (index - this.amountOfChildInARowSmall)),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              }

              if (softwareModelBasedDevelopment) {
                let arrChild: ChildVModel[] = softwareModelBasedDevelopment?.children as ChildVModel[];
                arrChild.push(newChild as ChildVModel)
              }
            });
          }
          break;
        case CONSTANT_V_MODEL.SOFTWARE_RAPID_PROTOTYPING:
          const softwareRapidPrototyping = this.contentLeft.find(ele => ele.label === phase.name);
          if (phase?.listProjects?.length > 0) {
            phase?.listProjects.forEach((dataChild: any, index: number) => {
              let newChild;
              if (index < this.amountOfChildInARowSmall) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * index),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else if (index >= this.amountOfChildInARowSmall && index < (this.amountOfChildInARow * 2)) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_2.x + (this.distanceChildSmall * (index - this.amountOfChildInARowSmall)),
                  LOCATION_CHILD[phase.name]?.ROW_2.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_2.x + (this.distanceChildSmall * (index - this.amountOfChildInARowSmall)),
                  LOCATION_CHILD[phase.name]?.ROW_2.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              }

              if (softwareRapidPrototyping) {
                let arrChild: ChildVModel[] = softwareRapidPrototyping?.children as ChildVModel[];
                arrChild.push(newChild as ChildVModel)
              }
            });
          }
          break;
        case CONSTANT_V_MODEL.GENERIC_REVIEW:
          const genericReview = this.contentLeft.find(ele => ele.label === phase.name);
          if (phase?.listProjects?.length > 0) {
            phase?.listProjects.forEach((dataChild: any, index: number) => {
              let newChild;
              if (index < this.amountOfChildInARowSmall) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * index),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * (index - this.amountOfChildInARowSmall)),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              }

              if (genericReview) {
                let arrChild: ChildVModel[] = genericReview?.children as ChildVModel[];
                arrChild.push(newChild as ChildVModel)
              }
            });
          }
          break;
      }
    });
  }
  generateChildForContentRight(data: Array<any>) {
    data.forEach(phase => {
      switch (phase.name) {
        case CONSTANT_V_MODEL.SYSTEM_SOFTWARE_TEST:
          const systemSoftwareTest = this.contentRight.find(ele => ele.label === phase.name);
          if (phase?.listProjects?.length > 0) {
            phase?.listProjects.forEach((dataChild: any, index: number) => {
              let newChild;
              if (index < this.amountOfChildInARow) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChild * index),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else if (index >= this.amountOfChildInARow && index < (this.amountOfChildInARow * 2)) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_2.x + (this.distanceChild * (index - this.amountOfChildInARow)),
                  LOCATION_CHILD[phase.name]?.ROW_2.y,
                  'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_2.x + (this.distanceChild * (index - this.amountOfChildInARow)),
                  LOCATION_CHILD[phase.name]?.ROW_2.y,
                  'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              }

              if (systemSoftwareTest) {
                let arrChild: ChildVModel[] = systemSoftwareTest?.children as ChildVModel[];
                arrChild.push(newChild as ChildVModel)
              }
            });
          }
          break;
        case CONSTANT_V_MODEL.SYSTEM_INTEGRATION:
          const systemIntegration = this.contentRight.find(ele => ele.label === phase.name);
          if (phase?.listProjects?.length > 0) {
            phase?.listProjects.forEach((dataChild: any, index: number) => {
              let newChild;
              if (index < this.amountOfChildInARow) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChild * index),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else if (index >= this.amountOfChildInARow && index < (this.amountOfChildInARow * 2)) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_2.x + (this.distanceChild * (index - this.amountOfChildInARow)),
                  LOCATION_CHILD[phase.name]?.ROW_2.y,
                  'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_2.x + (this.distanceChild * (index - this.amountOfChildInARow)),
                  LOCATION_CHILD[phase.name]?.ROW_2.y,
                  'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              }

              if (systemIntegration) {
                let arrChild: ChildVModel[] = systemIntegration?.children as ChildVModel[];
                arrChild.push(newChild as ChildVModel)
              }
            });
          }
          break;
        case CONSTANT_V_MODEL.SOFTWARE_INTEGRATION:
          const softwareIntegration = this.contentRight.find(ele => ele.label === phase.name);
          if (phase?.listProjects?.length > 0) {
            phase?.listProjects.forEach((dataChild: any, index: number) => {
              let newChild;
              if (index < this.amountOfChildInARow) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChild * index),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else if (index >= this.amountOfChildInARow && index < (this.amountOfChildInARow * 2)) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_2.x + (this.distanceChild * (index - this.amountOfChildInARow)),
                  LOCATION_CHILD[phase.name]?.ROW_2.y,
                  'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else if (index >= (this.amountOfChildInARow * 2) && index < (this.amountOfChildInARow * 3)) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_3.x + (this.distanceChild * (index - (this.amountOfChildInARow * 2))),
                  LOCATION_CHILD[phase.name]?.ROW_3.y,
                  'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_3.x + (this.distanceChild * (index - this.amountOfChildInARow)),
                  LOCATION_CHILD[phase.name]?.ROW_3.y,
                  'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              }

              if (softwareIntegration) {
                let arrChild: ChildVModel[] = softwareIntegration?.children as ChildVModel[];
                arrChild.push(newChild as ChildVModel)
              }
            });
          }
          break;
        case CONSTANT_V_MODEL.CI_DASHBOARD:
          const ciDashboard = this.contentRight.find(ele => ele.label === phase.name);
          if (phase?.listProjects?.length > 0) {
            phase?.listProjects.forEach((dataChild: any, index: number) => {
              let newChild;
              if (index < this.amountOfChildInARowSmall) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * index),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else if (index >= this.amountOfChildInARowSmall && index < (this.amountOfChildInARow * 2)) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * (index - this.amountOfChildInARowSmall)),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * (index - this.amountOfChildInARowSmall)),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              }

              if (ciDashboard) {
                let arrChild: ChildVModel[] = ciDashboard?.children as ChildVModel[];
                arrChild.push(newChild as ChildVModel)
              }
            });
          }
          break;
        case CONSTANT_V_MODEL.SOFTWARE_UNIT_TEST:
          const softwareUnitTest = this.contentRight.find(ele => ele.label === phase.name);
          if (phase?.listProjects?.length > 0) {
            phase?.listProjects.forEach((dataChild: any, index: number) => {
              let newChild;
              if (index < this.amountOfChildInARowSmall) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * index),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else {
                newChild = new ChildVModel('child_',
                LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * index),
                LOCATION_CHILD[phase.name]?.ROW_1.y,
                'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              }

              if (softwareUnitTest) {
                let arrChild: ChildVModel[] = softwareUnitTest?.children as ChildVModel[];
                arrChild.push(newChild as ChildVModel)
              }
            });
          }
          break;
        case CONSTANT_V_MODEL.SOFTWARE_CONSTRUCTION:
          const softwareConstruction = this.contentRight.find(ele => ele.label === phase.name);
          if (phase?.listProjects?.length > 0) {
            phase?.listProjects.forEach((dataChild: any, index: number) => {
              let newChild;
              if (index < this.amountOfChildInARowSmall) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * index),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else if (index >= this.amountOfChildInARowSmall && index < (this.amountOfChildInARow * 2)) {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * (index - this.amountOfChildInARowSmall)),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              } else {
                newChild = new ChildVModel('child_',
                  LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChildSmall * (index - this.amountOfChildInARowSmall)),
                  LOCATION_CHILD[phase.name]?.ROW_1.y,
                  'childRect', this.widthChildSmall, this.heightChildSmall, dataChild.name, dataChild.project_id, this.randomBgColorProject());
              }

              if (softwareConstruction) {
                let arrChild: ChildVModel[] = softwareConstruction?.children as ChildVModel[];
                arrChild.push(newChild as ChildVModel)
              }
            });
          }
          break;
      }
    })
  }
  generateChildForManagementSupportTools(data: Array<any>) {
    data.forEach(phase => {
      const phaseFiltered = this.locationManagement.find(ele => ele.text.title === phase.name);
      if (phase?.listProjects?.length > 0) {
        phase?.listProjects.forEach((dataChild: any, index: number) => {
          let newChild;
          if (index < this.amountOfChildInARowSmall) {
            newChild = new ChildVModel('child_',
              LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChild * index),
              LOCATION_CHILD[phase.name]?.ROW_1.y,
              'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
          } else {
            newChild = new ChildVModel('child_',
              LOCATION_CHILD[phase.name]?.ROW_1.x + (this.distanceChild * index),
              LOCATION_CHILD[phase.name]?.ROW_1.y,
              'childRect', this.widthChild, this.heightChild, dataChild.name, dataChild.project_id, this.randomBgColorProject());
          }
          if (phaseFiltered) {
            let arrChild: ChildVModel[] = phaseFiltered?.children as ChildVModel[];
            arrChild.push(newChild as ChildVModel)
          }
        });
      }
    })
  }
  mouseEnter($event: any, data: any): void {
    let circle = $event.target as HTMLElement;
    let coordinates = circle.getBoundingClientRect();
    let x = `${coordinates.left - 230}px`;
    let y = `${coordinates.top}px`;
    this.renderer.setStyle(this.tooltip.nativeElement, 'left', x);
    this.renderer.setStyle(this.tooltip.nativeElement, 'top', y);
    this.renderer.setStyle(this.tooltip.nativeElement, 'display', 'block');
    this.renderer.setProperty(this.tooltip.nativeElement, 'innerHTML', data);
  }
  mouseLeave($event: any): void {
    this.renderer.setProperty(this.tooltip.nativeElement, 'innerHTML', '');
    this.renderer.setStyle(this.tooltip.nativeElement, 'display', 'none');
  }


  // For Zoom SVG
  onClick(event: MouseEvent) {
    this.endZoomWindow();
  }

  onZoomWindow() {
    this.startZoomWindow();
  }

  private startZoomWindow() {
    this.zoomWindow = true;
    this.saveControlState();
    this.disableControls();
  }

  private endZoomWindow() {
    this.zoomWindow = false;
    this.restoreControlState();
    if (this.boxElement) {
      this.boxElement.remove();
    }
  }

  onDragStart(event: MouseEvent) {
    if (this.zoomWindow) {
      this.startPt = event;
      const div = document.createElement('div');
      div.classList.add('select-box');
      div.style.pointerEvents = 'none';
      div.style.border = '1px solid #55aaff';
      div.style.backgroundColor = 'rgba(75, 160, 255, 0.3)';
      div.style.position = 'fixed';
      this.el.nativeElement.appendChild(div);
      this.boxElement = div;
    }
  }

  onDragEnd(event: MouseEvent) {
    if (this.zoomWindow) {
      const box = this.boxElement.getBoundingClientRect();
      this.zoomToClientRect(box);
      this.endZoomWindow();
    }
  }

  onDragMove(event: MouseEvent) {
    if (this.zoomWindow) {
      this.currentPt = event;
      this.updateBoxElement();
    }
  }

  private updateBoxElement() {
    const { startPt, currentPt, boxElement } = this;
    const top = Math.min(startPt.y, currentPt.y);
    const right = Math.max(startPt.x, currentPt.x);
    const bottom = Math.max(startPt.y, currentPt.y);
    const left = Math.min(startPt.x, currentPt.x);
    boxElement.style.top = `${top}px`;
    boxElement.style.left = `${left}px`;
    boxElement.style.width = `${right - left}px`;
    boxElement.style.height = `${bottom - top}px`;
  }

  private disableControls() {
    this.panZoom.disableControlIcons();
    this.panZoom.disableDblClickZoom();
    this.panZoom.disableMouseWheelZoom();
    this.panZoom.disablePan();
    this.panZoom.disableZoom();
  }

  private saveControlState() {
    const { controlState, panZoom } = this;

    controlState.isControlIconsEnabled = panZoom.isControlIconsEnabled();
    controlState.isDblClickZoomEnabled = panZoom.isDblClickZoomEnabled();
    controlState.isMouseWheelZoomEnabled = panZoom.isMouseWheelZoomEnabled();
    controlState.isPanEnabled = panZoom.isPanEnabled();
    controlState.isZoomEnabled = panZoom.isZoomEnabled();
  }

  private restoreControlState() {
    const { controlState, panZoom } = this;
    const {
      isControlIconsEnabled,
      isDblClickZoomEnabled,
      isMouseWheelZoomEnabled,
      isPanEnabled,
      isZoomEnabled,
    } = controlState;

    isControlIconsEnabled && panZoom.enableControlIcons() || panZoom.disableControlIcons();
    isDblClickZoomEnabled && panZoom.enableDblClickZoom() || panZoom.disableDblClickZoom();
    isMouseWheelZoomEnabled && panZoom.enableMouseWheelZoom() || panZoom.disableMouseWheelZoom();
    isPanEnabled && panZoom.enablePan() || panZoom.disablePan();
    isZoomEnabled && panZoom.enableZoom() || panZoom.disableZoom();
  }

  onZoomObjects() {
    const objects = Array.from(this.svg.nativeElement.querySelectorAll('circle'));
    if (objects.length) {
      this.zoomToClientRect(getBoundingClientRect(objects));
      this.panZoom.zoomBy(1.5);
    }
  }

  private zoomToClientRect(rect: { left: number, top: number, width: number, height: number }) {
    const center = {
      x: rect.left + rect.width / 2,
      y: rect.top + rect.height / 2,
    };

    // Adjust by any parent offsets
    const svgRect = this.svg.nativeElement.getBoundingClientRect();
    center.x -= svgRect.left;
    center.y -= svgRect.top;

    // Adjust by viewport origin
    const sizes = this.panZoom.getSizes();
    const pan = {
      x: (sizes.width / 2) - center.x,
      y: (sizes.height / 2) - center.y,
    };

    const scale = Math.min(
      sizes.width / rect.width,
      sizes.height / rect.height,
    );

    this.panZoom.panBy(pan);
    this.panZoom.zoomBy(scale);
  }

  onFilter(event: { event: PointerEvent, ignore: boolean }) {
    const controlIcons = this.svg.nativeElement.querySelector('#svg-pan-zoom-controls');
    if (controlIcons && controlIcons.contains(event.event.target as Node)) {
      event.ignore = true;
    }
  }

  onClickProject(event: any) {
    if (!this.HAS_VIEW_PROJECT_DETAIL) return
    const projectId = event?.target?.getAttribute('project_id');
    this.dialogCommonService.onOpenCommonDialog({
      component: BoschProjectDetailComponent,
      title: this.translate.instant('projects.detail.title'),
      width: '80vw',
      height: 'auto',
      icon: 'a-icon ui-ic-watch-on',
      type: 'view',
      passingData: {
        type: 'view',
        project_id: projectId || event.project_id
      }
    })
  }

  showListProject(phase: ChildVModel) {
    this.dialogCommonService.onOpenCommonDialog({
      component: ListProjectDialogComponent,
      title: this.translate.instant('vmodel.dialog.list_project.title'),
      width: '615px',
      height: 'auto',
      type: 'view',
      passingData: {
        phase,
        onClickProject: this.onClickProject.bind(this)
      }
    })
  }

  maxNumItemInPhase(phase: ChildVModel) {
    let xOfPhase = phase.x
    if (xOfPhase === this.xContentLeft) {
      switch (phase.height) {
        case this.heightLeftChild.oneLine:
          return this.amountOfChildInARowSmall;
        case this.heightLeftChild.twoLine:
          return this.amountOfChildInARow * 2;
        case this.heightLeftChild.threeLine:
          return this.amountOfChildInARow * 3;
      }
    } else if (xOfPhase === this.xContentRight) {
      switch (phase.height) {
        case this.heightRightChild.oneLine:
          return this.amountOfChildInARowSmall;
        case this.heightRightChild.twoLine:
          return this.amountOfChildInARow * 2;
        case this.heightRightChild.threeLine:
          return this.amountOfChildInARow * 3;
      }
    } else if (xOfPhase === this.xContentMiddle) {
      return this.amountOfChildInARowSmall;
    }
    return 0
  }

  randomBgColorProject(): string {
    const lengthBoschArrayColor = CONFIG.VMODEL.COLORS.length
    const indexRandom = Math.floor((Math.random() * lengthBoschArrayColor)) // from 0 to lengthArrBoschColor - 1
    const randomBgColor: string = (CONFIG.VMODEL.COLORS)[indexRandom]
    return randomBgColor

  }

}

function getBoundingClientRect(elems: Element[]): { left: number, top: number, width: number, height: number } {
  return elems.map(el => el.getBoundingClientRect())
    .reduce((acc, rect) => {
      if (!acc.width && !acc.height) {
        acc.left = rect.left;
        acc.top = rect.top;
        acc.width = rect.width;
        acc.height = rect.height;
      } else {
        acc.width = Math.max(acc.width, Math.abs(rect.left - acc.left) + rect.width);
        acc.height = Math.max(acc.height, Math.abs(rect.top - acc.top) + rect.height);
        acc.left = Math.min(acc.left, rect.left);
        acc.top = Math.min(acc.top, rect.top);
      }
      return acc;
    }, { left: 0, top: 0, width: 0, height: 0 });
}



