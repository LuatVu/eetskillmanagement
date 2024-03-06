import { ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { of } from 'rxjs';
import { finalize, switchMap } from 'rxjs/operators';
import * as viewSkillDetailDialogComponent from '../../../../career-development/components/skill-evaluation/skill-detail-dialog/skill-detail-dialog.component';
import { Competency, SkillTypeModel } from '../../../model/competency-lead/competency-lead.model';
import { CompetencyLeadService } from '../../../services/competency-lead.service';
import { AddCompetencyDialogComponent } from '../../dialogs/competency-lead/add-competency-dialog/add-competency-dialog.component';
import { AddCompetencyLeadDialogComponent } from '../../dialogs/competency-lead/add-competency-lead-dialog/add-competency-lead-dialog.component';
import { SkillDetailDialogComponent } from '../../dialogs/manage-skill/skill-detail-dialog/skill-detail-dialog.component';
import { CommonListComponent } from './../../../../user-management/common/components/common-list/common-list.component';

@Component({
  selector: 'eet-competency-lead',
  templateUrl: './competency-lead.component.html',
  styleUrls: ['./competency-lead.component.scss']
})
export class CompetencyLeadComponent implements OnInit {
  @ViewChild('commonListComptetency') commonList: CommonListComponent;
  public competency: Competency[] = []
  public filterCompetency: Competency[] = [];
  public currentCompetencyId: string
  public competencyLead: any[] = []
  private originSourceList: any[] = []
  public sourceList: any[] = this.originSourceList
  public skillTypes : string[] = [];
  public selectedSkillType : string = '';
  private originSkillTypes: SkillTypeModel[] = []
  public selectedOption: any;
  public selectedOptionLead: any;

  constructor(
    public dialog: DialogCommonService,
    public competencyLeadService: CompetencyLeadService,
    public comLoader: LoadingService,
    public notificationService: NotificationService,
    private translate: TranslateService,
    public changeDetectorRef: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.getAllSkillType();
    this.getCompetency()
  }
  onDeleteItem(event: any, type: "competency" | "competency_lead") {
    if (type === 'competency') {
      const loader = this.comLoader.showProgressBar()
      this.competencyLeadService.deleteCompetency(event.id).pipe(finalize(() => this.comLoader.hideProgressBar(loader))).subscribe((resp) => {
        if (resp.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.notificationService.success(this.translate.instant('system.competency_lead.dialog.delete_competency.success'))
          this.getCompetency()
        }
        else {
          this.notificationService.error(resp.message || resp.code || this.translate.instant('system.competency_lead.dialog.delete_competency.failed'))
        }
      })
    }
    else {
      const loader = this.comLoader.showProgressBar();
      this.competencyLeadService.deleteCompetencyLead(event['id'], this.currentCompetencyId)
        .pipe(finalize(() => this.comLoader.hideProgressBar(loader)))
        .subscribe(response => {
          if (response && response.code == 'SUCCESS') {
            this.notificationService.success(this.translate.instant('system.competency_lead.dialog.delete_competency_lead.success'))
            this.getCompetencyDetail()
          } else {
            this.notificationService.error(response.message || this.translate.instant('system.competency_lead.dialog.delete_competency_lead.failed'))
          }
        })
    }
  }
  onSelectItemInList(event: any, type: 'competency' | 'competency_lead') {
    if (type === 'competency') {
      this.currentCompetencyId = event.id
      this.getCompetencyDetail()
      this.selectedOption = event;
    }
    this.keyword = '';
  }
  onSelectFirstCompetency(competencies: Competency[]){
    if(competencies[0]){
      this.onSelectItemInList(competencies[0], 'competency');
    }
  }
  getCompetency() {
    const loader = this.comLoader.showProgressBar()
    this.competencyLeadService.getCompetency().pipe(finalize(() => {
      this.skillTypeToggleChange(this.selectedSkillType);
      this.comLoader.hideProgressBar(loader);
    })).subscribe((resp) => {
      this.competency = (resp.data as Array<any>).sort((a, b) => {
        const x = a.name.toLowerCase().trim()
        const y = b.name.toLowerCase().trim()
        return x > y ? 1 : x < y ? -1 : 0
      })
    })
  }
  getCompetencyDetail() {
    this.competencyLead = []
    const loader = this.comLoader.showProgressBar()
    this.competencyLeadService.getSkillGroupDetail(this.currentCompetencyId).pipe(finalize(() => {
      this.comLoader.hideProgressBar(loader)
    })).subscribe((resp) => {
      this.competencyLead = resp.data.competency_leads
      this.originSourceList = (resp.data.skills as Array<any>).sort((a, b) => {
        return a.name > b.name ? -1 : a.name < b.name ? 1 : 0
      })
      this.sourceList = Helpers.cloneDeep(this.originSourceList)
    })
  }
  addCompetency() {
    const skilltypeId = this.getSkillTypeId(this.selectedSkillType);
    const title = this.selectedSkillType.toLowerCase().trim() === 'technical'? 'system.competency_lead.dialog.add_competency.technical'
      : 'system.competency_lead.dialog.add_competency.behavioral';
    const dialogRef = this.dialog.onOpenCommonDialog({
      component: AddCompetencyDialogComponent,
      title: title,
      width: "615px",
      icon: 'a-icon boschicon-bosch-ic-add',
      height: 'auto',
      type: 'edit',
      passingData: {

      }
    })
    dialogRef.afterClosed().subscribe((response) => {
      if (response) {
        const payload = {
          name: response,
          skill_type_id: skilltypeId,
          skill_type: this.selectedSkillType
        }
        const loader = this.comLoader.showProgressBar()
        this.competencyLeadService.addCompetency(payload).pipe(finalize(() => this.comLoader.hideProgressBar(loader))).subscribe((resp) => {
          if (resp.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
            this.notificationService.success(this.translate.instant('system.competency_lead.dialog.add_competency.add_success'))
            this.getCompetency();
            }
          else {
            this.notificationService.error(resp.message || resp.code || this.translate.instant('system.competency_lead.dialog.add_competency.add_failed'))
          }
        })
      }
    })
  }
  addCompetencyLead() {
    const dialogRef = this.dialog.onOpenCommonDialog({
      component: AddCompetencyLeadDialogComponent,
      title: 'system.competency_lead.dialog.add_competency_lead.title',
      width: "615px",
      icon: 'a-icon boschicon-bosch-ic-add',
      height: 'auto',
      type: 'edit',
      passingData: {
        name: this.competency.filter((data) => data.id === this.currentCompetencyId)[0].name,
        competencyLeads : this.competencyLead.map(user => user.id),
      }
    })
    dialogRef.afterClosed().subscribe((response) => {
      if (response?.status === true) {
        const loader = this.comLoader.showProgressBar()
        let skill_ids: string[] = [];
        let skill_names: string[] = [];
        this.competencyLeadService.getCompetencyLead(response.personal_id).pipe(finalize(() => this.comLoader.hideProgressBar(loader))).subscribe((originalCompetencyLeadResponse) => {
          const loader = this.comLoader.showProgressBar()
          for (let i of this.originSourceList) {
            skill_ids.push(i.skill_id)
            skill_names.push(i.name)
          }
          for (let i of originalCompetencyLeadResponse.data as Array<any>) {
            skill_ids.push(i.skill_id)
            skill_names.push(i.name)
          }
          const data = [{
            personal_id: response.personal_id,
            display_name: response.display_name,
            skill_ids,
            skill_names,
            skill_group_id: this.currentCompetencyId
          }]
          this.competencyLeadService.addCompetencyLead(data).pipe(finalize(() => {
            this.comLoader.hideProgressBar(loader)
          })).subscribe((resp) => {
            if (resp.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
              this.notificationService.success(this.translate.instant('system.competency_lead.dialog.add_competency_lead.add_success'))
              this.getCompetencyDetail()
            }
            else {
              this.notificationService.error(resp.message || resp.code || this.translate.instant('system.competency_lead.dialog.add_competency_lead.add_failed'))
            }
          })
        })
      }
    })
  }


  public keyword: string = '';
  searchKeyword() {
    this.sourceList = this.createTempSourceList(this.originSourceList);
    this.sourceList = this.sourceList.filter((item) => {
      const searchString = item?.name || item?.displayName || '';
      return searchString.toLowerCase().indexOf(this.keyword.toLowerCase()) > -1;
    });
  }

  createTempSourceList(sourceList: any[]): any[] {
    let tempSourceList: any[] = [];
    sourceList.forEach(function (element) {
      let tempObject = {};
      tempObject = element;
      tempSourceList.push(tempObject);
    });
    return tempSourceList;
  }

  addSkillForCompetency() {
    const dialogRef = this.dialog.onOpenCommonDialog({
      component: SkillDetailDialogComponent,
      title: this.translate.instant('system.competency_lead.dialog.add_skill.title'),
      width: "60vw",
      icon: 'a-icon boschicon-bosch-ic-add',
      height: 'auto',
      type: 'edit',
      passingData: {
        currentCompetency : this.competency.find((e) =>e.id==this.currentCompetencyId)
      }
    });
    dialogRef.afterClosed().subscribe((response) => {
      if(response) {
        this.notificationService.success(this.translate.instant('system.competency_lead.dialog.add_skill.add_success'));
        this.getCompetencyDetail()
      }
    })
  }
  handleTruncateLabel(name:string,numSlice:number) {
    return name?.length > numSlice? name.slice(0,numSlice) + "..." : name
  }
  openSkillInforDialog(skillId: string){
    let loader = this.comLoader.showProgressBar();
    this.competencyLeadService.getSkillDetail(skillId)
      .pipe(finalize(() => { this.comLoader.hideProgressBar(loader) }))
      .subscribe((response: any) => {
        if (response && response.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          const dialogRef = this.dialog.onOpenCommonDialog({
            component: viewSkillDetailDialogComponent.SkillDetailDialogComponent,
            title: this.translate.instant('manage_request.dialog.skill_information'),
            width: '80vw',
            icon: 'a-icon ui-ic-alert-info',
            height: 'auto',
            maxWdith: '800px',
            type: 'view',
            passingData: {
              skillTitle: response.data.skill_name,
              levelList: response.data.skill_levels.sort((a: any, b: any) => a.name > b.name ? 1 : -1)
            },

          });
          dialogRef.afterClosed();
        }
      })
  }
  editSkillForCompetency(skillId: string) {
    const loader = this.comLoader.showProgressBar();
    this.competencyLeadService.getSkillDetail(skillId)
      .pipe(
        switchMap((response: any)=>{
          this.comLoader.hideProgressBar(loader);
          if (response && response.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
            return this.dialog.onOpenCommonDialog({
              component: SkillDetailDialogComponent,
              title: 'system.manage_skill.edit_new_skill',
              icon: 'a-icon boschicon-bosch-ic-edit',
              height: 'auto',
              type: 'edit',
              passingData: { skillDetail: { ...response.data, skill_id: skillId } },
              width: "60vw"
            }).afterClosed();
          }
          return of(undefined);
        }),
        switchMap((response: any)=>{
          const loader = this.comLoader.showProgressBar();
          if (response) {
            this.competencyLead = [];
            return this.competencyLeadService.getSkillGroupDetail(this.currentCompetencyId)
            .pipe(finalize(()=>{
                this.comLoader.hideProgressBar(loader);
                this.notificationService.success(this.translate.instant("system.manage_skill.notification.edit_skill_success"));
              }));
          }
          return of(undefined).pipe(finalize(()=>{
            this.comLoader.hideProgressBar(loader);
          }));;
        }),
      )
      .subscribe((response: any)=>{
        if (response) {
          this.competencyLead = response.data.competency_leads;
          this.originSourceList = (response.data.skills as Array<any>).sort((a, b) => {
            return a.name > b.name ? -1 : a.name < b.name ? 1 : 0;
          })
          this.sourceList = Helpers.cloneDeep(this.originSourceList);
        }
      })
  }
  deleteSkillForCompetency(skill: any) {
    const dialogRef = this.dialog.onOpenConfirm({
      title: '',
      icon: 'a-icon boschicon-bosch-ic-alert-warning',
      content: `${this.translate.instant('system.competency_lead.dialog.remove_skill.confirm_message')} 
        "${skill.name}" ${this.translate.instant('system.competency_lead.dialog.remove_skill.from_system')}`,
      btnConfirm: 'learning.my_learning.delete_member.yes',
      btnCancel: 'learning.my_learning.delete_member.no',
    });
    dialogRef.afterClosed()
    .pipe(switchMap((response)=>{
      if(response){
        const loader = this.comLoader.showProgressBar();
        return this.competencyLeadService.deleteSkill(skill.skill_id).pipe(finalize(()=>{this.comLoader.hideProgressBar(loader)}));
      }
      return of(undefined);
    }))
    .subscribe((response) => {
      if(response) {
        this.notificationService.success(this.translate.instant('system.competency_lead.dialog.remove_skill.success'));
        this.getCompetencyDetail();
      }
    })
  }

  getAllSkillType(){
    const loader = this.comLoader.showProgressBar();
    this.competencyLeadService.getSkillType()
      .pipe(finalize(()=> this.comLoader.hideProgressBar(loader)))
      .subscribe((response)=>{
        if(response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS){
          this.originSkillTypes = response.data;
          this.skillTypes = response.data.map((skillType: SkillTypeModel) => `${skillType.name}`);
          this.skillTypes = this.skillTypes.sort((a, b)=> a < b ? 1 : -1);
          this.selectedSkillType = this.skillTypes[0];
        }
      })
  }

  resetData(){
    this.currentCompetencyId = '';
    this.competencyLead = [];
    this.sourceList = [];
    this.commonList? this.commonList.selectedItem = undefined : '';
  }


  getSkillTypeId(skillType: string){
    return this.originSkillTypes.find(sk => sk.name.toLowerCase() === skillType.toLowerCase())?.id;
  }

  public skillTypeToggleChange(type: string){
    this.selectedSkillType = type;
    switch(type){
      case CONFIG.EXPECTED_SKILL_LEVEL.SKILL_TYPE.BEHAVIORAL_SKILL:
        this.resetData();
        this.filterCompetency = Helpers.cloneDeep(this.competency.filter(com => 
          com?.skill_type?.toLowerCase() === type?.toLowerCase()));
        break;
      case CONFIG.EXPECTED_SKILL_LEVEL.SKILL_TYPE.TECHNICAL_SKILL:
        this.resetData();
        this.filterCompetency = Helpers.cloneDeep(this.competency.filter(com => 
          com?.skill_type?.toLowerCase() === type?.toLowerCase()));
        break;
      default:
        this.filterCompetency = Helpers.cloneDeep(this.competency);
        break;
    }
    this.onSelectFirstCompetency(this.filterCompetency);
  }

}
