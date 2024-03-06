import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { Component, Inject, Input, LOCALE_ID, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { Observable, ReplaySubject, finalize, takeUntil } from 'rxjs';
import { forkJoin } from 'rxjs/internal/observable/forkJoin';
import { ViewImageService } from '../../../../../shared/services/view-image.service';
import { AddNewAssociateSharedModel } from '../../associate-information/add-new-associate/add-new-associate.model';
import { EditAssociateService } from '../../associate-information/edit-associate/edit-associate.service';
import { DialogUploadUploadAvatarComponent } from '../components/dialog/dialog-upload-upload-avatar/dialog-upload-upload-avatar.component';
import { PersonalInfomationModel } from '../personal-infomation.model';
import { PersonalInfomationService } from '../personal-infomation.service';

@Component({
  selector: 'eet-detail-infomation',
  templateUrl: './detail-infomation.component.html',
  styleUrls: ['./detail-infomation.component.scss']
})
export class DetailInfomationComponent implements OnInit {
  @Input() isEditMode: boolean = false;
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  public personalInfomation!: Partial<PersonalInfomationModel>;
  private colorAvatar = CONFIG.AVATAR_COLOR_LIST;
  public editDetailInformationModel = {}
  public brief_info: string = "";

  constructor(
    private editAssociateInfoService: EditAssociateService,
    private personalInfomationService: PersonalInfomationService,
    private dialog: MatDialog,
    private dialogCommonService: DialogCommonService,
    private viewImageService: ViewImageService,
    private loaderService: LoadingService,
    @Inject(LOCALE_ID) private locale: string
  ) { }

  ngOnInit() {
    this.personalInfomationService.getPersonalInfoDetail()
      .pipe(takeUntil(this.destroyed$))
      .subscribe(data => {
        if (data) {
          this.personalInfomation = data as PersonalInfomationModel;
          this.configPersonalInfomation();
        }
      })

    this.editAssociateInfoService.getSharedData()
      .pipe(takeUntil(this.destroyed$))
      .subscribe((data: Partial<AddNewAssociateSharedModel>) => {
        if (!data['isStoredState']) {
          this.getSharedData();
        } else {
          this.editDetailInformationModel = data;
        }
      });
  }
  getSharedData() {
    const loader = this.loaderService.showProgressBar();
    const requestList: Array<Observable<any>> = [];
    requestList.push(
      this.editAssociateInfoService.getAllLevel(),
      this.editAssociateInfoService.getAllGender(),
      this.editAssociateInfoService.getAllDepartment(),
      this.editAssociateInfoService.getAllGroup(),
      this.editAssociateInfoService.getAllTeam(),
      this.editAssociateInfoService.getAllTitle(),
      this.editAssociateInfoService.getAllLocation(),
      this.editAssociateInfoService.getAllLineManager(),
      this.editAssociateInfoService.getAllSkillGroup()
    );

    forkJoin(requestList)
      .pipe(takeUntil(this.destroyed$)).pipe(finalize(() => this.loaderService.hideProgressBar(loader))).subscribe(responses => {
        if (responses) {
          this.editAssociateInfoService.setSharedData({
            isStoredState: true,
            levelList: responses[0]?.data || [],
            genderList: responses[1] || [],
            departmentList: responses[2]?.data || [],
            groupList: responses[3]?.data || [],
            teamList: responses[4]?.data || [],
            titleList: responses[5] || [],
            locationList: responses[6] || [],
            lineManagerList: responses[7]?.data || [],
            skillGroupList: responses[8]?.data || [],
          })
        }
      })
  }
  configPersonalInfomation() {
    this.personalInfomation.ntid = this.personalInfomation.code;
    const random = Math.floor(Math.random() * 6);
    this.personalInfomation.avatarcolor = this.personalInfomation.avatarcolor || this.colorAvatar[random].color;
    this.personalInfomation.avatarbgcolor = this.personalInfomation.avatarbgcolor || this.colorAvatar[random].backgroundColor;
    this.personalInfomation.shortName = this.getInitials(this.personalInfomation?.name);
    this.personalInfomation?.top_skills?.map((element: any) => element.experience_number = Number(element.experience_number));
    this.personalInfomation.total_exp = Number(this.personalInfomation.experienced_at_bosch || 0) + Number(this.personalInfomation.experienced_non_bosch || 0);
    this.personalInfomation.brief_info = this.personalInfomation.brief_info;
  }

  //avatar with short name
  getInitials(nameString?: string): string {
    if (!nameString) { return '' };
    // Name format: Fullname (Department) so we need to cut Fullname
    const formatName: string[] = nameString?.split('(');
    const fullName = formatName[0]?.trim()?.split(' ');
    // After having fullname cut like: 'Your Fullname' we need to use YF as shortname
    if (fullName && fullName.length != 0) {
      return fullName[0]?.substring(0, 1) + fullName[fullName.length - 1].substring(0, 1);
    }
    return '';
  }

  openUpload() {
    this.dialogCommonService.onOpenCommonDialog({
      component: DialogUploadUploadAvatarComponent,
      title: 'Upload Avatar',
      width: '600px',
      height: 'auto',
      icon: 'a-icon boschicon-bosch-ic-upload',
      type: 'edit',
      passingData: {
      }
    }).afterClosed().subscribe(result => {
    })
  }

  // onNonBoschExpChange() {
  //   this.personalInfomationService.setPersonalInfoDetail({ experienced_non_bosch: this.personalInfomation.experienced_non_bosch });
  // }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  onViewImage() {
    this.viewImageService.onViewImage(`data:image/jpeg;base64,${this.personalInfomation.picture}`);
  }

  get userAvatar() {
    return `url("data:image/jpeg;base64,${this.personalInfomation.picture}")`
  }

  onTeamChange(value: string) {
    this.personalInfomation.team_id = value
    const loader = this.loaderService.showProgressBar();
    this.editAssociateInfoService.getGroupByTeam(value).pipe(finalize(() => { this.loaderService.hideProgressBar(loader) })).subscribe((res) => {
      if (res.code === "SUCCESS") {
        this.personalInfomation.group = res.data.name
      }
    })
    const loader1 = this.loaderService.showProgressBar();
    this.editAssociateInfoService.getLineManagerByTeam(value).pipe(finalize(() => { this.loaderService.hideProgressBar(loader1) })).subscribe((res) => {
      if (res.code === "SUCCESS") {
        this.personalInfomation.supervisor_name = res.data?.name || "--"
      }
    })
  }

  onManagerChange(value: string) {
    this.personalInfomation.manager = value
  }

}
