import { Component, OnInit, Inject } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DatePickerHeader } from 'projects/eet-core-demo/src/app/shared/components/datepicker-header/datepicker-header.component';
import { LOCATION, ASSIGNEE, SUPPLY_TYPE, SUPPLY_STATUS } from 'projects/eet-core-demo/src/app/pages/user-management/common/constants/constants';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { Observable, ReplaySubject, Subscription, forkJoin, startWith, takeUntil } from 'rxjs';
import { EditDemandService } from './edit-supply.service';
import { debounceTime, finalize } from 'rxjs/operators';
import { AddDemandSharedModel } from './../add-new-supply/add-new-supply.model';
import { SupplyModel } from './../supply.model';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DatePipe } from '@angular/common';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { AssigneeUsers } from './../supply.model';
@Component({
  selector: 'eet-dialog-edit-demand',
  templateUrl: './edit-supply.component.html',
  styleUrls: ['./edit-supply.component.scss']
})
export class EditSupplyComponent implements OnInit {
  public readonly DATE_PICKER_HEADER = DatePickerHeader;
  public demandForm: FormGroup;
  public supplyTypeList: Array<String>;
  public statusList: Array<String>;
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  public addDemandSharedModel: Partial<AddDemandSharedModel>;
  private datepipe: DatePipe = new DatePipe('en-US');
  editData: SupplyModel;
  isProcessEdit: boolean = false;
  public assigneeUsersMap = new Map<string, Array<AssigneeUsers>>();
  public assigneeUsers: Array<AssigneeUsers>=[];
  public isStatusFilled: boolean = false;
  constructor(private dialogRef: MatDialogRef<EditSupplyComponent>,
    private fb: FormBuilder,
    private loader: LoadingService,
    private notifyService: NotificationService,
    private editDemandService: EditDemandService,
    @Inject(MAT_DIALOG_DATA) public data: any) {
    this.onBuildForm();
  }

  ngOnInit(): void {    
    this.supplyTypeList = [SUPPLY_TYPE.INTERNAL, SUPPLY_TYPE.EXTERNAL, SUPPLY_TYPE.RECRUITMENT];
    this.editData = this.data.data.editData;
    this.buildStatusList();
    this.buildEditData();
    this.getSharedData();
  }

  getSharedData() {
    this.editDemandService.getSharedData()
      .pipe(takeUntil(this.destroyed$))
      .subscribe((data: Partial<AddDemandSharedModel>) => {
        if (!data['isStoredState']) {
          this.getShareData();
        } else {
          this.addDemandSharedModel = data;
          this.builAssignUsersName();
        }
      })
  }

  builAssignUsersName(){
    this.addDemandSharedModel?.assigneeList?.forEach(assignee => {
      this.assigneeUsersMap.set(assignee.name, assignee.users);
    });
    this.assigneeUsers = this.assigneeUsersMap.get(this.editData.assignee) || [];
  }

  buildEditData() {
    this.demandForm.get('id')?.setValue(this.editData.id);
    this.demandForm.get('projectId')?.setValue(this.editData.projectId);
    this.demandForm.get('skillClusterId')?.setValue(this.editData.skillClusterId);
    this.demandForm.get('skill')?.setValue(this.editData.skill);
    this.demandForm.get('level')?.setValue(this.editData.level);
    this.demandForm.get('assignee')?.setValue(this.editData.assignee);
    this.demandForm?.get('candidateName')?.setValue(this.editData.candidateName);
    this.demandForm?.get('supplyType')?.setValue(this.editData.supplyType);
    this.demandForm?.get('location')?.setValue(this.editData.location);
    this.demandForm?.get('status')?.setValue(this.editData.status);
    this.demandForm?.get('note')?.setValue(this.editData.note);
    this.demandForm?.get('allowExternal')?.setValue(this.editData.allowExternal);
    this.demandForm?.get('assignUserName')?.setValue(this.editData.assignUserName);

    let expectedDate_formattedDate = this.datepipe.transform(`${this.editData.expectedDate || ''}`, 'YYYY-MM-dd');
    this.demandForm.get('expectedDate')?.setValue(expectedDate_formattedDate);

    let forecastDate_formattedDate = this.datepipe.transform(`${this.editData.forecastDate || ''}`, 'YYYY-MM-dd');
    this.demandForm.get('forecastDate')?.setValue(forecastDate_formattedDate);

    let filledDate_formattedDate = this.datepipe.transform(`${this.editData.filledDate || ''}`, 'YYYY-MM-dd');
    this.demandForm.get('filledDate')?.setValue(filledDate_formattedDate);

    this.demandForm.get('assignNtId')?.setValue(this.editData.assignNtId);
  }

  onBuildForm() {
    this.demandForm = this.fb.group({
      projectId: this.fb.control('', [Validators.required, Validators.pattern(/^[A-Za-z0-9]/)]),
      skillClusterId: this.fb.control('', [Validators.required]),
      skill: this.fb.control('', [Validators.required, Validators.maxLength(120), Validators.pattern(/^(\s+\S+\s*)*(?!\s).*$/)]),
      level: this.fb.control('', [Validators.required]),
      assignee: this.fb.control(''),
      assignUserName: this.fb.control(''),
      candidateName: this.fb.control('', [Validators.maxLength(120)]),
      supplyType: this.fb.control('', []),
      location: this.fb.control('', [Validators.required]),
      expectedDate: this.fb.control('', Validators.compose([Validators.required])),
      status: this.fb.control('', [Validators.required]),
      note: this.fb.control('', [Validators.maxLength(120)]),
      forecastDate: this.fb.control(''),
      filledDate: this.fb.control(''),
      allowExternal: this.fb.control(false),
      assignNtId: this.fb.control('')
    });
  }

   buildStatusList() {
    if (this.editData.status && this.editData.status !== SUPPLY_STATUS.DRAFT) {
      //Not edit status to Draft
      this.statusList = [SUPPLY_STATUS.OPEN, SUPPLY_STATUS.ON_GOING, SUPPLY_STATUS.ON_HOLD, SUPPLY_STATUS.FILLED, SUPPLY_STATUS.CANCEL];
    } else {
      this.statusList = [SUPPLY_STATUS.DRAFT, SUPPLY_STATUS.OPEN, SUPPLY_STATUS.ON_GOING, SUPPLY_STATUS.ON_HOLD, SUPPLY_STATUS.FILLED, SUPPLY_STATUS.CANCEL];
    }
  }


  editDemand() {
    const loader = this.loader.showProgressBar();
    this.editDemandService.edit(this.buildDataSubmit())
      .pipe(finalize(() => {this.loader.hideProgressBar(loader)})).subscribe((response: any) => {
        if (response && response?.code && response?.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.dialogRef.close(true);
        } else {
          this.notifyService.error(response?.message);
        }
      },
        (error: any) => {
          this.notifyService.error(error?.error?.message);
        }
      )
  }

  buildDataSubmit() {
    let formData = this.demandForm.getRawValue();
    let newDemand = Object.assign({}, this.editData);
    newDemand.projectId = formData.projectId;
    this.setProjectName(formData.projectId, newDemand);
    newDemand.skillClusterId = formData.skillClusterId;
    this.setSkillClusterName(formData.skillClusterId, newDemand);
    newDemand.skill = formData.skill;
    newDemand.level = formData.level;
    newDemand.assignee = formData.assignee;
    newDemand.candidateName = formData.candidateName;
    newDemand.supplyType = formData.supplyType;
    newDemand.location = formData.location;
    newDemand.expectedDate = formData.expectedDate;
    newDemand.forecastDate = formData.forecastDate;
    newDemand.filledDate = formData.filledDate;
    newDemand.status = formData.status;
    newDemand.note = formData.note;
    newDemand.allowExternal = formData.allowExternal;
    newDemand.assignUserName = formData.assignUserName;
    newDemand.assignNtId = formData.assignNtId;
    return newDemand;
  }

  setProjectName(projectId: string, newDemand: SupplyModel) {
    if (this.addDemandSharedModel.projectList?.length) {
      for (let project of this.addDemandSharedModel.projectList) {
        if (project.id === projectId) {
          newDemand.projectName = project.name
        }
      }
    }
  }

  setSkillClusterName(skillClusterId: string, newDemand: SupplyModel) {
    if (this.addDemandSharedModel.skillClusterList?.length) {
      for (let skillCluster of this.addDemandSharedModel.skillClusterList) {
        if (skillCluster.id === skillClusterId) {
          newDemand.skillClusterName = skillCluster.name
        }
      }
    }
  }

  onClose() {
    this.dialogRef.close();
  }

  getShareData() {
    const loader = this.loader.showProgressBar();
    const requestList: Array<Observable<any>> = [];
    requestList.push(
      this.editDemandService.getAllCreationInfo()
    );

    forkJoin(requestList)
      .pipe(takeUntil(this.destroyed$))
      .pipe(finalize(() => this.loader.hideProgressBar(loader)))
      .subscribe(responses => {
        if (responses) {
          this.editDemandService.setSharedData({
            isStoredState: true,
            projectList: responses[0]?.data.projects?.sort(this.sortObject) || [],
            skillClusterList: responses[0]?.data.skillClusters?.sort(this.sortObject) || [],
            levelList: responses[0]?.data.levels?.sort(this.sortObject) || [],
            locationList: responses[0]?.data.locations || [],
            assigneeList: responses[0]?.data.assignees || []
          })
          this.builAssignUsersName();
        }
      })
  }

  sortObject(n1: any, n2: any) {
    if (n1.name.toLowerCase() > n2.name.toLowerCase()) {
      return 1;
    }
    if (n1.name.toLowerCase() < n2.name.toLowerCase()) {
      return -1;
    }
    return 0;
  }

  fillExpectedDate(event: MatDatepickerInputEvent<Date>) {
    let formattedDate = this.datepipe.transform(`${event.value}`, 'YYYY-MM-dd')
    this.demandForm.get('expectedDate')?.setValue(formattedDate);
  }

  fillForecastDate(event: MatDatepickerInputEvent<Date>){
    let formattedDate = this.datepipe.transform(`${event.value}`, 'YYYY-MM-dd')
    this.demandForm.get('forecastDate')?.setValue(formattedDate);
  }
  
  fillFilledDate(event: MatDatepickerInputEvent<Date>){
    let formattedDate = this.datepipe.transform(`${event.value}`, 'YYYY-MM-dd')
    this.demandForm.get('filledDate')?.setValue(formattedDate);
  }

  selectAssignee(event:any){
    this.assigneeUsers = this.assigneeUsersMap.get(event) || [];
    this.demandForm?.get('assignUserName')?.setValue(this.assigneeUsers?this.assigneeUsers[0]?.name:"");
    this.demandForm.get('assignNtId')?.setValue(this.assigneeUsers?this.assigneeUsers[0]?.ntId:"");
  }

  setAllowExternal(event:any){
    this.demandForm?.get('allowExternal')?.setValue(event);
  }

  setAssignUserName(event:any){
    let assignUserName = "";
    
    for(var i = 0; i< this.assigneeUsers.length; i++){
      if(this.assigneeUsers[i].ntId == event){
        assignUserName = this.assigneeUsers[i].name;
        break;
      }
    }

    this.demandForm?.get('assignUserName')?.setValue(assignUserName);
  }

  setStatus(event: any){
    if(event == SUPPLY_STATUS.FILLED){
      this.isStatusFilled = true;
      let formattedDate = this.datepipe.transform(new Date(), 'YYYY-MM-dd');
      this.demandForm.get('filledDate')?.setValue(formattedDate);
    } else {
      this.isStatusFilled = false;
    }
  }

  invalidCandidate():boolean{
    if(this.demandForm?.get('candidateName') == undefined
        || this.demandForm?.get('candidateName')?.value == undefined 
        || this.demandForm?.get('candidateName')?.value?.trim() == ''){
      return true;
    }
    return false;
  }

  invalidSupplyType(): boolean{
    if(this.demandForm?.get('supplyType') == undefined
        || this.demandForm?.get('supplyType')?.value == undefined 
        || this.demandForm?.get('supplyType')?.value.trim() == ''){
          return true;
    }
    return false;
  }
  
  getCreatedDate(){
    if(this.editData){
      return this.datepipe.transform(`${this.editData.createdDate}`, 'YYYY-MM-dd');
    }
    return this.datepipe.transform(`${new Date()}`, 'YYYY-MM-dd');
  }
  checkAllowExternal():boolean {
    const isAllowExternal = this.demandForm?.get('allowExternal')?.value
    const supplyType=this.demandForm?.get('supplyType')?.value
    if(!isAllowExternal && supplyType !==SUPPLY_TYPE.INTERNAL){
      return true
    }
    return false
  }
}
