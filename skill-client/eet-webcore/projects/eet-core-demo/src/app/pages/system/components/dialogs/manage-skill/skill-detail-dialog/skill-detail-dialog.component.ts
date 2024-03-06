import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { ElasticService } from 'projects/eet-core-demo/src/app/shared/services/elastic.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { Observable, Subscription, finalize, map } from 'rxjs';
import { Helpers } from '../../../../../../shared/utils/helper';
import { DEFAULT_SKILL_DESCRIPTION } from '../../../../model/manage-skill/dumb-data.constants';
import { CompetencyModel, ERROR_WHEN_GET_COMPETENCY_LEAD, SkillDescriptionModel, SkillLevelModel } from '../../../../model/manage-skill/manage-skill.model';
import { ELASTIC_DOCUMENT } from './../../../../../../shared/constants/api.constants';
import { SkillDetailDialogService } from './skill-detail-dialog.service';

interface SkillType {
  name: string;
  skillType: string;
}

@Component({
  selector: 'eet-skill-detail-dialog',
  templateUrl: './skill-detail-dialog.component.html',
  styleUrls: ['./skill-detail-dialog.component.scss']
})
export class SkillDetailDialogComponent implements OnInit {
  public skillForm!: FormGroup
  private selectedCompetencyList: string[] = []
  private isEditing!: boolean;
  private listLevel = Array<string>(5);
  private skillLevel: string[] = [];
  private skillLevelList: Array<SkillLevelModel> = [];

  public skillGroupControl: FormControl = new FormControl();
  private numberOfLevels: number = 5;
  public skillDescriptionList: SkillDescriptionModel[] = [];
  public searchCompetencyControl = new FormControl();
  public competencyOptionsList: string[] = [];
  public competencyListRes: CompetencyModel[] = []; //full list data competency lead
  public filteredCompetencyOptionList: Observable<string[]>;
  public subscription!: Subscription;
  public isCompetencyLeadEmpty = false;
  public isRequired: boolean = false;
  public isMandatory: boolean = false;
  public isFilterTechnical: boolean = false;
  private skillType: SkillType[];

  constructor(
    @Inject(MAT_DIALOG_DATA) private dialogData: any,
    public dialogRef: MatDialogRef<SkillDetailDialogComponent>,
    private formBuilder: FormBuilder,
    private loaderService: LoadingService,
    private skillDetailDialogService: SkillDetailDialogService,
    private elasticService: ElasticService,
    private notify: NotificationService,
    private translateService: TranslateService
  ) {
    this.onBuildForm();

  }

  _filterCompetency(keyword: string) {
    const filterValue = keyword.toLowerCase();
    return this.competencyOptionsList.filter(option => option.toLowerCase().includes(filterValue));
  }

  ngOnInit(): void {
    this.skillDescriptionList = Helpers.cloneDeep(DEFAULT_SKILL_DESCRIPTION);
    if (this.dialogData?.data && this.dialogData?.data?.skillDetail) {
      this._fillValueToForm(this.dialogData?.data?.skillDetail);
    }

    this.getCompetencyLead();
    this.getSkillGroup();

    
    this.skillDetailDialogService.getSkillGroupData()
    .subscribe((response: any) => {
      this.skillType = response.data.map((item: { name: any; skill_type: any; }) => ({
        name: item.name,
        skillType: item.skill_type
      }));
      if(this.dialogData.data?.currentCompetency){
        this.onSkillCompetency(this.dialogData.data.currentCompetency.name)
      }
    })
  }

  onBuildForm() {
    this.skillForm = this.formBuilder.group({
      id: this.formBuilder.control(""),
      skillName: this.formBuilder.control('', [Validators.required]),
      skillCompetency: this.formBuilder.control('', [Validators.required]),
      competency_lead_id: [null, [Validators.required]],
    })
  }


  _fillValueToForm(skill: any) {
    this.skillForm.get('id')?.setValue(skill.skill_id);
    this.skillNameFormControl.setValue(skill.skill_name);
    this.searchCompetencyControl.setValue(skill.skill_group);
    this.skillCompetencyFormControl.setValue(skill.skill_group);
    this.competencyLeadFormControl.setValue((skill.skill_competency_leads as Array<any>).map(element => {
      return {
        displayName: element?.display_name,
        id: element?.personal_id
      }
    }))
    this.skillDescriptionList = (skill?.skill_levels as Array<SkillDescriptionModel>).sort((a: SkillDescriptionModel, b: SkillDescriptionModel) =>
      a.name.toLowerCase().localeCompare(b.name.toLowerCase())).map(element => {
        return {
          ...element,
          id: ""
        }
      }) || [];
    this.isRequired = skill.is_required;
    this.isMandatory = skill.is_mandatory;
    this.isFilterTechnical = skill.skill_type === 'Technical' ? false : true;
  }

  //suggestion list for chip
  public competencyLeaderList: string[] = [];
  private skillGroupList: string[] = [];

  onCompetencyLeadChange(list: Array<any>) {
    if (list.length == 0) {
      this.isCompetencyLeadEmpty = true;
    } else {
      this.isCompetencyLeadEmpty = false;
      this.competencyLeadFormControl.setValue(list);
    }
  }

  onSkillCompetency(list: Array<any>) {
    this.skillType.forEach(item => {
      if (item.name == list.toString()) {
        switch (item.skillType) {
          case 'Technical': 
            this.isFilterTechnical = false;
            break;
          case 'Behavioral': 
            this.isFilterTechnical = true;
            break;
          default:
            break;
        }
      }
    })
    this.skillCompetencyFormControl.setValue(list)
    let competency = this.competencyListRes.filter(item => item.name==this.skillCompetencyFormControl.value)
    if(this.dialogData.data.currentCompetency?.name && list==this.dialogData.data.currentCompetency?.name){
      competency =[this.dialogData.data.currentCompetency] 
    }
    const loader = this.loaderService.showProgressBar();
    this.skillDetailDialogService.getSkillCompetenceLead(competency[0].id)
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe((response: any) => {
        if (response?.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          const listLeadCompetency = response.data as Array<any>;
          this.competencyLeadFormControl.setValue(listLeadCompetency.map(item => {
            item.displayName = item.name;
            return item;
          }))
        } else {
          this.notify.error(this.translateService.instant(ERROR_WHEN_GET_COMPETENCY_LEAD));
        }
      })
  }

  onSave() {
    if (this.competencyLeadFormControl.value == null ||
      this.competencyLeadFormControl.value?.length == 0) {
      this.isCompetencyLeadEmpty = true;
      return;
    }
    if (this.skillForm.invalid) {
      this.skillForm.markAllAsTouched();
      this.skillForm.markAsDirty();
      return;
    }

    if (this.skillForm.get('id')?.value) {
      this.updateSkill();
    } else {
      this.addNewSkill();
    }
  }

  addNewSkill() {
    const loader = this.loaderService.showProgressBar();
    let requestData = {
      id: this.skillForm.get('id')?.value,
      name: this.skillNameFormControl.value,
      skill_group: this.skillCompetencyFormControl.value,
      competency_leads: (this.competencyLeadFormControl.value as Array<any>).map(m => m.id),
      skill_experience_levels: this.skillDescriptionList,
      is_required: this.isRequired,
      is_mandatory: this.isMandatory
    };
    this.skillDetailDialogService.addSkillData(requestData)
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe((response: any) => {
        if (response?.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          requestData.id = response.data;
          this.dialogRef.close(requestData);
        } else {
          this.notify.error(this.translateService.instant('system.manage_skill.notification.add_skill_error'));
        }
      })
  }

  updateSkill() {
    const loader = this.loaderService.showProgressBar();
    let requestData: any = {
      id: this.skillForm.get('id')?.value,
      name: this.skillNameFormControl.value,
      skill_group: this.skillCompetencyFormControl.value,
      competency_leads: (this.competencyLeadFormControl.value as Array<any>).map(m => m.id),
      skill_experience_levels: this.skillDescriptionList,
      is_required: this.isRequired,
      is_mandatory: this.isMandatory
    }
    this.skillDetailDialogService.updateSkillData(requestData)
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe((response: any) => {
        if (response?.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.dialogRef.close({
            result: true,
            requestData: requestData
          });
        } else {
          this.notify.error(this.translateService.instant('system.manage_skill.notification.skill_exist'));
        }
      })
  }

  private searchCompetencySubcription: Subscription;
  public competencyList: Array<any> = [];

  getSkillGroup() {
    const loader = this.loaderService.showProgressBar();
    this.skillDetailDialogService.getSkillGroupData()
      .pipe(
        map(value => {
          if (value.data) {
            //using for get skill group id from name skill group
            this.competencyListRes = value.data.map((skillGroupList: CompetencyModel) => skillGroupList);
            //
            return value.data.map((skillGroupList: { id: string, name: string }) => skillGroupList.name)
          }
        })
      )
      .pipe(finalize(() => {
        this.loaderService.hideProgressBar(loader)
      })).subscribe((response: any) => {
        this.competencyOptionsList = response || [];
      })
  }

  getCompetencyLead() {
    const loader = this.loaderService.showProgressBar();
    this.elasticService.getDocument(ELASTIC_DOCUMENT.PERSONAL, {
      size: 10000,
      query: "",
      from: 0
    }).pipe(finalize(() => {
      this.loaderService.hideProgressBar(loader)
    }))
      .subscribe(response => {
        this.competencyLeaderList = response?.arrayItems || [];
      });
  }

  onCompetencyOptionSelected(event: MatAutocompleteSelectedEvent) {
    this.skillCompetencyFormControl.setValue(event?.option?.value);
  }

  get formControl() {
    return this.skillForm.controls;
  }

  get skillNameFormControl() {
    return this.skillForm.get('skillName') as FormControl;
  }

  get skillCompetencyFormControl() {
    return this.skillForm.get('skillCompetency') as FormControl;
  }

  get competencyLeadFormControl(): FormControl {
    return this.skillForm.get('competency_lead_id') as FormControl;
  }

  get skillExperienceLevelFormControl(): FormControl {
    return this.skillForm.get('skill_experience_level_list') as FormControl;
  }

}
