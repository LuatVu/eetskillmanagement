import { animate, keyframes, style, transition, trigger } from '@angular/animations';
import { AfterViewInit, ChangeDetectorRef, Component, Inject, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { ProjectsService } from '../../../services/projects.service';
import { PermisisonService } from 'projects/eet-core-demo/src/app/shared/services/permisison.service';
import { APP_BASE_HREF } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { NotificationService } from '@bci-web-core/core';
import { TranslateService } from '@ngx-translate/core';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { finalize } from 'rxjs/operators';
import { ProjectScope, Projects } from '../../../models/projects.model';
import { BoschProjectDetailComponent } from '../../bosch-project-detail/bosch-project-detail.component';
import { NonBoschProjectDetailComponent } from '../../non-bosch-project-detail/non-bosch-project-detail.component';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { DEFAULT_COLOUR_MIXED_PROJECT_SCOPE, DEFAULT_COLOUR_PROJECT_SCOPE } from '../../../../system/model/manage-project-scope/manage-project-scope.constant';
import { ManageProjectScopeService } from '../../../../system/services/manage-project-scope.service';
import { Project } from '../../../../supply-demand/supply.model';

@Component({
  selector: 'eet-project-item',
  templateUrl: './project-item.component.html',
  styleUrls: ['./project-item.component.scss', '../../project-list/project-list.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ProjectItemComponent implements OnInit,AfterViewInit {
  public readonly NOT_APPLICABLE = CONFIG.COMMON_FORM.NOT_APPLICABLE;
  @Input() project: any;
  @Input() index: any;
  @Input() query:any
  @Input() scopeColor:string
  @Input() scopeColorMixed:string
  @Input() previewMode:boolean=false
  public HAS_VIEW_PROJECT_DETAIL: boolean;
  public HAS_EDIT_PROJECT: boolean;
  public HAS_DELETE_PROJECT: boolean
  public BOSCH_TYPE = CONFIG.PROJECT.PROJECT_TYPE.BOSCH
  public NON_BOSCH_TYPE = CONFIG.PROJECT.PROJECT_TYPE.NON_BOSCH
  private projectScopeList:ProjectScope[] = []
  private currentBgHover:string=''
  public currentBgColor:string = ''
  private START_STATE='start'
  private END_STATE='end'
  constructor(
    @Inject(APP_BASE_HREF) public baseHref: string,
    private dialogCommonService : DialogCommonService,
    private permissionService : PermisisonService,
    private translate: TranslateService,
    private notifyService: NotificationService ,
    private http: HttpClient,
    private loaderService: LoadingService,
    private projectsService: ProjectsService,
    private manageProjectScopeSerivce:ManageProjectScopeService,
    private cdr: ChangeDetectorRef
  ) {
    this.HAS_VIEW_PROJECT_DETAIL = permissionService.hasPermission(CONFIG.PERMISSIONS.VIEW_PROJECT_DETAIL);
    this.HAS_EDIT_PROJECT = permissionService.hasPermission(CONFIG.PERMISSIONS.EDIT_PROJECT);
    this.HAS_DELETE_PROJECT = permissionService.hasPermission(CONFIG.PERMISSIONS.DELETE_PROJECT)
  }
  ngAfterViewInit(): void {
    if(!this.previewMode){
      this.applyBackgroundForProject()
    }
  }

  ngOnInit(): void {
    this.projectsService._getProjectScope.subscribe((res) => {
      if(res && res.length > 0){
        this.projectScopeList =Helpers.cloneDeep(res) 
      }
    })


  }
  detectChanges() {
    this.cdr.detectChanges();
  }
  // Trigger animation on hover
  currentState = this.START_STATE;
  hoveredItemIndex: number | null = null;
  onMouseEnter(i: number,event?:any) {
    this.currentState = this.END_STATE;
    this.hoveredItemIndex = i

    const headerCard = event.target.getElementsByClassName('card-header')[0] as HTMLElement
    if(this.currentBgHover) {
      if(headerCard.style?.background){
        headerCard.style.background=''
      }
      headerCard.style.background = this.currentBgHover
    }


    if(this.scopeColor && this.scopeColorMixed){
      if(headerCard.style?.background){
        headerCard.style.background=''
      }
      headerCard.style.background = this.manageProjectScopeSerivce.linearBackgroundProjectCard(this.scopeColorMixed,this.scopeColor)
    }
    this.detectChanges()
  }

  onMouseLeave(event?:any) {
    this.currentState = this.START_STATE;
    this.hoveredItemIndex = null
    const headerCard = (event.target.getElementsByClassName('card-header')[0] as HTMLElement)

    if(this.currentBgHover) {
      if(headerCard.style?.background){
        headerCard.style.background=''
      }
      headerCard.style.background = this.currentBgHover
    }

    if(this.scopeColor && this.scopeColorMixed){
      if(headerCard.style?.background){
        headerCard.style.background=''
      }
      headerCard.style.background = this.manageProjectScopeSerivce.linearBackgroundProjectCard(this.scopeColorMixed,this.scopeColor)
    }
    
  }
  public teamSizeHandle(teamSize: string) {
    if (teamSize.includes(".0")) {
      return teamSize.replace(".0", "")
    } else return teamSize;
  }
  onDetailProject(e:any,projectId: string, projectType: string, openType: string) {
    e.stopPropagation()
    if(this.previewMode) return
    const DIALOG_WIDTH = '75vw';
    let openTypeDetail: { title: string, icon: string };
    switch (projectType) {
      case CONFIG.PROJECT.PROJECT_TYPE.BOSCH:
        openTypeDetail = this.getDetailBoschProjectTitle(openType);
        this.dialogCommonService.onOpenCommonDialog({
          component: BoschProjectDetailComponent,
          height: 'auto',
          width: DIALOG_WIDTH,
          title: openTypeDetail.title,
          icon: openTypeDetail.icon,
          maxWdith: '1200px',
          type: openType,
          passingData: {
            type: openType,
            project_id: projectId
          }
        }).afterClosed().subscribe((response) => {
          if (response) {
            if (openType === 'edit') {
              this.projectsService.addProjectEvent.emit()
            }
          }
        });
        break;
      case CONFIG.PROJECT.PROJECT_TYPE.NON_BOSCH:
        openTypeDetail = this.getDetailNonBoschProjectTitle(openType);
        const dialogRef = this.dialogCommonService.onOpenCommonDialog({
          component: NonBoschProjectDetailComponent,
          title: openTypeDetail.title,
          icon: openTypeDetail.icon,
          width: DIALOG_WIDTH,
          maxWdith: '1200px',
          height: 'auto',
          type: openType,
          passingData: {
            type: openType,
            project_id: projectId
          }
        }).afterClosed().subscribe((response) => {
          if (response) {
            if (openType === 'edit') {
              const comLoader = this.loaderService.showProgressBar()
              // wait data from elastic is saved
              setTimeout(() => {
                this.loaderService.hideProgressBar(comLoader)
                this.projectsService.addProjectEvent.emit()
                this.notifyService.success(this.translate.instant('personal_project.update_non_bosch_project_successfully'));
              }, 1000)
            }
          }
        });
        break;
    }

  }

  viewPortfolio(projectId: string, projectType: string, openType?: string) {
    if(this.previewMode) return
    this.projectsService.viewProjectDetailEvent.emit(projectId);
  }
  getDetailBoschProjectTitle(openType: string){
    let projectTitle : { title:string, icon: string} = { 
      title: this.translate.instant('projects.detail.title'),
      icon:''
    }
    switch(openType){
      case CONFIG.PROJECT.OPEN_PROJECT_TYPE.VIEW:
        projectTitle = { 
          title: this.translate.instant('projects.detail.title'),
          icon:'a-icon ui-ic-watch-on'
        }
        this.translate.instant('projects.detail.title');
        break;
      case CONFIG.PROJECT.OPEN_PROJECT_TYPE.EDIT:
        projectTitle = projectTitle = { 
          title: this.translate.instant('projects.detail.title_edit'),
          icon:'a-icon a-button__icon boschicon-bosch-ic-edit'
        };
        break;
      default:
        break;
    }
    return projectTitle;
  }
  getDetailNonBoschProjectTitle(openType: string){
    let projectTitle : { title:string, icon: string} = {
      title :this.translate.instant('projects.detail.title_nonBosch'),
      icon: ''
    };
    switch(openType){
      case CONFIG.PROJECT.OPEN_PROJECT_TYPE.VIEW:
        projectTitle = {
          title : this.translate.instant('projects.detail.title_nonBosch'),
          icon:'a-icon ui-ic-watch-on'
        };
        break;
      case CONFIG.PROJECT.OPEN_PROJECT_TYPE.EDIT:
        projectTitle = {
          title: this.translate.instant('projects.detail.title_nonBosch_edit'),
          icon: 'a-icon a-button__icon boschicon-bosch-ic-edit'
        };
        break;
      default:
        break;
    }
    return projectTitle;
  }
  deleteProject(project:Projects) {
    if(this.previewMode) return
    const dialogRef = this.dialogCommonService.onOpenConfirm({
      content: this.translate.instant('projects.delete_prompt.content.name_line') + ': ' + ((project.projectName.length > 20) ? project.projectName.substring(0, 20) + "..." : project.projectName) + '.\n ' + this.translate.instant('projects.delete_prompt.content.revert_line') + '.',
      title: this.translate.instant('dialog.title_confirm'),
      btnConfirm: this.translate.instant('projects.delete_prompt.yes'),
      btnCancel: this.translate.instant('projects.delete_prompt.no'),
      isShowOKButton: false,
      icon: 'a-icon ui-ic-alert-warning'
    })
    dialogRef.afterClosed().subscribe(response => {
      if (response === true) {
        let comLoader = this.loaderService.showProgressBar()
        this.projectsService.deleteProjects(project.projectId).pipe(finalize(() => {
          this.loaderService.hideProgressBar(comLoader)
        })).subscribe(resp => {
          if (resp.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
            this.projectsService.updateProject(true)
            this.notifyService.success(this.translate.instant('projects.delete_prompt.delete_successfully'));
          }
          else {
            this.notifyService.error(resp.message || resp.code)
          }
        }
        )
      }
    })
  }
  applyBackgroundForProject() {
      const currentScopeProject = this.projectScopeList.filter((e) => e.id === this.project.scopeId)[0]
      this.currentBgColor = currentScopeProject?.colour || DEFAULT_COLOUR_PROJECT_SCOPE
      const bgMixed = currentScopeProject?.hover_colour || DEFAULT_COLOUR_MIXED_PROJECT_SCOPE
      this.currentBgHover = this.manageProjectScopeSerivce.linearBackgroundProjectCard(bgMixed, this.currentBgColor)
  }
  

  checkConditionBgColorForHeader() {
    if(this.previewMode){
      return {
        'background': this.manageProjectScopeSerivce.linearBackgroundProjectCard(this.scopeColorMixed,this.scopeColor) || {}
      };
    }else{
      return {
        'background': this.currentBgHover || {}
      };
    }
  }
  truncateText(text: string, num: number = 35) {
    return text?.length > num ? text.slice(0, num) + "..." : text
  }
}
