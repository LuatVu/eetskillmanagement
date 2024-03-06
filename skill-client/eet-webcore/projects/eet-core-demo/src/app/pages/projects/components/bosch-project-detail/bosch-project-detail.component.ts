import { takeUntil, debounceTime } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, ElementRef, Inject, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { MatAutocomplete, MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { NotificationService } from '@bci-web-core/core';
import { TranslateService } from '@ngx-translate/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { ProjectPhaseModel } from 'projects/eet-core-demo/src/app/shared/models/common-model';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { PermisisonService } from 'projects/eet-core-demo/src/app/shared/services/permisison.service';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { Observable, catchError, finalize, map, startWith, Subscription, of, throwError } from 'rxjs';
import { ProjectScope, managerDTO, managerModel } from '../../models/projects.model';
import { ProjectsService } from '../../services/projects.service';
import { ProjectMemberDialogComponent } from './project-member-dialog/project-member-dialog.component';
import { ReplaySubject } from 'rxjs/internal/ReplaySubject';
import { LdapUserModel } from 'projects/eet-core-demo/src/app/shared/models/group.model';
import { BoschProjectDetailService } from './bosch-project-detail.service';
import { MembersInfo } from '../../models/dialog-data/members-info/members-info.model';
import { BaseResponseModel } from 'projects/eet-core-demo/src/app/shared/models/base.model';
import { STATUS } from 'angular-in-memory-web-api';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';

interface memberDate {
  startDate: Date;
  endDate: Date;
}

@Component({
  selector: 'eet-bosch-project-detail',
  templateUrl: './bosch-project-detail.component.html',
  styleUrls: ['./bosch-project-detail.component.scss']
})
export class BoschProjectDetailComponent implements OnInit {
  @ViewChild('skillTagInput', { read: MatAutocompleteTrigger }) skillTagInput: MatAutocompleteTrigger;
  @ViewChild('projectPhaseInput', { read: MatAutocompleteTrigger }) projectPhaseInput: MatAutocompleteTrigger;
  @ViewChild('pmNameInput', { read: MatAutocompleteTrigger }) pmNameInput : MatAutocompleteTrigger; 
  @ViewChild('projectPhaseInput') projectPhaseNativeTagInput: ElementRef;
  @ViewChild('skillTagInput') skillTagNativeTagInput: ElementRef;
  skillTagClickOut : boolean = true;
  projectPhaseClickOut : boolean = true;
  public originProjectPhases: ProjectPhaseModel[];
  public filteredProjectPhases: ProjectPhaseModel[];
  public projectPhaseAsText: string;
  public projectPhaseSearchControl = new FormControl([Validators.required]);
  public projectStatusList = CONFIG.PROJECT_STATUS_LIST;
  public allowCreateSkillTag = true;
  public skillTagAsText : string;
  public filteredSkillTagList : any;
  public originalSkillTagList : any;
  public skillTagSearchControl = new FormControl([Validators.required]);
  private isChecked: boolean = false;
  public form!: FormGroup;
  public HAS_ASSIGN_ASSOCIATE_TO_PROJECT: boolean = false;
  public inValidFieldProjectPhases:boolean = false
  public inValidFieldTechnologyUsed:boolean = false
  public TECHNOLOGY_USED='technology_used'
  public PROJECT_PHASES='project_phases'
  private subscription: Subscription;
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  private isShowLoading : boolean = false;
  private ldapManagerList : managerModel[] = [];
  private managerSearchControl : FormControl = new FormControl();
  public listCustomerGb: any = [];
  public maxMemberCount: number = 0;
  public scopeName:string=''
  public projectMemberDialogId: string = '';
  public readonly CURRENT_DATE = new Date()
  private highlight:any
  constructor(
    @Inject(MAT_DIALOG_DATA) public passingData: any,
    public dialogRef: MatDialogRef<BoschProjectDetailComponent>,
    public dialogCommonService: DialogCommonService,
    public projectsService: ProjectsService,
    public comLoader: LoadingService,
    private formBuilder: FormBuilder,
    private translateServce : TranslateService,
    private permisisonService: PermisisonService,
    private loadService : LoadingService,
    private notification: NotificationService,
    private changeDetectorRef: ChangeDetectorRef,
    private service: BoschProjectDetailService
    ) {
      this.service.closeEvent.subscribe(() => {
        this.dialogRef.close();
      })

      this.projectPhaseSearchControl.valueChanges.subscribe((keyword : string)=>{
        this.filteredSelectedProjectPhase();
        if(typeof(keyword) !== 'string' || keyword.trim().length === 0) return;
        this.filteredProjectPhases = this.filteredProjectPhases.filter((pjPhase : any) =>
          pjPhase.name.trim().toLowerCase().includes(keyword.trim().toLowerCase())
        );
      });
      this.skillTagSearchControl.valueChanges.subscribe(() =>{
        if(typeof(this.skillTagSearchControl.value) === 'string'){
          this.allowCreateSkillTag = this.isAllowAddNewSkillTag(this.skillTagSearchControl.value);
          this.filteredSelectedSkillTag();
          this.filteredSkillTagList = this.filteredSkillTagList.filter((tech : any) => 
          tech.name.toLowerCase().includes(this.skillTagSearchControl.value.toLowerCase()));
        }
      })
      this.HAS_ASSIGN_ASSOCIATE_TO_PROJECT = this.permisisonService.hasPermission(CONFIG.PERMISSIONS.ASSIGN_ASSOCIATE_TO_PROJECT);
      this.form = this.formBuilder.group({
        name: this.formBuilder.control(null, [Validators.required, this.whiteSpaceValidator]),
        start_date: this.formBuilder.control(null, [Validators.required]),
        end_date: this.formBuilder.control(null, [this.endDateValidator]),
        pm_name: this.formBuilder.control(null, [Validators.required, this.checkIsNotSearchKey()]),
        challenge: this.formBuilder.control(null),
        status: this.formBuilder.control(null, [Validators.required]),
        project_type: this.formBuilder.control({value: 'Bosch', disabled: true}, [Validators.required]),
        project_type_id: this.formBuilder.control({value: (passingData.data.project_type_id || null), disabled: true}, [Validators.required]),
        objective: this.formBuilder.control(null, [Validators.required, this.whiteSpaceValidator]),
        gb_unit: this.formBuilder.control(null, [Validators.required]),
        team_size: this.formBuilder.control(null, [Validators.required, Validators.pattern('^[1-9][\\d]{0,1}$')]),
        reference_link: this.formBuilder.control(null),
        top_project: this.formBuilder.control(false),
        description: this.formBuilder.control(null,[Validators.required, this.whiteSpaceValidator]),
        members: this.formBuilder.control([],[Validators.required]),
        stakeholder: this.formBuilder.control(null,[Validators.required, this.whiteSpaceValidator]),
        project_phases: this.formBuilder.control([],[Validators.required]),
        skill_tags: this.formBuilder.control([], [Validators.required]),
        customer_gb: this.formBuilder.control(null,[Validators.required, this.whiteSpaceValidator]),
        project_scope_id:this.formBuilder.control(null,[Validators.required]),
      })
      if (this.passingData.data.type !== 'add') {
        const loader = this.comLoader.showProgressBar()
        this.projectsService.getProjectDetail(this.passingData.data.project_id).pipe(
          finalize(() => {
            this.comLoader.hideProgressBar(loader)
          })
        ).subscribe((response: any) => {
          response.data.members = response.data.members?.map((e:MembersInfo) => {
            return {...e,uuid:Helpers.uuidv4()}
          })
          this.form = this.formBuilder.group({
            id: this.formBuilder.control(response.data.id || this.passingData.data.project_id, [Validators.required]),
            name: this.formBuilder.control(this.replaceLeftToRightMark(response.data.name), [Validators.required, this.whiteSpaceValidator]),
            start_date: this.formBuilder.control(response.data.start_date, [Validators.required]),
            end_date: this.formBuilder.control(response.data.end_date, [this.endDateValidator]),
            pm_name: this.formBuilder.control(response.data.pm_name, [Validators.required, this.checkIsNotSearchKey()]),
            challenge: this.formBuilder.control(this.replaceLeftToRightMark(response.data.challenge)),
            status: this.formBuilder.control(response.data.status, [Validators.required]),
            project_type: this.formBuilder.control({value: response.data.project_type, disabled: true}, [Validators.required]),
            project_type_id: this.formBuilder.control({value: response.data.project_type_id, disabled: true}, [Validators.required]),
            objective: this.formBuilder.control(this.replaceLeftToRightMark(response.data.objective), [Validators.required, this.whiteSpaceValidator]),
            gb_unit: this.formBuilder.control(response.data.gb_unit, [Validators.required]),
            team_size: this.formBuilder.control(response.data.team_size, [Validators.required, Validators.pattern('^[1-9][\\d]{0,1}$')]),
            reference_link: this.formBuilder.control(this.replaceLeftToRightMark(response.data.reference_link)),
            top_project: this.formBuilder.control(response.data.top_project),
            description: this.formBuilder.control({value: this.replaceLeftToRightMark(response.data.description)  || null, disabled: (this.passingData.data.type === 'view')? true : null},[Validators.required, this.whiteSpaceValidator]),
            members: this.formBuilder.control(response.data.members || [],[Validators.required]),
            stakeholder: this.formBuilder.control(this.replaceLeftToRightMark(response.data.stakeholder) || null,[Validators.required, this.whiteSpaceValidator]),
            project_phases: this.formBuilder.control(response.data.project_phases || [],[Validators.required]),
            skill_tags: this.formBuilder.control(response.data.skill_tags || [], [Validators.required]),
            customer_gb: this.formBuilder.control(this.replaceLeftToRightMark(response.data.customer_gb),[Validators.required, this.whiteSpaceValidator]),
            project_scope_id:this.formBuilder.control(response.data?.project_scope_id,[Validators.required]),
          })
          this.projectPhaseAsText = this.form.controls.project_phases.value.map((pjPhase: any)=> pjPhase.description).join(', ');
          this.skillTagAsText = this.form.controls.skill_tags.value.map((skTag : any) => skTag.name).join(', ');
          this.scopeName = response.data.project_scope_name
          if (this.passingData.data.type === 'edit') {
            this.form.markAllAsTouched();
            const standarPmName = this.replaceLeftToRightMark(response.data.pm_name);
            if(standarPmName && standarPmName.length > 0)
            {
              this.form.controls.pm_name.setValue({name : standarPmName});
              this.onSearchManagerChange(response.data.pm_name);
            }
            this.inValidFieldTechnologyUsed= this.form.controls.skill_tags.value.length > 0? false: true;
            this.inValidFieldProjectPhases= this.form.controls.project_phases.value.length > 0? false: true;

            this.getAllData();
            this.form.get('pm_name')?.valueChanges.subscribe(()=>{
              this.onSearchManagerChange(this.form.controls.pm_name.value);
            });
          }
          this.calculateMaxMemberCount();
        })
      }
      else {
        this.getAllData()
      }
}

  public gbUnitList = CONFIG.GB_UNIT;
  public projectScopeList:ProjectScope[] = []
  public filteredManagerList: Observable<managerDTO[]>;
  public managerList: managerDTO[] = [];
  public postProjectRequestData: any;
  ngOnInit(): void {
    window.addEventListener('scroll', this.scrollEvent, true);
    this.form = this.formBuilder.group({
      name: this.formBuilder.control(this.form.controls.name.value, [Validators.required, this.whiteSpaceValidator]),
      start_date: this.formBuilder.control(this.form.controls.start_date.value, [Validators.required]),
      end_date: this.formBuilder.control(this.form.controls.end_date.value, [this.endDateValidator]),
      pm_name: this.formBuilder.control(this.form.controls.pm_name.value, [Validators.required, this.checkIsNotSearchKey()]),
      challenge: this.formBuilder.control(this.form.controls.challenge.value),
      status: this.formBuilder.control(this.form.controls.status.value, [Validators.required]),
      project_type: this.formBuilder.control({value: this.form.controls.project_type.value, disabled: true}, [Validators.required]),
      project_type_id: this.formBuilder.control({value: this.form.controls.project_type_id.value, disabled: true}, [Validators.required]),
      objective: this.formBuilder.control(this.form.controls.objective.value, [Validators.required, this.whiteSpaceValidator]),
      gb_unit: this.formBuilder.control(this.form.controls.gb_unit.value, [Validators.required]),
      team_size: this.formBuilder.control(this.form.controls.team_size.value, [Validators.required, Validators.pattern('^[1-9][\\d]{0,1}$')]),
      reference_link: this.formBuilder.control(this.form.controls.reference_link.value),
      top_project: this.formBuilder.control({value:this.form.controls.top_project.value, disabled: (this.passingData.data.type === 'view')? true : null}),
      description: this.formBuilder.control({value: this.form.controls.description.value  || null, disabled: (this.passingData.data.type === 'view')? true : false},[Validators.required, this.whiteSpaceValidator]),
      members: this.formBuilder.control(this.form.controls.members.value || [],[Validators.required]),
      stakeholder: this.formBuilder.control(this.form.controls.stakeholder.value,[Validators.required, this.whiteSpaceValidator]),
      project_phases: this.formBuilder.control(this.form.controls.project_phases.value || [],[Validators.required]),    
      skill_tags: this.formBuilder.control(this.form.controls.skill_tags.value || [], [Validators.required]),
      customer_gb: this.formBuilder.control(this.form.controls.customer_gb.value,[Validators.required, this.whiteSpaceValidator])    ,
      project_scope_id:this.formBuilder.control(this.form.controls.project_scope_id.value,[Validators.required]),
    })
      this.setFilter()
      this.form.get('pm_name')?.valueChanges.subscribe(()=>{
        this.onSearchManagerChange(this.form.controls.pm_name.value);
      });
    this.getListCustomerGb();
    this.getAllProjectScopes()


    this.CURRENT_DATE.setHours(7,0,0,0)

    if(this.passingData.data.type === 'edit'){
      this.getHighlightProject()
    }
    
    
  }
  selectedProjectPhase(e: any){
    if(!this.isProjectPhaseSelected(e.option.value.id)){
      this.form.controls.project_phases.value.push(e.option.value);
      this.form.controls.project_phases.setValue(Helpers.cloneDeep(this.form.controls.project_phases.value));
      this.filteredSelectedProjectPhase();
      this.projectPhaseNativeTagInput.nativeElement.value='';
      this.projectPhaseNativeTagInput.nativeElement.focus();
      this.form.controls.project_phases.value?.length !== 0 ? this.inValidFieldProjectPhases = false : this.inValidFieldProjectPhases = true
    }  
  }
  isProjectPhaseSelected(id : string){
    let isExist = [];
    isExist = this.form.controls.project_phases.value
      .filter((pjPhase : any) => pjPhase.id === id);
    return isExist.length > 0;
  }
  removeProjectPhase(id : string){
    this.form.controls.project_phases.setValue(this.form.controls.project_phases.value
      .filter((pjPhase : any) => pjPhase.id !== id));
    this.filteredSelectedProjectPhase();
    this.form.controls.project_phases.value?.length !== 0 ? this.inValidFieldProjectPhases = false : this.inValidFieldProjectPhases = true
  }
  getAllData() {
    //this.getGBUnit()
    //this.getManagerList()
    this.getProjectPhase();
    this.getAllSkillTags();
  }

  checkIsNotSearchKey() {
    return (control: AbstractControl): ValidationErrors | null => {
      return typeof control.value === 'object' ? null : {'isSearchedObject': false}  
    }
  }
  checkIsValidObject() {
    return (control: AbstractControl): ValidationErrors | null => {
      return  control.value?.name  ? null : {'isValidObject': false}  
    }
  }
  getGBUnit() {
    const loader = this.comLoader.showProgressBar()
    this.projectsService.getGBUnit().pipe(finalize(() => {
      this.comLoader.hideProgressBar(loader)
    })).subscribe((resp) => {
    })
  }

  getManagerList() {
    const loader = this.comLoader.showProgressBar()
    this.projectsService.getManager().pipe(finalize(() => {
      this.comLoader.hideProgressBar(loader)
    })).subscribe((response: any) => {
      this.managerList = response.data
      if (this.passingData.data.type === 'edit') {
        this.form.controls.pm_name.setValue((this.managerList.filter((data) => {
          return data.name === this.form.controls.pm_name.value
        })[0] || this.form.controls.pm_name.value));
        this.setFilter()
      }
    })
  }
  getProjectPhase() {
    const loader = this.loadService.showProgressBar();
    this.projectsService.getProjectPhase()
      .pipe(finalize(()=>{this.loadService.hideProgressBar(loader)}))
      .subscribe(response => {
        if(response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS){
          this.originProjectPhases = response.data || [];
          this.filteredProjectPhases = Helpers.cloneDeep(this.originProjectPhases);
          this.filteredSelectedProjectPhase();
        }
    })
  }
  setFilter() {
    this.filteredManagerList = this.form.controls.pm_name.valueChanges.pipe(startWith(this.form.controls.pm_name.value || ''),
    map(value => typeof value === 'string' ? value : ''),
    map((name: any) => name ? this.filterManager(name) : this.managerList.slice())
    )
  }

  onSelect(value: any) {
    this.form.controls.pm_name.setValue(value.option.value)
  }

  filterManager(name: string): managerDTO[] {
    const filterValue = name.toLowerCase().trim();
    return this.managerList.filter(option => option.name.toLowerCase().trim().includes(filterValue));
  }

  displayFn(manager?: managerDTO) {
    return manager && manager.name ? manager.name : ''
  }

  onChangeMember(event: any) {
    this.form.controls.members.setValue(event)
    this.calculateMaxMemberCount();
  }

  addProjectMembers() {
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: ProjectMemberDialogComponent,
      title: 'projects.detail.Member_Info.Member_Add.title',
      icon: 'a-icon a-button__icon boschicon-bosch-ic-add',
      width: '80vw',
      height: 'auto',
      type: 'edit',
      passingData: {
        type: 'add',
        members: Helpers.cloneDeep(this.form.controls.members.value),
        project_startDay:this.form?.getRawValue()?.start_date,
        project_endDay:this.form?.getRawValue()?.end_date
      }
    })
    this.projectMemberDialogId = dialogRef.id;
    dialogRef.afterClosed().subscribe((response) => {
      if (!!response && response?.status === true && response.members) {
        this.form.controls.members.setValue(Helpers.cloneDeep(response.members));
      }
      this.calculateMaxMemberCount();
    })
  }

  onEnter() { }

  calculateMaxMemberCount() {
    this.maxMemberCount = 0;

    const memberDates: memberDate[] = this.form.controls.members.value.map((item: { start_date: any; end_date: any; }) => ({
      startDate: new Date(item.start_date),
      endDate: new Date(item.end_date),
    }))


    const datePoints: { date: any; type: string; }[] = [];

    memberDates.forEach((range) => {
        datePoints.push({ date: range.startDate, type: 'start' });
        if (!isNaN(range.endDate.getTime())) datePoints.push({ date: range.endDate, type: 'end' });
    });

    datePoints.sort((a, b) => a.date - b.date);

    let currentCount: number = 0;

    for (const point of datePoints) {
        if (point.type === 'start') {
            currentCount++;
            this.maxMemberCount = Math.max(this.maxMemberCount, currentCount);
        } else {
            currentCount--;
        }
    }

  }

  saveData() {
    const loader = this.comLoader.showProgressBar()
    this.trimAllTextInput()
    var passingData = Helpers.cloneDeep(this.form.getRawValue());
    passingData.pm_name = passingData.pm_name.name;
    this.projectsService.putProjects(this.passingData.data.project_id, {...passingData,highlight:this.highlight})
    .pipe(
      finalize(() => {
        this.comLoader.hideProgressBar(loader)
      }),
      catchError((error: HttpErrorResponse) => {
        if(error.status===STATUS.BAD_REQUEST){
          const messageError = error.error.message?.toLowerCase()
          if(messageError.includes('startdate') && messageError.includes('member')){
            this.notification.error(this.translateServce.instant("projects.detail.member_start_date_not_value"));
          }else{
            this.notification.error(error.error.message);
          }
        }
        return of(error);
      })
    )
    .subscribe((resp) => {
      if (resp.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
        this.projectsService.updateProject(true)
        this.dialogRef.close(resp.data);
        this.notification.success(this.translateServce.instant("projects.detail.notification_update_success"));
      }
    })
  }
  
  addData() {
    const loader = this.comLoader.showProgressBar()
    this.trimAllTextInput()
    this.postProjectRequestData = this.form.getRawValue();
    this.postProjectRequestData.pm_name = this.form.controls.pm_name.value.name;
    this.projectsService.postProjects(this.postProjectRequestData).pipe(
      finalize(() => {
        this.comLoader.hideProgressBar(loader)
      }),
      catchError((error: HttpErrorResponse) => {
        return [error.error];
      })
    ).subscribe((response) => {
      if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
        this.projectsService.addProjectEvent.emit();
        this.dialogRef.close(response);
        this.notification.success(this.translateServce.instant("projects.detail.notification_add_success"));
      } else {
        this.notification.error(response.message);
      }
    }) 
  }

  isAllowAddNewSkillTag(skillTagName: string){
    if(typeof(skillTagName) !== 'string' || skillTagName.trim().length === 0) return true;
    let isExists = [];
    if(skillTagName.trim().length > 0){
      isExists = this.originalSkillTagList.filter((skillTagObject: any)=>
        skillTagObject.name.trim().toLowerCase() === skillTagName.trim().toLowerCase()
      )
    }
    if(isExists.length > 0) return true;
    if(this.form.controls.skill_tags.value.length > 0 && skillTagName.trim().length > 0){
      isExists = this.form.controls.skill_tags.value.filter((skillTagObject: any)=>
        skillTagObject.name.trim().toLowerCase() === skillTagName.trim().toLowerCase()
      )
    }
    if(isExists.length === 0) return false;
    return true;
  }
  isSkillTagSelected(skillTagName : string){
    let isExists = [];
    isExists = this.form.controls.skill_tags.value.filter((skillTagObject: any)=>
        skillTagObject.name.trim().toLowerCase() === skillTagName.trim().toLowerCase()
    )
    if(isExists.length === 0) return false;
    return true;
  }
  addNewSkillTag(skillTagName: string){
    let isExists = [];
    if(typeof(skillTagName) !== 'string') return;
    if(this.form.controls.skill_tags.value.length > 0){
      isExists = this.form.controls.skill_tags.value.filter((skillTagObject: any)=>
        skillTagObject.name.trim().toLowerCase() === skillTagName.trim().toLowerCase()
      )
    }
    if(isExists.length === 0){
      this.form.controls.skill_tags.value.push({name: skillTagName});
      this.form.controls.skill_tags.setValue(this.form.controls.skill_tags.value);
      this.allowCreateSkillTag = this.isAllowAddNewSkillTag(this.skillTagSearchControl.value);
    }
    this.skillTagNativeTagInput.nativeElement.value='';
    this.skillTagNativeTagInput.nativeElement.focus()
    this.skillTagInput.closePanel();
    this.filteredSelectedSkillTag()
    this.form.controls.skill_tags.value?.length !== 0 ? this.inValidFieldTechnologyUsed = false : this.inValidFieldTechnologyUsed = true
  }
  selectedSkillTag(e: any){
    if(!this.form.controls.skill_tags.value.includes(e.option.value)){
      this.form.controls.skill_tags.value.push({name: e.option.value.name});
      this.form.controls.skill_tags.setValue(Helpers.cloneDeep(this.form.controls.skill_tags.value));
      this.filteredSelectedSkillTag();
    }
    this.skillTagNativeTagInput.nativeElement.value='';
    this.skillTagNativeTagInput.nativeElement.focus();
    this.form.controls.skill_tags.value?.length !== 0 ? this.inValidFieldTechnologyUsed = false : this.inValidFieldTechnologyUsed = true
  }
  removeSkillTag(tech:any){
    this.form.controls.skill_tags.setValue(this.form.controls.skill_tags.value.filter((skill : any) => 
      skill.name !== tech.name
    ));
    this.filteredSelectedSkillTag();
    this.form.controls.skill_tags.value?.length !== 0 ? this.inValidFieldTechnologyUsed = false : this.inValidFieldTechnologyUsed = true
  }

  //the selected tag will not show in autocomplete panel until user remove it in chip list
  filteredSelectedSkillTag(){
    this.filteredSkillTagList = Helpers.cloneDeep(this.originalSkillTagList);
    for(let skTag of this.form.controls.skill_tags.value){
      this.filteredSkillTagList = this.filteredSkillTagList.filter((filterskTag: any) => filterskTag.name !== skTag.name);
    }
  }

  filteredSelectedProjectPhase(){
    this.filteredProjectPhases = Helpers.cloneDeep(this.originProjectPhases);
    for(let pjPhase of this.form.controls.project_phases.value){
      this.filteredProjectPhases = this.filteredProjectPhases.filter((filterPjPhase: any) => filterPjPhase.name !== pjPhase.name);
    }
  }


  getAllSkillTags(){
    const loader = this.loadService.showProgressBar();
    this.projectsService.getAllSkillTags()
    .pipe(finalize(()=> this.loadService.hideProgressBar(loader)))
    .subscribe((response)=>{
      if(response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS){
        this.originalSkillTagList = response.data.map((skillTag:any) => 
          ({name : skillTag.name})
        );
        this.filteredSkillTagList = Helpers.cloneDeep(this.originalSkillTagList);
        this.filteredSelectedSkillTag();
      }
    });
  }
  //custom validator start_date< end_date
  endDateValidator(control: AbstractControl): { [key: string]: any } | null {
    const startDate = control.parent?.get('start_date')?.value;
    const endDate = control.value;
    if (startDate && endDate && startDate >= endDate) {
      return { 'invalidEndDate': true };
    }
    return null;
  }
  // disabled project start date which > smallest member start date
  filterProjectStartDay = (d: Date | null): boolean => {
    if(this.form.controls.members?.value && this.form.controls.members?.value.length > 0){
      const dateArr = this.form.controls.members?.value?.map((member:any) => new Date(member.start_date))
      const smallestStartDateMember = new Date(Math.min(...dateArr))
      const day = (d || new Date());
      day.setHours(7)
      if(day > smallestStartDateMember) {
        return false
      }
    }
    return true
  };
   // disabled project end date which < biggest member end date
   filterProjectEndDay = (d: Date | null): boolean => {
    if(this.form.controls.members?.value && this.form.controls.members?.value.length > 0){
      const dateArr = this.form.controls.members?.value?.map((member:any) => {
        if(member.end_date){
          return new Date(member.end_date)
        }
        return null
      })
      const biggestEndDateMember = new Date(Math.max(...dateArr))
      const day = (d || new Date());
      day.setHours(7)
      if(day < biggestEndDateMember) {
        return false
      }
    }
    return true
  };
  //custom validator whitespace input
  whiteSpaceValidator(control: AbstractControl) : {[key: string]: any} | null{
    const text = control.value;
    if(text === null || (control.dirty && text.length === 0)) return null;
    if(typeof(text) !== 'string' || text.trim().length === 0) return  {'invalidCharacterValidator': true};
    return null;
  }
  
  pmNameValidator(control : AbstractControl) : {[key: string] : any} | null{
    return null;
  }
  selectTagValidator(control: AbstractControl) : {[key: string]: any} | null{
    const selectedTags = control.value;
    if(control.touched && control.dirty && selectedTags.length === 0){
      return  {'invalidSelectedTag': true};
    } 
    return null;
  }
  skillTagInvalid(){
    if(this.form.controls.skill_tags.valid) return false;
    else return true;
  }
  projectPhaseFocus(){
    this.projectPhaseInput.openPanel();
  }
  preventDefault(event: MouseEvent) {
    event.preventDefault();
  }
  skillTagDropdownHandler(){
    if(this.skillTagInput.autocomplete.isOpen){
      this.skillTagInput.closePanel();
    }
    else{
      this.skillTagNativeTagInput.nativeElement.focus();
      this.skillTagInput.openPanel();
    }
  }

  projectPhaseDropdownHandler(){
    if(this.projectPhaseInput.panelOpen){
      this.projectPhaseInput.closePanel();
    }
    else{
      this.projectPhaseNativeTagInput.nativeElement.focus();
      this.projectPhaseInput.openPanel();
    }
  }
  onFocusElement(element: any){
    element.focus();
  }
  
  projectPhaseClickOutSide(outSide: boolean){
    this.projectPhaseClickOut = outSide;
  }

  onInputTeamSize(event: any) {
    // Get the entered value from the input event
    const input = event.target.value;
    const numericValue = input.replace(/\D|^0+/g, '');
    this.form.patchValue({
      team_size: numericValue,
    });
  }

  fetchOptionData(searchKey: string) {
    this.isShowLoading = true;
    this.subscription = this.projectsService
      .searchLDAPUsers(searchKey)
      .pipe(takeUntil(this.destroyed$), debounceTime(500))
      .pipe(
        finalize(() => {
          this.isShowLoading = false;
        })
      )
      .subscribe((res: any) => {
        this.ldapManagerList = (res?.data as Array<LdapUserModel>).map(item => ({name: item.displayName})) || [];
      });
  }

  onSelectManager(event: any){
    this.form.get('pm_name')?.setValue({name : event.option.value});
  }

  onSearchManagerChange(manager : any){
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    this.isShowLoading = true;
    if(typeof(manager) !== 'string'){
      this.fetchOptionData(manager.name);
      return;
    }
    this.fetchOptionData(manager);
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
    if(this.subscription){this.subscription.unsubscribe();}
    window.removeEventListener('scroll', this.scrollEvent, true);
    this.dialogRef.close();
    if(this.projectMemberDialogId.length>0){
      this.dialogCommonService.closeDialogById(this.projectMemberDialogId);
    }
  }
  getNextDate(currentDate: string){
    const crDate = new Date(currentDate); 
    let nextDate = new Date(currentDate);
    nextDate.setDate(crDate.getDate() + 1);
    return nextDate;
  }

  getPreviousDate(currentDate: string){
    if(currentDate){
      const crDate = new Date(currentDate); 
      let previousDate = new Date(currentDate);
      previousDate.setDate(crDate.getDate() - 1);
      return previousDate;
    }
    return null;
  }

  replaceLeftToRightMark(text : any)
  {
    if(typeof(text) === 'string'){
      text = text.replace(/&lrm;|\u200E/gi,'');
    }
    return text;
  }

  scrollEvent = (event: any): void => {
    CONFIG
    if(event.target.id !== CONFIG.COMMON_DIALOG.DIALOG_CONTAIN_CONTENT_ID) return;
    if(this.projectPhaseInput && this.projectPhaseInput.panelOpen)
      this.projectPhaseInput.closePanel();
    if(this.skillTagInput && this.skillTagInput.panelOpen)
      this.skillTagInput.closePanel();
    if(this.pmNameInput && this.pmNameInput.panelOpen)
      this.pmNameInput.closePanel();
  };
  isElementOverflow(element : any){
    return element.offsetHeight < element.scrollHeight ||
      element.offsetWidth < element.scrollWidth;
  }
  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  trimAllTextInput() {
    this.form.controls.name.setValue(this.form.controls.name.value.trim())
    this.form.controls.challenge.setValue(this.form.controls.challenge.value?.trim())
    this.form.controls.customer_gb.setValue(this.form.controls.customer_gb.value.trim())
    this.form.controls.stakeholder.setValue(this.form.controls.stakeholder.value.trim())
    this.form.controls.description.setValue(this.form.controls.description.value.trim())
    this.form.controls.objective.setValue(this.form.controls.objective.value.trim())
    this.form.controls.reference_link.setValue(this.form.controls.reference_link.value?.trim())
  }
  onBlurInput(type:string) {
    if(type===this.TECHNOLOGY_USED) {
      this.form.controls.skill_tags.value?.length !== 0 ? this.inValidFieldTechnologyUsed = false : this.inValidFieldTechnologyUsed = true
    }else if (type===this.PROJECT_PHASES) {
      this.form.controls.project_phases.value?.length !== 0 ? this.inValidFieldProjectPhases = false : this.inValidFieldProjectPhases = true

    }
  }

  isNotEmptyName(input: any){
    return input.value && input.value.length > 0;
  }

  getListCustomerGb(){
    const loader = this.loadService.showProgressBar();
    this.projectsService.getCustomerGb()
      .pipe(
      takeUntil(this.destroyed$),
      finalize(()=>{this.loadService.hideProgressBar(loader)})
      )
      .subscribe((response)=>{
        if(response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS){
          this.listCustomerGb = response.data;
        }
      })
  }

  getAllProjectScopes() {
    this.projectsService.getAllProjectScope().subscribe((result: BaseResponseModel) => {
      if(result.code===CONFIG.API_RESPONSE_STATUS.SUCCESS) {
        this.projectScopeList  = result.data
      }
    });
  }
  numberOfMemberWorkingUntilProjectEndDate() {
    const endDate = this.form.controls.end_date?.value ? new Date(this.form.controls.end_date?.value).getTime() : 0
    const members = this.form.controls.members.value;
    let count = 0
   
    const uniqueNames = new Set();
        members.forEach((e: any) => {
          const isValidCount = !e.end_date || new Date(e.end_date).getTime() >= this.CURRENT_DATE.getTime()
          if (!uniqueNames.has(e.name) && isValidCount) {
            uniqueNames.add(e.name);
            count++
          }
        });

   

    return count

  }

  getHighlightProject() {
    this.projectsService.getProjectPortfolio(this.passingData.data.project_id)
        .pipe(
          catchError((error: any) => {
            return throwError(error);
          })
        )
        .subscribe((response: any) => {
          if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
            this.highlight = response.data.highlight
          }
        });
  }
}
