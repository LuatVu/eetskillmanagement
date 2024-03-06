import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { DatePickerHeader } from 'projects/eet-core-demo/src/app/shared/components/datepicker-header/datepicker-header.component';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { Observable, ReplaySubject, Subscription, catchError, forkJoin, map, startWith, takeUntil } from 'rxjs';
import { DialogAddDemandService } from './add-new-supply.service'; 
import { debounceTime, finalize } from 'rxjs/operators';
import { AddDemandSharedModel } from './add-new-supply.model'; 
import { AssigneeUsers, Project } from '../supply.model';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DatePipe } from '@angular/common';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { TranslateService } from '@ngx-translate/core';
import { DialogCommonService } from '../../../shared/services/dialog-common.service';
import { BoschProjectDetailComponent } from '../../projects/components/bosch-project-detail/bosch-project-detail.component';
import { ProjectsService } from '../../projects/services/projects.service';
import { Helpers } from '../../../shared/utils/helper';
import { ProjectType } from '../../projects/models/projects.model';
import { PermisisonService } from '../../../shared/services/permisison.service';
@Component({
  selector: 'eet-dialog-add-demand',
  templateUrl: './add-new-supply.component.html',
  styleUrls: ['./add-new-supply.component.scss']
})
export class AddNewSupplyComponent implements OnInit {
  public readonly DATE_PICKER_HEADER = DatePickerHeader;
  public demandForm: FormGroup;
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  public addDemandSharedModel: Partial<AddDemandSharedModel>;
  private datepipe: DatePipe = new DatePipe('en-US');  
  public assigneeUsersMap = new Map<string, Array<AssigneeUsers>>();
  public assigneeUsers: Array<AssigneeUsers> = [];
  public HAS_ADD_NEW_BOSCH_PROJECT: boolean = false;

  constructor(private dialogRef: MatDialogRef<AddNewSupplyComponent>,
    private fb: FormBuilder, 
    private loader: LoadingService,
    private notifyService: NotificationService,
    private translate: TranslateService,
    private dialogAddDemandService: DialogAddDemandService,
    private dialogCommonService: DialogCommonService,
    private projectsService:ProjectsService,
    private permisisonService: PermisisonService) { 
      this.onBuildForm();
      this.HAS_ADD_NEW_BOSCH_PROJECT = this.permisisonService.hasPermission(
        CONFIG.PERMISSIONS.ADD_BOSCH_PROJECT
      );
    }

  ngOnInit(): void {    

    this.dialogAddDemandService.getSharedData()
    .pipe(takeUntil(this.destroyed$))
    .subscribe((data: Partial<AddDemandSharedModel>) => {
      if(!data['isStoredState']){
        this.getShareData();
      }else{
        this.addDemandSharedModel = data;
        this.buildData();
      }
    })

  }

  ngOnDestroy(): void {

  }

  createDemand(){
    if(this.validateDemandForm()){
      const loader = this.loader.showProgressBar();
      this.dialogAddDemandService.createDemand({
        ...this.demandForm.getRawValue()
      })
      .pipe(finalize(() => this.loader.hideProgressBar(loader)))
      .subscribe((response: any) => {
        if (response && response?.code && response?.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.notifyService.success("Add demand successfully");
          this.dialogRef.close(true);
        } else {
          this.notifyService.error(response?.message);
        }
      },
        (error: any)=> {
          this.notifyService.error(error?.error?.message);
        }
      )
    }
  }

  onClose() {
    this.dialogRef.close();
  }

  onBuildForm(){
    this.demandForm = this.fb.group({
        projectId: this.fb.control("",[Validators.required]),
        skillClusterId: this.fb.control("", [Validators.required]),
        skill: this.fb.control("",[Validators.required, Validators.maxLength(120), Validators.pattern(/^(\s+\S+\s*)*(?!\s).*$/)]),
        level: this.fb.control("", [Validators.required]),
        assignee: this.fb.control('', [Validators.required]),
        location: this.fb.control('',[Validators.required]),
        expectedDate: this.fb.control(null, [Validators.required]),
        quantity: this.fb.control(1, [Validators.required]),
        note: this.fb.control("", [Validators.maxLength(120)]),
        assignUserName: this.fb.control(""),
        assignNtId: this.fb.control(""),
        allowExternal: this.fb.control(true)
    });
  }


  buildData(){
    this.demandForm?.get('location')?.setValue(this.addDemandSharedModel?.locationList?this.addDemandSharedModel?.locationList[0].name:"");
    this.demandForm?.get('assignee')?.setValue(this.addDemandSharedModel?.assigneeList?this.addDemandSharedModel?.assigneeList[0].name:"");

    this.addDemandSharedModel?.assigneeList?.forEach(assignee => {
      this.assigneeUsersMap.set(assignee.name, assignee.users);
    });
  }
  

  getShareData() {
    const loader = this.loader.showProgressBar();
    const requestList: Array<Observable<any>> = [];
    requestList.push(
      this.dialogAddDemandService.getAllCreationInfo()      
    );

    forkJoin(requestList)
    .pipe(takeUntil(this.destroyed$))
    .pipe(finalize(() => this.loader.hideProgressBar(loader)))
    .subscribe(responses => {
      if(responses){
        this.dialogAddDemandService.setSharedData({
          isStoredState: true,
          projectList: responses[0]?.data.projects?.sort(this.sortObject) || [], 
          skillClusterList: responses[0]?.data.skillClusters?.sort(this.sortObject) || [],
          levelList: responses[0]?.data.levels?.sort(this.sortObject) || [],
          locationList: responses[0]?.data.locations || [],
          assigneeList: responses[0]?.data.assignees || []
        })
        this.buildData();
      }
    })
  }


  fillNote(event:any){
    this.demandForm?.get('note')?.setValue(event.target.value?.trim());
  }

  fillQuantity(event:any){
    this.demandForm?.get('quantity')?.setValue(Number(event));
  }

  fillSkills(event:any){
    this.demandForm.get('skill')?.setValue(event.target.value?.trim());
  }

  fillExpectedDate(event: MatDatepickerInputEvent<Date>) {
    let formattedDate = this.datepipe.transform(`${event.value}`, 'YYYY-MM-dd')
    this.demandForm.get('expectedDate')?.setValue(formattedDate);
  }

  fillAssigneeUser(event: any){
    this.demandForm?.get('assignNtId')?.setValue(event);
    this.assigneeUsers.forEach(assignee => {
      if(assignee.ntId == event){
        this.demandForm?.get('assignUserName')?.setValue(assignee.name);
      }
    })
  }


  validateDemandForm(): boolean{
    if(this.demandForm.get('projectId')?.value == "" 
        || this.demandForm.get('projectId')?.value == undefined){
      this.notifyService.error(this.translate.instant('add_demand.validate.project_name'));
      return false;
    }
    if(this.demandForm.get('skillClusterId')?.value == "" 
        || this.demandForm.get('skillClusterId')?.value == undefined){
      this.notifyService.error(this.translate.instant('add_demand.validate.skill_cluster'));
      return false;
    }
    if(this.demandForm.get('skill')?.value == "" 
        || this.demandForm.get('skill')?.value == undefined){
      this.notifyService.error(this.translate.instant('add_demand.validate.skills'));
      return false;
    }
    if(this.demandForm.get('level')?.value == "" 
        || this.demandForm.get('level')?.value == undefined){
      this.notifyService.error(this.translate.instant('add_demand.validate.level'));
      return false;
    }    
    if(this.demandForm.get('location')?.value == "" 
        || this.demandForm.get('location')?.value == undefined){
      this.notifyService.error(this.translate.instant('add_demand.validate.location'));
      return false;
    }
    if(this.demandForm.get('expectedDate')?.value == ""  
        || this.demandForm.get('expectedDate')?.value == undefined){
      this.notifyService.error(this.translate.instant('add_demand.validate.expected_date'));
      return false;
    }
    if(this.demandForm.get('quantity')?.value == undefined 
        || this.demandForm.get('quantity')?.value <= 0
        || isNaN(this.demandForm.get('quantity')?.value)){
      this.notifyService.error(this.translate.instant('add_demand.validate.quantity'));
      return false;
    }
    return true;
  }

  isValidQuantity(): boolean{
    if(this.demandForm.get('quantity')?.value == undefined 
      || this.demandForm.get('quantity')?.value <= 0
      || isNaN(this.demandForm.get('quantity')?.value)){
        return false;
    }
    return true;
  }


  sortObject(n1:any, n2:any){
    if(n1.name.toLowerCase() > n2.name.toLowerCase()){
      return 1;
    }
    if(n1.name.toLowerCase() < n2.name.toLowerCase()){
      return -1;
    }
    return 0;
  }

  public isMeetRequired = false;
  onFieldChange(){
    if(this.demandForm.get('projectId')?.value
      &&this.demandForm.get('projectId')?.value != ""

      && this.demandForm.get('expectedDate')?.value
      && this.demandForm.get('expectedDate')?.value != ""
      
      &&  this.demandForm.get('skillClusterId')?.value
      &&  this.demandForm.get('skillClusterId')?.value != ""            

      &&  this.demandForm.get('skill')?.value
      &&  this.demandForm.get('skill')?.value != ""

      &&  this.demandForm.get('level')?.value
      &&  this.demandForm.get('level')?.value != ""
    ){
      this.isMeetRequired = true;
    }else{
      this.isMeetRequired = false;
    }
    
  }

  selectAssignee(event:any){
    this.assigneeUsers = this.assigneeUsersMap.get(event) || [];
    this.demandForm?.get('assignUserName')?.setValue(this.assigneeUsers?this.assigneeUsers[0]?.name:"");
    this.demandForm?.get('assignNtId')?.setValue(this.assigneeUsers?this.assigneeUsers[0]?.ntId:"");
  }

  setAllowExternal(event:any){
    this.demandForm?.get('allowExternal')?.setValue(event);
  }

  getCurrentDay(){
    return this.datepipe.transform(`${new Date()}`, 'YYYY-MM-dd');
  }
  
  handleCreateNewBoschProject() {
  if(!this.HAS_ADD_NEW_BOSCH_PROJECT) return
   this.getIdOfTypeBoschProject().subscribe((listOfTypeProject:ProjectType[]) => {
    if(Array.isArray(listOfTypeProject) && listOfTypeProject.length >0) {
      const dialogRef = this.dialogCommonService.onOpenCommonDialog({
        component: BoschProjectDetailComponent,
        title: this.translate.instant('projects.detail.title_add'),
        icon: 'a-icon boschicon-bosch-ic-add',
        width: '80vw',
        height: 'auto',
        type: 'edit',
        passingData: {
          type: 'add',
          project_type_id: listOfTypeProject.filter((value: any) => {
            return value.name === 'Bosch'
          })[0].id
        }
      })
      dialogRef.afterClosed().subscribe(resp => {
        if (resp) {
          if (this.addDemandSharedModel.projectList === undefined) {
            this.addDemandSharedModel.projectList = [];
          }
          const newProject:Project = {id:resp.data.project_id,name:resp.data.name}
          this.addDemandSharedModel.projectList = [...this.addDemandSharedModel.projectList, newProject];
          this.addDemandSharedModel.projectList.sort((a: Project, b: Project) => a.name.localeCompare(b.name))
          this.demandForm?.get('projectId')?.setValue(resp.data.project_id)
        }
      })
    }
   })

    
  }
  getIdOfTypeBoschProject() {
    return this.projectsService.getFilterList().pipe(
      map(response => {
       return Helpers.cloneDeep(response.data.project_type_filter);
      }),
      catchError(error => {
        console.error("Error when get project type:", error);
        return error; 
      })
    );
  }
}
