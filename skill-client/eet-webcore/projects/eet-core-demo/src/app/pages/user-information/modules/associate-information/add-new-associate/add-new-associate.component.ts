import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { DatePickerHeader } from 'projects/eet-core-demo/src/app/shared/components/datepicker-header/datepicker-header.component';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { Observable, ReplaySubject, Subscription, forkJoin, startWith, takeUntil } from 'rxjs';
import { debounceTime, finalize } from 'rxjs/operators';
import { AddNewAssociateSharedModel, CommonModel, LdapUserModel } from './add-new-associate.model';
import { AddNewAssociateService } from './add-new-associate.service';
import * as DUMMY_DATA from './dumb-data.constants';
import { SkillGroup } from '../associate-info.model';
import { DATA_CONFIG } from '../associate-info.constant';

@Component({
  selector: 'eet-add-new-associate',
  templateUrl: './add-new-associate.component.html',
  styleUrls: ['./add-new-associate.component.scss']
})

export class AddNewAssociateComponent implements OnInit {
  public readonly DATE_PICKER_HEADER = DatePickerHeader;
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

  constructor(private addNewAssociateService: AddNewAssociateService,
    private loader: LoadingService,
    private fb: FormBuilder,
    private notifyService: NotificationService,
    private dialogRef: MatDialogRef<AddNewAssociateComponent>,
    private translate: TranslateService) {
    this.onBuildForm();
  }
  ngOnInit(): void {
    this.addNewAssociateService.getSharedData()
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
  }

  onBuildForm() {
    this.associateForm = this.fb.group({
      personalDto: this.fb.group({
        id: this.fb.control(""),
        name: this.fb.control(""),
        level_id: this.fb.control(null, [Validators.required]),
        code: this.fb.control(""),
        gender: this.fb.control('', [Validators.required]),
        email: this.fb.control(""),
        department: this.fb.control('', [Validators.required]),
        group: this.fb.control(""),
        team: this.fb.control('', [Validators.required]),
        title: this.fb.control('', [Validators.required]),
        location: this.fb.control('', [Validators.required]),
        joinDate: this.fb.control(null, Validators.compose([Validators.required])),
        manager: this.fb.control(''),
        experienced_non_bosch: this.fb.control(null, [Validators.required, this.experiencedNonBoschValidator()]),
        experienced_non_bosch_type: this.fb.control(1)
      }),
      skill_group_ids: this.fb.control([], [Validators.required])
    });
    this.associateForm.disable();
  }

  get personalDtoFormGroup(): FormGroup {
    return this.associateForm.get('personalDto') as FormGroup;
  }

  fillValueToForm(value: any) {
    this.associateForm.enable();
    this.personalDtoFormGroup.get('name')?.setValue(value?.displayName);
    this.personalDtoFormGroup.get('code')?.setValue(value?.userId);
    this.personalDtoFormGroup.get('email')?.setValue(value?.email);
  }

  getSharedData() {
    const loader = this.loader.showProgressBar();
    const requestList: Array<Observable<any>> = [];
    requestList.push(
      this.addNewAssociateService.getAllLevel(),
      this.addNewAssociateService.getAllGender(),
      this.addNewAssociateService.getAllDepartment(),
      this.addNewAssociateService.getAllGroup(),
      this.addNewAssociateService.getAllTeam(),
      this.addNewAssociateService.getAllTitle(),
      this.addNewAssociateService.getAllLocation(),
      this.addNewAssociateService.getAllLineManager(),
      this.addNewAssociateService.getAllSkillGroup()
    );


    forkJoin(requestList)
      .pipe(takeUntil(this.destroyed$)).pipe(finalize(() => this.loader.hideProgressBar(loader))).subscribe(responses => {
        if (responses) {
          this.addNewAssociateService.setSharedData({
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

  fetchOptionData(type: string, searchKey: string) {
    this.isShowLoading = true;
    this.subscription = this.addNewAssociateService
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

    const loader = this.loader.showProgressBar();
    this.addNewAssociateService.getGroupByTeam(value).pipe(finalize(() => { this.loader.hideProgressBar(loader) })).subscribe((res) => {
      if (res.code === "SUCCESS") {
        this.groupName = res.data?.name
        this.personalDtoFormGroup.get('group')?.setValue(res.data?.id)
      }
    })

    this.personalDtoFormGroup.get('manager')?.reset()
    this.managerDisplayName = ''
    
    const loader1 = this.loader.showProgressBar();
    this.addNewAssociateService.getLineManagerByTeam(value).pipe(finalize(() => { this.loader.hideProgressBar(loader1) })).subscribe((res) => {
      if (res.code === "SUCCESS") {
        this.managerDisplayName = res.data?.name || ''
        this.personalDtoFormGroup.get('manager')?.setValue(res.data?.id)
      }
    })
  }


  selecteOption(value: any) {
    const loader = this.loader.showProgressBar();
    this.addNewAssociateService.checkAssociate(value.userId).pipe(finalize(() => { this.loader.hideProgressBar(loader) })).subscribe((res: any) => {
      if (res != null) {
        if (res?.data == true) {
          this.notifyService.error(this.translate.instant('manage_associate.existed_message'));
          this.searchControl.reset();
        } else {
          this.fillValueToForm(value);
          this.isShowLoading = false;
          this.searchControl.reset();
        }
      } else { this.notifyService.error(res?.message); }
    })
  }

  displayFn(value: any): string {
    return value && value ? value : '';
  }

  onSave() {
    const loader = this.loader.showProgressBar();
    this.addNewAssociateService.addAssociate({
      ...this.associateForm.getRawValue(),
      experienced_non_bosch: this.personalDtoFormGroup.get('experienced_non_bosch')?.value * this.personalDtoFormGroup.get('experienced_non_bosch_type')?.value
    })
      .pipe(finalize(() => this.loader.hideProgressBar(loader))).subscribe((response: any) => {
        if (response && response?.code && response?.code == CONFIG.API_RESPONSE_STATUS.ADD_ASSOCIATE_SUCCESSFUL) {
          this.notifyService.success("Add Associate successfully");
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
