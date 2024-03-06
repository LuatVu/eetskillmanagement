import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { Manager, Team } from '../../../../model/line-manager/line-manager.model';
import { debounceTime, finalize, startWith, takeUntil } from 'rxjs/operators';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { LineManagerService } from '../../../../services/line-manager.service';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { NotificationService } from '@bci-web-core/core';
import { TranslateService } from '@ngx-translate/core';
import { ElasticService } from 'projects/eet-core-demo/src/app/shared/services/elastic.service';
import {ReplaySubject} from 'rxjs';
import { isEmptyObject } from '@core/src/lib/authentication';
@Component({
  selector: 'eet-line-manager-dialog',
  templateUrl: './line-manager-dialog.component.html',
  styleUrls: ['./line-manager-dialog.component.scss']
})
export class LineManagerDialogComponent implements OnInit {
  public lineManagerInfoForm!: FormGroup
  public listTeam: Team[] = [];
  public listManager:any[] = []
  public isEditFunc:boolean = false
  public optionList:any[] = []
  public isInputReadOnly = false
  public validManagerName:boolean=false
  public isShowLoading: boolean = false;
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  public currentMemberSearch: Manager
  private isNameManagerSelected:boolean=false
  subscription: any;
  constructor(@Inject(MAT_DIALOG_DATA) public dialogData: any,
    public dialogRef: MatDialogRef<LineManagerDialogComponent>,
    private formBuilder: FormBuilder,
    private loaderService: LoadingService,
    private LineManagerService:LineManagerService,
    private notifyService: NotificationService,
    private translateService:TranslateService,
    private elasticService: ElasticService,
  ) {
    this.onBuildForm()

    this.lineManagerNameFormControl?.valueChanges.pipe(startWith(null), debounceTime(500)).subscribe(res => {
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

   }

  ngOnInit(): void {
    this.listManager = this.dialogData.data.lineManager
    this.dialogData.data.listTeam.forEach((e:Team)=> {
      this.listTeam.push(e)
    })
    if(this.dialogData.data.type==="edit"){
      this.isEditFunc=true
      this.lineManagerNameFormControl.setValue(this.dialogData.data.currentTeamInfo?.line_manager?.displayName)
      this.teamFormControl.setValue(this.dialogData.data.currentTeamInfo?.id)
    }
  }

  onBuildForm() {
    this.lineManagerInfoForm = this.formBuilder.group({
      manager: this.formBuilder.control(null,[Validators.required,this.validateNameManager()]),
      team:this.formBuilder.control(null, [Validators.required]),
    })
  }
  
  selecteOption(value: any) {
    if(value){
      this.isInputReadOnly = true
    }
  }

  removeValueInput() {
    if (this.isInputReadOnly) {
      this.isInputReadOnly = false
      this.lineManagerNameFormControl.setValue('')
    }
  }

  addNewAssociate() {
    const loader = this.loaderService.showProgressBar();
    const team = this.dialogData.data.listTeam.find((e:Team) => e.id===this.teamFormControl.value)
    const requestData = {
      id: team.id,
      name: team.name,
      group: team.group,
      line_manager:this.currentMemberSearch
    };
    this.LineManagerService.updateTeam(requestData.id,requestData).pipe(finalize(() => {
      this.loaderService.hideProgressBar(loader)
    })).subscribe((response: any) => {
      if(response.code===CONFIG.API_RESPONSE_STATUS.SUCCESS){
        this.LineManagerService.doUpdateTeam(true)
        this.dialogRef.close(true)
        this.notifyService.success(this.translateService.instant('system.line_manager.dialog.associate.add_success'))
      }
    })
  }
  findMembers(searchKey: string) {
    this.isShowLoading = true;
    this.elasticService.searchPersonalByNameOrNtid({
      query: searchKey,
      size:1000,
      from:0
    }).pipe(
      takeUntil(this.destroyed$),
      finalize(() => {
        this.isShowLoading = false;
      })
    ).subscribe(data=>{
      this.optionList = data.arrayItems;
    })
  }
  validateNameManager() {
    return (control: AbstractControl): ValidationErrors | null => {
      const res = this.optionList.find((e) => e.displayName.includes(control.value.trim()))
      if(typeof res === 'object' && !isEmptyObject(res) && this.isNameManagerSelected) {
        return null
      }else{
        this.isNameManagerSelected=false
        return {'validNameManager': false}  
      }
    }
    
  }
  adjustLineManager() {
    const loader = this.loaderService.showProgressBar();
    const associate = this.dialogData.data.lineManager.find((e:Manager) => e.id===this.lineManagerNameFormControl.value)
    const team = this.dialogData.data.listTeam.find((e:Team) => e.id===this.teamFormControl.value)
    const requestData:Team = {
      id: team.id,
      name: team.name,
      group: team.group,
      line_manager:this.currentMemberSearch
    };
    this.LineManagerService.updateTeam(requestData.id,requestData).pipe(finalize(() => {
      this.loaderService.hideProgressBar(loader)
    })).subscribe((response: any) => {
      if(response.code===CONFIG.API_RESPONSE_STATUS.SUCCESS){
        this.LineManagerService.doUpdateTeam(true)
        this.dialogRef.close(true)
        this.notifyService.success(this.translateService.instant('system.line_manager.dialog.associate.update_success'))
      }
    })
  }
  onSave() {
    if(this.dialogData.data?.type==='edit'){
      this.adjustLineManager()
    }else{
      this.addNewAssociate()
    }
  }
  selectOption(value: any) {
    if (!value || value.length === 0) {
      return;
    }
    this.currentMemberSearch = {
      id: value.personalId,
      displayName: value.displayName
    }
    this.isNameManagerSelected=true
    this.lineManagerNameFormControl.setValue(value.displayName)
    this.isShowLoading = false;
  }

  displayFn(value: any): string {
    return value && value ? value : '';
  }

  onSelectTeam(team: string) {
    this.teamFormControl.setValue(team)
    if(this.dialogData.data.type==="edit") {
      const thisTeamMamager = this.dialogData.data.listTeam.find((e:Team) => e.id===team).line_manager?.id
      this.lineManagerNameFormControl.setValue(thisTeamMamager)
    }
  }
  onSearchChange() {
    this.isShowLoading = true;
  }
  get lineManagerNameFormControl() {
    return this.lineManagerInfoForm.get('manager') as FormControl;
  }
  get teamFormControl() {
    return this.lineManagerInfoForm.get('team') as FormControl;
  }
}
