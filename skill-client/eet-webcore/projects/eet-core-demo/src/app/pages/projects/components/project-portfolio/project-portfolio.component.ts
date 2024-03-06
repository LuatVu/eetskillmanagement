import { Component, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ProjectsService } from '../../services/projects.service';
import { Location } from '@angular/common';
import { BoschProjectDetailComponent } from '../bosch-project-detail/bosch-project-detail.component';
import { TranslateService } from '@ngx-translate/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { PermisisonService } from 'projects/eet-core-demo/src/app/shared/services/permisison.service';
import { ProjectPorfolio, ProjectScope } from '../../models/projects.model';
import { catchError, finalize } from 'rxjs/operators';
import { throwError } from 'rxjs/internal/observable/throwError';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { PROJECT_PORTFOLIO } from '../../constants/projects-default-parameters.constants';
import { NotificationService } from '@bci-web-core/core';
import { LayoutService } from '../../../overview/layout.service';
import { STATUS } from 'angular-in-memory-web-api';
import { EMPTY } from 'rxjs/internal/observable/empty';
import { BaseResponseModel } from 'projects/eet-core-demo/src/app/shared/models/base.model';
import { of } from 'rxjs';
import { DEFAULT_COLOUR_MIXED_PROJECT_SCOPE, DEFAULT_COLOUR_PROJECT_SCOPE } from '../../../system/model/manage-project-scope/manage-project-scope.constant';
import { ManageProjectScopeService } from '../../../system/services/manage-project-scope.service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from 'projects/eet-core-demo/src/app/shared/components/dialogs/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'eet-project-portfolio',
  templateUrl: './project-portfolio.component.html',
  styleUrls: ['./project-portfolio.component.scss']
})

export class ProjectPortfolioComponent implements OnInit {
  @HostListener('window:beforeunload', ['$event'])
  onWindowClose(event: any): void {
    if (this.isEditHighlightMode) {
      event.preventDefault();
      event.returnValue = false;
    }
  }
  public projectId: string;
  public HAS_EDIT_PORTFOLIO: boolean = false;
  public HAS_VIEW_PROJECT_DETAIL: boolean;
  public dataProjectPorfolio: ProjectPorfolio = {
    projectName: null,
    highlight: null,
    skillTags: [],
    teamSize: null,
    customerGB: null,
    projectScopeName: null,
    projectScopeId: null
  }
  public PROJECT_HIGHLIGHT = PROJECT_PORTFOLIO.HIGHLIGHT
  public PROJECT_BENEFIT = PROJECT_PORTFOLIO.BENEFITS
  public PROJECT_SOLUTION = PROJECT_PORTFOLIO.SOLUTION
  public PROJECT_PROBLEM_STATEMENT = PROJECT_PORTFOLIO.PROBLEM_STATEMENT
  private currentProjectScope: string
  public currentBgHover: string
  public highlight: any
  public isEditHighlightMode: boolean = false
  constructor(
    private router: Router,
    private projectsService: ProjectsService,
    private location: Location,
    private translate: TranslateService,
    private dialogCommonService: DialogCommonService,
    private permisisonService: PermisisonService,
    private comLoader: LoadingService,
    private notify: NotificationService,
    private layoutService: LayoutService,
    private manageProjectScopeSerivce:ManageProjectScopeService,
    public dialog: MatDialog,
  ) {
    this.projectId = this.router.getCurrentNavigation()?.extras.state?.projectId;
    this.HAS_VIEW_PROJECT_DETAIL = this.permisisonService.hasPermission(
      CONFIG.PERMISSIONS.VIEW_PROJECT_DETAIL
    );
    this.HAS_EDIT_PORTFOLIO = this.permisisonService.hasPermission(
      CONFIG.PERMISSIONS.EDIT_PROJECT
    );
  }



  ngOnInit(): void {
    if (this.projectId) {
      this.projectsService.getProjectPortfolio(this.projectId)
        .pipe(
          catchError((error: any) => {
            this.router.navigateByUrl(`${CoreUrl.PROJECTS}/${CoreUrl.PROJECT_LIST}`);
            return throwError(error);
          })
        )
        .subscribe((response: any) => {
          if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
            this.dataProjectPorfolio = {
              projectName: response?.data.name,
              customerGB: response.data?.customer_gb,
              skillTags: response.data?.skill_tags || [],
              teamSize: response.data?.team_size,
              projectScopeName: response.data?.project_scope_name,
              projectScopeId: response.data?.project_scope_id,
              highlight: response.data?.highlight,
            }
            this.currentProjectScope = response.data.project_scope_id
            this.highlight = this.dataProjectPorfolio.highlight
          }


        });
      this.getProjectPortfolioLayout(this.PROJECT_BENEFIT)
      this.getProjectPortfolioLayout(this.PROJECT_SOLUTION)
      this.getProjectPortfolioLayout(this.PROJECT_PROBLEM_STATEMENT)


      this.handleBackgroundHeaderDependOnProjectScope()
    }else{
      this.router.navigateByUrl(`${CoreUrl.PROJECTS}/${CoreUrl.PROJECT_LIST}`);
    }

  }

  back() {
    this.location.back();
  }

  onDetailProject(projectId: string, openType: string) {
    const DIALOG_WIDTH = '75vw';
    let openTypeDetail: { title: string, icon: string };
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
    })
  }

  getDetailBoschProjectTitle(openType: string) {
    let projectTitle: { title: string, icon: string } = {
      title: this.translate.instant('projects.detail.title'),
      icon: ''
    }
    switch (openType) {
      case CONFIG.PROJECT.OPEN_PROJECT_TYPE.VIEW:
        projectTitle = {
          title: this.translate.instant('projects.detail.title'),
          icon: 'a-icon ui-ic-watch-on'
        }
        this.translate.instant('projects.detail.title');
        break;
      case CONFIG.PROJECT.OPEN_PROJECT_TYPE.EDIT:
        projectTitle = projectTitle = {
          title: this.translate.instant('projects.detail.title_edit'),
          icon: 'a-icon a-button__icon boschicon-bosch-ic-edit'
        };
        break;
      default:
        break;
    }
    return projectTitle;
  }

  truncateText(text: string, num: number = 12) {
    return text?.length > num ? text.slice(0, num) + "..." : text
  }
  handleEventEditor(e: any) {

    switch (e.type) {
      case 'save':
        this.handleSave(e)
        break
      case 'preview':
        this.handlePreView()
        break
      case 'edit':
        this.handleEdit()
        break
      case 'cancel':
        this.handleCancel()
        break
      default:

    }
  }
  handleSave(e: any) {
    const loader = this.comLoader.showProgressBar();
    const formData = this.createLayout(e.data, e.layoutType)
    this.projectsService
      .saveLayoutForProjectPortfolio(this.projectId, e.layoutType, formData)
      .pipe(
        catchError((err: any) => {
          this.notify.error(this.translate.instant("editor.maximum_upload_size_exceeded"))
          this.layoutService.setStatusFailedForSaveEditor({ isFailed: true, data: e.data, layoutType: e.layoutType })
          return of(err);
        }),
        finalize(() => this.comLoader.hideProgressBar(loader))
      )
      .subscribe((rs: any) => {
        if (rs.status === STATUS.BAD_REQUEST) {
          this.notify.error(this.translate.instant("editor.maximum_upload_size_exceeded"))
          this.layoutService.setStatusFailedForSaveEditor({ isFailed: true, data: e.data, layoutType: e.layoutType })
        } else if (rs.status === STATUS.INTERNAL_SERVER_ERROR) {
          this.notify.error(this.translate.instant("editor.server_error"))
        } else if (rs.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.notify.success(this.translate.instant("editor.save_success"))
          this.layoutService.setStatusFailedForSaveEditor({ isFailed: false, layoutType: e.layoutType })
          this.getProjectPortfolioLayout(e.layoutType)
        }
      });
  }
  getProjectPortfolioLayout(layoutType: string) {
    const loader = this.comLoader.showProgressBar();
    this.projectsService
      .getProjectPortfolioLayout(this.projectId, layoutType)
      .pipe(
        catchError((err) => {
          return EMPTY;
        }),
        finalize(() => this.comLoader.hideProgressBar(loader))
      )
      .subscribe((rs: BaseResponseModel) => {
        setTimeout(() => {
          this.layoutService.setDataLayoutObservable({ layoutType, data: rs.data })
        }, 100)
      });
  }
  createLayout(fileContent: string, layoutName: string) {

    const blob = new Blob([fileContent], { type: 'text/plain' });
    const file = new File([blob], layoutName + '.txt');

    const formData: FormData = new FormData();
    formData.append('file', file, file.name);

    return formData
  }
  handlePreView() {
    const loader = this.comLoader.showProgressBar();
    setTimeout(() => {
      this.comLoader.hideProgressBar(loader);
    }, 100);
  }
  handleEdit() {
    const loader = this.comLoader.showProgressBar();
    setTimeout(() => {
      this.comLoader.hideProgressBar(loader);
    }, 100);
  }
  handleCancel() {

  }
  handleBackgroundHeaderDependOnProjectScope() {
    this.projectsService.getAllProjectScope().subscribe((result: BaseResponseModel) => {
      if (result.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
        const currentScopeObj = (result.data.filter((e: ProjectScope) => e.id === this.currentProjectScope))[0]
        const firstColorMix = currentScopeObj?.colour || DEFAULT_COLOUR_PROJECT_SCOPE
        const SecondColorMixSecond = currentScopeObj?.hover_colour || DEFAULT_COLOUR_MIXED_PROJECT_SCOPE
        this.currentBgHover = this.manageProjectScopeSerivce.linearBackgroundProjectCard(SecondColorMixSecond, firstColorMix)

      }
    });
  }
  onEditHighlight() {
    this.isEditHighlightMode = true
  }
  onSaveHighlight() {
    this.isEditHighlightMode = false
    const loader = this.comLoader.showProgressBar();
    this.projectsService.saveHighlightProject(this.projectId, { project_id: this.projectId, highlight: this.highlight }).pipe(
      finalize(() => this.comLoader.hideProgressBar(loader))
    ).subscribe((result: BaseResponseModel) => {
      if (result.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
        this.isEditHighlightMode = false
        this.notify.success(this.translate.instant('project_porfolio.save_highlight_success'))
      }
    });

  }
  onCancelHighlight() {
    const replaceDialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: this.translate.instant('editor.dialog.confirm.title'),
        content: this.translate.instant('editor.dialog.confirm.content'),
        btnConfirm: this.translate.instant('editor.dialog.confirm.yes'),
        btnCancel: this.translate.instant('editor.dialog.confirm.no'),
        icon: 'a-icon ui-ic-alert-warning',
      },
      width: '420px',
    });
    replaceDialogRef.afterClosed().subscribe((response) => {
      if (response) {
        this.isEditHighlightMode = false
        this.projectsService.getProjectPortfolio(this.projectId)
        .subscribe((response: any) => {
          if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
            this.highlight =  response.data?.highlight
          }
        });
      }
    });
  

  }
}


