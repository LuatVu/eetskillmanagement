import { Component, Inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { CommonTaskModel, RoleModel } from 'projects/eet-core-demo/src/app/shared/models/common-model';
import { ElasticService } from 'projects/eet-core-demo/src/app/shared/services/elastic.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { debounceTime, finalize, isEmpty, ReplaySubject, startWith, Subscription, takeUntil } from 'rxjs';
import { MembersInfo } from '../../../models/dialog-data/members-info/members-info.model';
import { ProjectsService } from '../../../services/projects.service';
import { TranslateService } from '@ngx-translate/core';
import { center } from 'svg-pan-zoom';

@Component({
  selector: 'eet-project-member-dialog',
  templateUrl: './project-member-dialog.component.html',
  styleUrls: ['./project-member-dialog.component.scss']
})
export class ProjectMemberDialogComponent implements OnInit {
  public form!: FormGroup;
  public roles: RoleModel[] = [];
  public commonTasks: CommonTaskModel[] = []
  public members: any;
  public userWorkingTimes: any[] = []
  listAdditionMembers: string[] = [];
  listRemoveMembers: string[] = [];
  originalMembers: string[] = [];
  isFormUpdate: boolean = false;

  constructor(@Inject(MAT_DIALOG_DATA) public passingData: any,
    public dialog: MatDialog,
    private projectsService: ProjectsService,
    private elasticService: ElasticService,
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<ProjectMemberDialogComponent>,
    private loader: LoadingService,
    private translate: TranslateService,
    private notificationService: NotificationService,
    private changeDetectorRef: ChangeDetectorRef) {
    if (this.passingData.data.members) {
      this.originalMembers = this.passingData.data.members.map((m: any) => { m.id as string });
    }
    this.form = this.formBuilder.group({
      id: this.formBuilder.control(null, [Validators.required]),
      name: this.formBuilder.control(null, [Validators.required]),
      role: this.formBuilder.control(null, [Validators.required]),
      role_id: this.formBuilder.control(null, [Validators.required]),
      common_task: this.formBuilder.control([]),
      additional_task: this.formBuilder.control(null),
      start_date: this.formBuilder.control(null, [Validators.required]),
      end_date: this.formBuilder.control(null, [this.endDateValidator])
    })
    if (this.passingData.data.type === 'update') {
      this.form = this.formBuilder.group({
        id: this.formBuilder.control({ value: this.passingData.data.memberInfo.id, disabled: true }, [Validators.required]),
        name: this.formBuilder.control({ value: this.passingData.data.memberInfo.name, disabled: true }, [Validators.required]),
        role: this.formBuilder.control(this.passingData.data.memberInfo.role, [Validators.required]),
        role_id: this.formBuilder.control(this.passingData.data.memberInfo.role_id, [Validators.required]),
        common_task: this.formBuilder.control(this.passingData.data.memberInfo.common_task || []),
        additional_task: this.formBuilder.control(this.passingData.data.memberInfo.additional_task || ''),
        start_date: this.formBuilder.control(this.passingData.data.memberInfo.start_date || '', [Validators.required]),
        end_date: this.formBuilder.control(this.passingData.data.memberInfo.end_date || '', [this.endDateValidator])
      })
    }
    this.getprojectRole()
    this.getCommonTasks()
  }

  ngOnInit(): void {
    this.members = this.passingData.data.members
    this.form = this.formBuilder.group({
      id: this.formBuilder.control({ value: this.form.controls.id.value, disabled: this.passingData.data.type === 'update' }, [Validators.required]),
      name: this.formBuilder.control({ value: this.form.controls.name.value, disabled: this.passingData.data.type === 'update' }, [Validators.required]),
      role: this.formBuilder.control(this.form.controls.role.value, [Validators.required]),
      role_id: this.formBuilder.control(this.form.controls.role_id.value, [Validators.required]),
      common_task: this.formBuilder.control(this.form.controls.common_task.value || []),
      additional_task: this.formBuilder.control(this.form.controls.additional_task.value || ''),
      start_date: this.formBuilder.control(this.form.controls.start_date.value ? new Date(this.form.controls.start_date.value) : '', [Validators.required]),
      end_date: this.formBuilder.control(this.form.controls.end_date.value ? new Date(this.form.controls.end_date.value) : '', [this.endDateValidator])
    })
    const initialValue = this.form.value;
    this.form.controls.name.valueChanges.pipe(takeUntil(this.destroyed$), startWith(null), debounceTime(500)).subscribe(res => {
      if (res) {
        if (this.subscription) {
          this.subscription.unsubscribe()
        }
        this.findMembers(res)
      } else {
        this.optionList = []
        this.isShowLoading = false
      }
    })
    this.form.controls.role.valueChanges.subscribe((data) => {
      this.isFormUpdate = (this.form.value['role_id'] != initialValue['role_id'])
        || (this.form.value['additional_task'] != initialValue['additional_task']);
    });
    this.form.controls.additional_task.valueChanges.subscribe((data) => {
      this.isFormUpdate = ((this.form.value['role_id'] != initialValue['role_id'])) || (data != initialValue['additional_task']);
    });

    if (this.passingData.data.type === 'update') {
      this.getUserDay()
    }
  }

  getprojectRole() {
    const comLoader = this.loader.showProgressBar()
    this.projectsService.getProjectRole().pipe(finalize(() => {
      this.loader.hideProgressBar(comLoader)
    })).subscribe((response: any) => {
      this.roles = response as RoleModel[]
      this.roles = this.roles.sort((a: RoleModel, b: RoleModel) => a.name.localeCompare(b.name))
    })
  }

  endDateValidator(control: AbstractControl): { [key: string]: any } | null {
    const startDate = control.parent?.get('start_date')?.value;
    const endDate = control.value;
    if (startDate && endDate && startDate >= endDate) {
      return { 'invalidEndDate': true };
    }
    return null;
  }

  getCommonTasks() {
    const comLoader = this.loader.showProgressBar()
    this.projectsService.getCommonTasks().pipe(finalize(() => {
      this.loader.hideProgressBar(comLoader)
    })).subscribe((response: any) => {
      this.commonTasks = response.data as CommonTaskModel[]
    })
  }

  saveData() {
    // this.passingData.data.members = this.members
    if (this.passingData.data.type === 'update') {
      this.dialogRef.close({
        status: true,
        members: {
          ...this.form.getRawValue(),
          uuid: this.passingData.data.memberInfo.uuid,
          start_date: this.form.getRawValue().start_date ? Helpers.parseDateTimeToString(new Date(this.form.getRawValue().start_date)) : '',
          end_date: this.form.getRawValue().end_date ? Helpers.parseDateTimeToString(new Date(this.form.getRawValue().end_date)) : '',
        }
      })
    }
    else {
      this.dialogRef.close({
        status: true,
        members: this.passingData.data.members
      })
    }
  }

  onRoleChange(event: any) {
    let role = this.roles.filter((val) => {
      return val.id === event.value;
    })[0];

    this.form.controls.role.setValue(role.name)

    this.form.controls.common_task.setValue(this.commonTasks.filter(task => task.project_role_id === role.id)
      .map(item => { return { name: item.name, id: item.id } as CommonTaskModel }));
  }

  onCommonTaskChange(event: any) {
    // Distinct by id
    event = event.filter((obj: any, index: number, self: any) => {
      return index === self.findIndex((o: any) => o.id === obj.id);
    });
    this.form.controls.common_task.setValue(event)

  }
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  private subscription: Subscription;
  public currentMemberSearch: any = null
  public isShowLoading: boolean = false;
  public optionList: any[] = [];
  onSearchChange() {
    this.isShowLoading = true;
  }
  displayFn(value: any): string {
    return value && value ? value : '';
  }

  selectOption(value: any) {
    if (!value || value.length === 0) {
      return;
    }
    this.currentMemberSearch = {
      personal_id: value.personalId,
      display_name: value.displayName
    }
    this.form.controls.id.setValue(value.personalId)
    this.form.controls.name.setValue(value.displayName)
    this.getUserDay()
    this.isShowLoading = false;

    this.form.controls.start_date.setValue('')
    this.form.controls.end_date.setValue('')
  }

  findMembers(searchKey: string) {
    if (!this.form.controls.id.value || this.form.controls.id.value !== null && searchKey !== this.currentMemberSearch.display_name) {
      this.form.controls.id.setValue(null)
    }
    this.isShowLoading = true;
    this.elasticService.searchPersonalByNameOrNtid({
      query: searchKey,
      size: 1000,
      from: 0
    }).pipe(
      takeUntil(this.destroyed$),
      finalize(() => {
        this.isShowLoading = false;
      })
    ).subscribe(data => {
      this.optionList = data.arrayItems
    })

  }

  refresh(addProjectMemberForm: FormGroupDirective) {
    this.addMember()
    this.currentMemberSearch = null
    this.form.reset();
    addProjectMemberForm.resetForm();
    this.ngOnInit()
  }


  addMember() {
    this.form = this.formBuilder.group({
      id: this.formBuilder.control({ value: this.form.controls.id.value, disabled: this.passingData.data.type === 'update' }, [Validators.required]),
      name: this.formBuilder.control({ value: this.form.controls.name.value, disabled: this.passingData.data.type === 'update' }, [Validators.required]),
      role: this.formBuilder.control(this.form.controls.role.value, [Validators.required]),
      role_id: this.formBuilder.control(this.form.controls.role_id.value, [Validators.required]),
      common_task: this.formBuilder.control(this.form.controls.common_task.value || []),
      additional_task: this.formBuilder.control(this.form.controls.additional_task.value || ''),
      start_date: this.formBuilder.control(Helpers.parseDateTimeToString(this.form.controls.start_date.value) || ''),
      end_date: this.formBuilder.control(this.form.controls.end_date.value ? Helpers.parseDateTimeToString(this.form.controls.end_date.value) : '', [this.endDateValidator]),
      isNotFromApi: this.formBuilder.control(true),
      uuid:Helpers.uuidv4()
    })
    var formData = this.form.getRawValue();
    this.passingData.data.members.push(formData);
    const memberId = formData.id as string;
    this.listAdditionMembers.includes(memberId) ? '' : this.listAdditionMembers.push(memberId);

  }

  reloadData(event: any) {
    this.passingData.data.members = event;
    this.listAdditionMembers = this.listAdditionMembers.filter(id =>
      this.passingData.data.members.map((m: any) => m.id as string).includes(id));
    this.listRemoveMembers = this.originalMembers.filter(id =>
      !this.passingData.data.members.map((m: any) => m.id as string).includes(id));

    this.members = Helpers.cloneDeep(event) 
    this.getUserDay()

  }

  isNotEmptyName(input: any) {
    return input.value && input.value.length > 0;
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }
  filterMemberStartDay = (d: Date | null): boolean => {
    const pj_startDate = new Date(this.passingData.data.project_startDay)
    const pj_endDate = this.passingData.data.project_endDay ? new Date(this.passingData.data.project_endDay) : ''
    const day = (d || new Date());
    day.setHours(7)
    if ((day < pj_startDate) || (pj_endDate && day > pj_endDate)) {
      return false
    }
    if(pj_endDate && day.getTime() === pj_endDate.getTime()) return false // start_date member !== end date project

    for (let i = 0; i < this.userWorkingTimes.length; i++) { // check overlap date member
      if (day >= this.userWorkingTimes[i].start_date && day < this.userWorkingTimes[i].end_date) return false
      if(day.getTime() === this.userWorkingTimes[i].start_date.getTime() && !this.userWorkingTimes[i].end_date ) return false
    }
    return true
  };

  filterMemberEndDay = (d: Date | null): boolean => {
    const pj_startDate = new Date(this.passingData.data.project_startDay)
    const pj_endDate = this.passingData.data.project_endDay ? new Date(this.passingData.data.project_endDay) : ''
    const day = (d || new Date());
    day.setHours(7)

    if (!this.form?.getRawValue()?.start_date) return false

    let tmp: Date | null = null
    for (const record of this.userWorkingTimes) {
      if (record.start_date > this.form?.getRawValue()?.start_date) {
        if (tmp === null || record.start_date < tmp) {
          tmp = record.start_date
        }
      }
    }

    if (tmp && day > tmp) {
      return false
    }

    if ((day <= pj_startDate) || (pj_endDate && day > pj_endDate)) {
      return false
    }
    if (day <= this.form.controls.start_date.value) return false

    return true
  }

  getUserDay() {
    const arr: any = []
    this.members.forEach((e: any) => {
      if (e.name === this.form.controls.name.value && (!this.form.controls.start_date.value || new Date(e.start_date).getTime() !== this.form.controls.start_date.value.getTime())) {
        arr.push({ ...e, start_date: new Date(e.start_date), end_date:e.end_date ?  new Date(e.end_date)  : ''})
      }
    })
    this.userWorkingTimes = arr
  }
  checkConditionToDisabledMatDatePicker() {
    if(this.passingData.data.type !== 'update') {
      const isNameIncluded = this.optionList.some(e =>e.displayName===this.form?.controls.name?.value)
      return !isNameIncluded
    }
    return false
  }
  checkConditionToDisabledSaveBtnWithDate() {
    if(this.form.controls.end_date?.value) {
      return this.form.controls.start_date.value >= this.form.controls.end_date.value
    }
    return false
  }
  checkConditionToShowHintInvalidMemberDate() {
    if(this.form.controls.end_date?.value) { // check if end date not null (It is synonymous with the selected person already existing.)
      const startDateMember = this.form.controls.start_date?.value.getTime()
      const endDateMember = this.form.controls.end_date?.value.getTime()
      if(startDateMember >= endDateMember) {
        return this.translate.instant('notification.start_date_member_invalid')
      }
    }else if(this.optionList.some(e =>e.displayName===this.form?.controls.name?.value)){ // check if exist user choosen but end date null
      const isExistMemberHasEndDateNull = this.passingData.data.members.some((e:any) => {
        return this.form?.controls.name?.value === e.name && !e.end_date
      })
      if(isExistMemberHasEndDateNull){
        return this.translate.instant('notification.end_date_cannot_null')
      }
    }
  }
}
