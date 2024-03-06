import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DatePickerHeader } from 'projects/eet-core-demo/src/app/shared/components/datepicker-header/datepicker-header.component';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { Observable, ReplaySubject, Subscription, debounceTime, finalize, forkJoin, startWith, takeUntil } from 'rxjs';
import { AddNewAssociateSharedModel, CommonModel, LdapUserModel } from '../add-new-associate/add-new-associate.model';
import * as DUMMY_DATA from './dumb-data.constants';
import { EditAssociateService } from './edit-associate.service';
import { SkillGroup } from '../associate-info.model';
import { DATA_CONFIG } from '../associate-info.constant';

@Component({
  selector: 'eet-edit-associate',
  templateUrl: './edit-associate.component.html',
  styleUrls: ['./edit-associate.component.scss']
})
export class EditAssociateComponent implements OnInit {
  public readonly DATE_PICKER_HEADER = DatePickerHeader;
  public today: Date = new Date();
  public associateData: any;
  public searchControl: FormControl = new FormControl();
  public isShowLoading: boolean = false;
  public optionList: LdapUserModel[] = [];
  public addNewAssociateSharedModel: Partial<AddNewAssociateSharedModel>;
  public associateForm: FormGroup;
  public nonBoschTypeList: CommonModel[] = DUMMY_DATA.NON_BOSCH_TYPE;
  public groupName: string = "";
  public managerDisplayName: string = "";

  private subscription: Subscription;
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    // private editAssociateInfoService: editAssociateInfoService,
    private editAssociateInfoService: EditAssociateService,
    private loaderService: LoadingService,
    private fb: FormBuilder,
    private notifyService: NotificationService,
    private dialogRef: MatDialogRef<EditAssociateComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,) {
    this.onBuildForm();
  }
  ngOnInit(): void {
    this.associateData = this.data.data.associateInfo;


    this.editAssociateInfoService.getSharedData()
      .pipe(takeUntil(this.destroyed$))
      .subscribe((data: Partial<AddNewAssociateSharedModel>) => {
        if (!data['isStoredState']) {
          this.getSharedData();
        } else {
          data.skillGroupList = this.filterTechnicalSkillGroup(data.skillGroupList)
          this.addNewAssociateSharedModel = data;
        }
      });

    this.searchControl.valueChanges
      .pipe(takeUntil(this.destroyed$)).pipe(startWith(null), debounceTime(500)).subscribe(res => {
        if (res) {
          if (this.subscription) {
            this.subscription.unsubscribe();
          }
          this.fetchOptionData('User', res);
        } else {
          this.optionList = [];
          this.isShowLoading = false;
        }
      });

    this.getAssociateDetail();
  }

  onBuildForm() {
    this.associateForm = this.fb.group({
      personalDto: this.fb.group({
        id: this.fb.control(""),
        name: this.fb.control({ value: null, disabled: true }),
        level_id: this.fb.control(null, [Validators.required]),
        code: this.fb.control({ value: null, disabled: true }),
        gender: this.fb.control('', [Validators.required]),
        email: this.fb.control({ value: null, disabled: true }),
        department: this.fb.control('', [Validators.required]),
        group: this.fb.control(""),
        team: this.fb.control('', [Validators.required]),
        title: this.fb.control('', [Validators.required]),
        location: this.fb.control('', [Validators.required]),
        joinDate: this.fb.control(null, Validators.compose([Validators.required])),
        manager: this.fb.control(''),
        experienced_non_bosch: this.fb.control({ value: null, disabled: false },[Validators.required, this.experiencedNonBoschValidator()]),
        experienced_non_bosch_type: this.fb.control({ value: 12, disabled: false })
      }),
      skill_group_ids: this.fb.control([], [Validators.required])
    });
  }

  get personalDtoFormGroup(): FormGroup {
    return this.associateForm.get('personalDto') as FormGroup;
  }

  fillValueToForm(value: any) {
    let personDto = value.personalDto
    let skillGroupId = value.skill_group_ids

    this.personalDtoFormGroup.get('id')?.setValue(personDto?.id)
    this.personalDtoFormGroup.get('name')?.setValue(personDto?.name);
    this.personalDtoFormGroup.get('code')?.setValue(personDto?.code);
    this.personalDtoFormGroup.get('email')?.setValue(personDto?.email);
    this.groupName = personDto?.group;
    this.personalDtoFormGroup.get('group')?.setValue(personDto?.group_id);
    this.personalDtoFormGroup.get('joinDate')?.setValue(personDto?.joinDate);
    this.personalDtoFormGroup.get('title')?.setValue(personDto?.title);
    this.associateForm.get('skill_group_ids')?.setValue(skillGroupId);
    this.personalDtoFormGroup.get('level_id')?.setValue(personDto?.level_id);
    this.personalDtoFormGroup.get('gender')?.setValue(personDto?.gender_code);
    this.personalDtoFormGroup.get('department')?.setValue(personDto?.department);
    this.personalDtoFormGroup.get('team')?.setValue(personDto?.team_id);
    this.personalDtoFormGroup.get('location')?.setValue(personDto?.location);
    this.managerDisplayName = personDto?.supervisor_name;
    this.personalDtoFormGroup.get('manager')?.setValue(personDto?.manager);
    this.personalDtoFormGroup.get('experienced_non_bosch')?.setValue(personDto?.experienced_non_bosch);
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
            skillGroupList: responses[8]?.data.filter((e:SkillGroup) => e.skill_type === DATA_CONFIG.TECHNICAL_SKILL_TYPE) || [],
          })
        }
      })
  }

  getAssociateDetail() {
    const loader = this.loaderService.showProgressBar();
    this.editAssociateInfoService.getAssociateDetailData(this.associateData.id).pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe(res => {

        this.fillValueToForm(res.data);
      })
  }


  fetchOptionData(type: string, searchKey: string) {
    this.isShowLoading = true;
    this.subscription = this.editAssociateInfoService
      .searchLDAPUsers(searchKey)
      .pipe(takeUntil(this.destroyed$))
      .pipe(
        finalize(() => {
          this.isShowLoading = false;
        })
      )
      .subscribe((res: any) => {
        this.optionList = (res?.data as Array<LdapUserModel>) || [];
        // (res?.data as Array<LdapUserModel>).filter(f => this.addNewAssociateSharedModel.lineManagerList?.findIndex(x => x.displayName == f.displayName) == -1) || [];
      });
  }

  onSearchChange() {
    this.isShowLoading = true;
  }

  onTeamChange(value: string) {
    this.personalDtoFormGroup.get('team')?.setValue(value)

    this.personalDtoFormGroup.get('group')?.reset()
    this.groupName = ''

    const loader = this.loaderService.showProgressBar();
    this.editAssociateInfoService.getGroupByTeam(value).pipe(finalize(() => { this.loaderService.hideProgressBar(loader) })).subscribe((res) => {
      if (res.code === "SUCCESS") {
        this.groupName = res.data?.name
        this.personalDtoFormGroup.get('group')?.setValue(res.data?.id)
      }
    })

    this.personalDtoFormGroup.get('manager')?.reset()
    this.managerDisplayName = ''
    
    const loader1 = this.loaderService.showProgressBar();
    this.editAssociateInfoService.getLineManagerByTeam(value).pipe(finalize(() => { this.loaderService.hideProgressBar(loader1) })).subscribe((res) => {
      if (res.code === "SUCCESS") {
        this.managerDisplayName = res.data?.name || ''
        this.personalDtoFormGroup.get('manager')?.setValue(res.data?.id)
      }
    })
  }

  selecteOption(value: any) {
    if (this.addNewAssociateSharedModel.lineManagerList?.findIndex(x => value?.displayName == x.displayName) != -1) {
      this.notifyService.error("User existed in system");
      this.searchControl.reset();
      return;
    } else {
      this.fillValueToForm(value);
      this.isShowLoading = false;
      this.searchControl.reset();
    }
  }

  displayFn(value: any): string {
    return value && value ? value : '';
  }

  onSave() {
    const loader = this.loaderService.showProgressBar();
    this.editAssociateInfoService.editAssociate(this.personalDtoFormGroup.get('id')?.value, {
      ...this.associateForm.getRawValue(),
      // level_id: this.personalDtoFormGroup.get('level')?.value,
      experienced_non_bosch: this.personalDtoFormGroup.get('experienced_non_bosch')?.value * this.personalDtoFormGroup.get('experienced_non_bosch_type')?.value
    })
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader))).subscribe((response: any) => {
        if (response && response?.code && response?.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.notifyService.success("Edit Associate successfully");
          this.dialogRef.close(true);
        } else {
          this.notifyService.error(response?.message);
        }
      })
  }

  experiencedNonBoschValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control?.value) return null;
      const value = String(control?.value);
      return (value?.includes('e') || value?.includes('.') || (Number(control?.value) < 0)) ? { isNotValid: true } : null;
    };
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }
  filterTechnicalSkillGroup(skillGroupArr:SkillGroup[] = []) {
    skillGroupArr = skillGroupArr.filter((e:SkillGroup) => e.skill_type === DATA_CONFIG.TECHNICAL_SKILL_TYPE)
    return skillGroupArr
  }
}
