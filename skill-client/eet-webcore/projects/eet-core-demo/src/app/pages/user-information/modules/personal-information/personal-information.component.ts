import { PermisisonService } from './../../../../shared/services/permisison.service';
import { formatDate } from '@angular/common';
import {
  AfterViewInit,
  Component,
  HostListener,
  Inject,
  Input,
  LOCALE_ID,
  OnInit,
  ViewChild
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { finalize, debounce, debounceTime } from 'rxjs/operators';
import { ReplaySubject, takeUntil, switchMap, of } from 'rxjs';
import { UserInformationService } from '../../user-information.service';
import { PesonalLearningService } from './assinged-learning/pesonal-learning.service';
import { ProjectInfoService } from './assinged-project/components/project-info/project-info.service';
import { PersonalProjectService } from './assinged-project/personal-project.service';
import { PERSONAL_INFO_CONFIG, TAB_CODE_LIST } from './common/personal-info.contanst';
import { DialogMakeSkillHighlightComponent } from './components/dialog/dialog-make-skill-highlight/dialog-make-skill-highlight.component';
import { PersonalInfomationModel } from './personal-infomation.model';
import { PersonalInfomationService } from './personal-infomation.service';
import { MatPaginator } from '@angular/material/paginator';
import { PaginDirectionUtil } from 'projects/eet-core-demo/src/app/shared/utils/paginDirectionUtil';
import { HistoricalLevel } from './historical-level/models/historical-level.model';
import { Router } from '@angular/router';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { ConfirmDialogComponent } from 'projects/eet-core-demo/src/app/shared/components/dialogs/confirm-dialog/confirm-dialog.component';
import { CanDeactivateGuard } from 'projects/eet-core-demo/src/app/shared/utils/can-deactivate.guard';

@Component({
  selector: 'eet-personal-information',
  templateUrl: './personal-information.component.html',
  styleUrls: ['./personal-information.component.scss'],
})

export class PersonalInformationComponent implements OnInit, AfterViewInit, CanDeactivateGuard {
  @Input() idAssociate!: string;
  public typeUser: string = '';
  public CONFIG = CONFIG;
  public personalInfoDetail!: PersonalInfomationModel;
  public isShowExportBtn: boolean = false;
  public isEditMode: boolean = false;
  public currentSelectedTab: string = TAB_CODE_LIST.PROJECT;
  public tabList: any = PERSONAL_INFO_CONFIG.PERSONAL_INFO_TAB_LIST;
  public currentViewAssociateMode: string = '';
  public editAssociateInfoPermission: boolean = false;
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  private readonly DEFAULT_PAGE = CONFIG.PAGINATION.DEFAULT_PAGE;
  private readonly DEFAULT_SIZE = CONFIG.PAGINATION.DEFAULT_SIZE;

  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  readonly paginationSize = CONFIG.PAGINATION_OPTIONS;
  constructor(
    public dialog: MatDialog,
    private personalInfomationService: PersonalInfomationService,
    private personalProjectService: PersonalProjectService,
    private pesonalLearningService: PesonalLearningService,
    private matIconRegistry: MatIconRegistry,
    private sanitizer: DomSanitizer,
    private loader: LoadingService,
    private projectInfoService: ProjectInfoService,
    private notify: NotificationService,
    private translateService: TranslateService,
    private userInformationService: UserInformationService,
    private permisisonService: PermisisonService,
    private router: Router,
    @Inject(LOCALE_ID) private locale: string
  ) {
    this.matIconRegistry.addSvgIcon(
      'star',
      this.sanitizer.bypassSecurityTrustResourceUrl('assets/images/star.svg')
    );
  }
  @HostListener('window:beforeunload', ['$event'])
  onWindowClose(event: any): void {
    if (this.isEditMode) {
      event.preventDefault();
      event.returnValue = false;
    }
  }
  ngOnInit(): void {
    this.userInformationService._getCurrentAssociateInModeView.pipe(takeUntil(this.destroyed$)).subscribe((res: string) => {
      if (res) {
        this.idAssociate = res
      }
    })

    this.userInformationService.getCurrentViewAssociateMode()
      .pipe(takeUntil(this.destroyed$)).subscribe(result => {
        if (this.router.url?.split('/')[2]?.includes(CoreUrl.PERSONAL_INFO)) {
          this.currentViewAssociateMode = '';
          this.idAssociate='';
        }else{
          this.currentViewAssociateMode = result;
        }
      
      });

      this.getPermission();
      this.personalInfomationService.personalInfoDetail.next({});
      this.getSharedData();
      this.typeUser = this.idAssociate ? CONFIG.TYPE_USER.ASSOCIATE : CONFIG.TYPE_USER.MANAGER;
      let iduser = this.idAssociate ? this.idAssociate : JSON.parse(localStorage.getItem('Authorization') || '{}').id;
      this.getPersonalInfoDetail(iduser);
      this.getPersonalHistorycalLevel(iduser);
  }
  ngAfterViewInit() {
    PaginDirectionUtil.expandTopForDropDownPagination()
  }
  getPersonalInfoDetail(loginId: string) {
    const loader = this.loader.showProgressBar();
    this.personalInfomationService.getPersonInfo(loginId)
      .pipe(finalize(() => this.loader.hideProgressBar(loader)))
      .pipe(takeUntil(this.destroyed$))
      .subscribe(data => {
        this.personalInfoDetail = data;
        this.personalInfomationService.setPersonalInfoDetail(this.personalInfoDetail);
        this.getAssignProject(loginId);
        this.getAssignLearning(loginId);
        this.onTabSwitch(this.tabList.findIndex((f: any) => f.code == this.currentSelectedTab));
      })
  }

  getAssignProject(loginId: string) {
    const loader = this.loader.showProgressBar();
    this.personalProjectService.getPersonProject(loginId)
      .pipe(finalize(() => this.loader.hideProgressBar(loader)))
      .subscribe(data => {
        this.personalInfomationService.setPersonalInfoDetail({ projects: data });
        this.personalInfomationService.projectCloneList = data;
      })
  }

  getAssignLearning(loginId: string) {
    const loader = this.loader.showProgressBar();
    this.pesonalLearningService.getPersonLearning(loginId)
      .pipe(finalize(() => this.loader.hideProgressBar(loader))).subscribe(data => {
        this.personalInfomationService.setPersonalInfoDetail({ courses: data });
      })
  }

  //Button Back list Associate
  btnClickGoBack() {
    if(this.isEditMode){
      const confirmDialogRef = this.dialog.open(ConfirmDialogComponent, {
        data: {
          title: this.translateService.instant('editor.dialog.confirm.title'),
          content: this.translateService.instant('editor.dialog.confirm.content'),
          btnConfirm: this.translateService.instant('editor.dialog.confirm.yes'),
          btnCancel: this.translateService.instant('editor.dialog.confirm.no'),
          icon: 'a-icon ui-ic-alert-warning',
        },
        width: '420px',
      });
      confirmDialogRef.afterClosed().subscribe((response) => {
        if (response) {
          this.userInformationService.setCurrentViewAssociateMode('a');
        }
      });
    }else {
      this.userInformationService.setCurrentViewAssociateMode('a');

    }
    
  }

  //edit button My Personal Info
  onEdit() {
    this.isEditMode = true;
    this.userInformationService.setEditMode(this.isEditMode);
  }

  onSave() {
    this.personalInfomationService.getPersonalInfoDetail().subscribe(data => {
      this.personalInfoDetail = data as PersonalInfomationModel;
    });
    if (!this.isEditInforValid()) return;
    this.personalInfomationService.personalInfoDetail.value.projects?.map(m => {
      m.start_date = formatDate(m.start_date, 'dd/MM/yyyy', this.locale);
      if (m.end_date !== undefined) {
        m.end_date = formatDate(m?.end_date, 'dd/MM/yyyy', this.locale);
      }
    })
    this.personalInfomationService.personalInfoDetail.value.courses?.map(m => m.start_date = formatDate(m.start_date, 'dd/MM/yyyy', this.locale))
    const loader = this.loader.showProgressBar();
    this.personalInfomationService.savePersonalInfo(this.personalInfomationService.personalInfoDetail.value)
      .pipe(finalize(() => this.loader.hideProgressBar(loader)))
      .subscribe(data => {
        if (data) {
          this.isEditMode = false;
          this.userInformationService.setEditMode(this.isEditMode)
          this.notify.success(this.translateService.instant('notification.save_user-information_success'));
          this.getPersonalInfoDetail(this.personalInfomationService._idUser);
        }
      })
  }

  onCancel() {
    const confirmDialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: this.translateService.instant('editor.dialog.confirm.title'),
        content: this.translateService.instant('editor.dialog.confirm.content'),
        btnConfirm: this.translateService.instant('editor.dialog.confirm.yes'),
        btnCancel: this.translateService.instant('editor.dialog.confirm.no'),
        icon: 'a-icon ui-ic-alert-warning',
      },
      width: '420px',
    });
    confirmDialogRef.afterClosed().subscribe((response) => {
      if (response) {
        this.isEditMode = false;
        this.userInformationService.setEditMode(this.isEditMode)
        this.getPersonalInfoDetail(this.personalInfomationService._idUser);
      }
    });
  }

  //check permission to show button define by mockup
  public onTabSwitch(tabIndex: number) {
    this.currentSelectedTab = this.tabList[tabIndex].code;
    if (this.currentSelectedTab == TAB_CODE_LIST.SKILL) {
      this.isShowExportBtn = true;
    } else {
      this.isShowExportBtn = false;
    }
  }

  export() {
    const loader = this.loader.showProgressBar();
    this.personalInfomationService.getExport(this.personalInfoDetail.id)
      .pipe(finalize(() => this.loader.hideProgressBar(loader)))
      .subscribe((data) => {
        if (data) {
          var FileSaver = require('file-saver');
          const blob = new Blob([data], {
            type: 'application/pdf',
          });
          FileSaver.saveAs(blob, this.personalInfoDetail.name.toString() + '.pdf');
        }
      });
  }

  onMakingSkillHighlight() {
    const dialogRef = this.dialog.open(DialogMakeSkillHighlightComponent, {
      data: {
        title: this.translateService.instant('personal_information.dialog.make_skill_highlight.title'),
        data: this.personalInfoDetail.id
      },
      width: "500px"
    })
  }

  // GET SHARED DATA
  getSharedData() {
    this.getProjectGB();
    this.getProjectRole();
  }
  getProjectRole() {
    this.projectInfoService.getProjectRole().subscribe(data => {
      this.personalInfomationService.setSharedData({ roleList: data });
    })
  }
  getProjectGB() {
    this.projectInfoService.getProjectGB().subscribe(data => {
      this.personalInfomationService.setSharedData({ gbList: data });
    })
  }
  isEditInforValid(): boolean {
    return this.personalInfomationService.isEditInforValid();
  }
  getPersonalHistorycalLevel(id: string) {
    const loader = this.loader.showProgressBar();
    this.personalInfomationService.getHistorycalLevel(this.DEFAULT_PAGE, this.DEFAULT_SIZE, id, '')
      .pipe(
        switchMap((response) => {
          if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
            const totalItems = response.data.totalItem === 0 ? this.DEFAULT_SIZE : response.data.totalItem;
            return this.personalInfomationService.getHistorycalLevel(this.DEFAULT_PAGE, totalItems, id, '');
          }
          return of(undefined);
        })
      )
      .pipe(
        takeUntil(this.destroyed$),
        finalize(() => this.loader.hideProgressBar(loader))
      )
      .subscribe((response: any) => {
        if (response && response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          const histories: HistoricalLevel[] = response.data.histories as Array<HistoricalLevel>;
          this.personalInfomationService.setSharedData({ historicalLevel: histories });
        }
        else {
          this.personalInfomationService.setSharedData({ historicalLevel: [] });
        }
      });
  }
  ngOnDestroy(): void {

    this.destroyed$.next(true);
    this.destroyed$.complete();
    this.dialog.closeAll();

    PaginDirectionUtil.removeEventListenerForDirectionExpandPagination()
  }

  getPermission() {
    this.editAssociateInfoPermission = this.permisisonService.hasPermission(CONFIG.PERMISSIONS.EDIT_ASSOCIATE_INFO_PERMISSION);
  }

  isSelfInformation() {
    return (this.typeUser === CONFIG.TYPE_USER.MANAGER) ? true : false
  }

  canDeactivate(): boolean | Promise<boolean> | import('rxjs').Observable<boolean> { 
    if(this.userInformationService.getEditMode()) {
      this.userInformationService.setOldIndex(0)
      const confirmation = confirm('This page is asking you to confirm that you want to leave — information you\'ve entered may not be saved.');
      if (confirmation == true) this.userInformationService.setEditMode(false);
      this.userInformationService.setConfirm(confirmation);
      return confirmation;
    } else {
        return true;
    }
  }
}
