import { Component, ElementRef, EventEmitter, Inject, LOCALE_ID, OnInit, Output, ViewChild, ChangeDetectorRef } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { Observable, filter, finalize, map, startWith } from 'rxjs';
import { ProjectDetailNonBosch } from '../../models/dialog-data/project-detail-non-bosch.model';
import { ProjectsService } from '../../services/projects.service';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { managerDTO } from '../../models/projects.model';
import { RoleModel } from 'projects/eet-core-demo/src/app/shared/models/common-model';
import { PersonalInfomationService } from '../../../user-information/modules/personal-information/personal-infomation.service';
import { formatDate } from '@angular/common';
import { SkillTagModel } from '../../../user-information/modules/personal-information/personal-infomation.model';
import { MatAutocomplete, MatAutocompleteTrigger } from '@angular/material/autocomplete';
@Component({
  selector: 'eet-non-bosch-project-detail',
  templateUrl: './non-bosch-project-detail.component.html',
  styleUrls: ['./non-bosch-project-detail.component.scss']
})
export class NonBoschProjectDetailComponent implements OnInit {
  @Output() changeListNonBoschProject: EventEmitter<boolean> = new EventEmitter<boolean>();
  @ViewChild('skillTagInput') skillTagNativeTagInput: ElementRef;
  @ViewChild('skillTagInput', { read: MatAutocompleteTrigger }) skillTagInput: MatAutocompleteTrigger;
  private project_id: string = ''
  public selectedEndDate: Date | null = null
  public projectDetailData: ProjectDetailNonBosch = {
    name: '',
    start_date: '',
    end_date: '',
    pm_name: '',
    challenge: '',
    status: '',
    role: '',
    additional_tasks: '',
    project_id: '',
    project_type: 'Non-Bosch',
    project_type_id: 'bd377e0d-688e-4b91-b328-334fa911a7d7',
    objective: '',
    gb_unit: '',
    team_size: '',
    reference_link: '',
    department: '',
    description: '',
  }

  public projectStatusList = CONFIG.PROJECT_STATUS_LIST
  public allowCreateSkillTag = true;
  public form!: FormGroup;
  public skillTagSearchControl = new FormControl();
  public filteredSkillTagList : SkillTagModel[];
  public originalSkillTagList : SkillTagModel[];
  public managerList: managerDTO[] = [];
  public roles: RoleModel[] = [];
  public skillTagAsText : string;
  public filteredManagerList: Observable<managerDTO[]>;
  private personalProjectId:string
  public skillTagClickOut : boolean = true;
  public inValidFieldTechnologyUsed:boolean = false
  public TECHNOLOGY_USED='technology_used'
  constructor(
    @Inject(MAT_DIALOG_DATA) public passingData: any,
    @Inject(LOCALE_ID) private locale: string,
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<NonBoschProjectDetailComponent>,
    public projectsService: ProjectsService,
    private loader: LoadingService,
    private personalInfomationService: PersonalInfomationService,
    private formBuilder: FormBuilder,
    private changeDetectorRef: ChangeDetectorRef
    ) {
    this.onBuildForm();
    this.skillTagSearchControl.valueChanges.subscribe(() =>{
      if(typeof(this.skillTagSearchControl.value) === 'string'){
        this.allowCreateSkillTag = this.isAllowAddNewSkillTag(this.skillTagSearchControl.value);
        this.filteredSelectedSkillTag();
        this.filteredSkillTagList = this.filteredSkillTagList.filter((tech : any) => 
        tech.name.toLowerCase().includes(this.skillTagSearchControl.value.toLowerCase()));
      }
    })
    if (this.passingData.data.type !== 'add') {
      const loader = this.loader.showProgressBar()
        this.projectsService.getProjectDetail(this.passingData.data.project_id).pipe(
          finalize(() => {
            this.loader.hideProgressBar(loader)
          })
        ).subscribe((response: any) => {
          this.form = this.formBuilder.group({
            id: this.formBuilder.control(response.data.id || this.passingData.data.project_id),
            name: this.formBuilder.control(response.data.name, [Validators.required,this.textValidator]),
            start_date: this.formBuilder.control(response.data.start_date,[Validators.required] ),
            end_date: this.formBuilder.control(response.data.end_date,[this.endDateValidator] ),
            pm_name: this.formBuilder.control(response.data.pm_name,[Validators.required,this.textValidator] ),
            challenge: this.formBuilder.control(response.data.challenge,[this.textValidator]),
            role_id: this.formBuilder.control(response.data.members ? response.data.members[0]?.role_id : null, [Validators.required]),
            role_name:this.formBuilder.control(response.data.members ? response.data?.members[0]?.role : null,[Validators.required] ),
            additional_tasks: this.formBuilder.control(response.data.members ? response.data?.members[0]?.additional_task : null,[Validators.required,this.textValidator] ),
            status: this.formBuilder.control({value:response.data.status,disabled:true}, ),
            project_type: this.formBuilder.control({value: response.data.project_type, disabled: true}, ),
            objective: this.formBuilder.control(response.data.objective, [Validators.required,this.textValidator]),
            description: this.formBuilder.control(response.data.description  || null,[Validators.required,this.textValidator] ),  
            skill_tags: this.formBuilder.control(response.data.skill_tags || [],[Validators.required,this.techUsedValidator]),
            team_size: this.formBuilder.control(response.data.team_size,[Validators.required,Validators.pattern('^[1-9][\\d]{0,1}$')]),
          })
          this.projectDetailData = response.data
          
          this.skillTagAsText = this.form.controls.skill_tags.value.map((skTag : any) => skTag.name).join(', ');
          this.personalProjectId = response.data.personal_project_id
          if (this.passingData.data.type === 'edit') {
            this.getAllData();
            this.form.markAllAsTouched();
            this.inValidFieldTechnologyUsed = this.form.controls.skill_tags.value.length === 0? true: false;
          }
          
        })
    }
    else {
      this.getAllData()
    }
  }

  ngOnInit(): void {
    window.addEventListener('scroll', this.scrollEvent, true);
    this.getprojectRole()
  }
  onBuildForm() {
    this.form = this.formBuilder.group({
      id: this.formBuilder.control(''),
      name: this.formBuilder.control(null, [Validators.required,this.textValidator]),
      start_date: this.formBuilder.control(null,[Validators.required] ),
      end_date: this.formBuilder.control(null,[this.endDateValidator] ),
      pm_name: this.formBuilder.control(null,[Validators.required,this.textValidator] ),
      challenge: this.formBuilder.control(null,[this.textValidator]),
      role_id: this.formBuilder.control(null, [Validators.required]),
      role_name:this.formBuilder.control(null, ),
      additional_tasks: this.formBuilder.control(null,[Validators.required,this.textValidator] ),
      status: this.formBuilder.control({ value: 'Done', disabled: true }, ),
      project_type: this.formBuilder.control({ value: 'Non-bosch', disabled: true }, ),
      objective: this.formBuilder.control(null, [Validators.required,this.textValidator]),
      description: this.formBuilder.control(null,[Validators.required,this.textValidator] ),  
      skill_tags: this.formBuilder.control([],[Validators.required,this.techUsedValidator]),
      team_size: this.formBuilder.control(null,[Validators.required,Validators.pattern('^[1-9][\\d]{0,1}$')])
    })
  }
  getAllData() {
    this.getAllSkillTags();
  }
 
  updateNonBoschProject() {
    this.trimAllTextInput()
    this.projectsService.updateNonBoschProject(this.personalInfomationService._idUser,this.personalProjectId,  this.form.getRawValue()).subscribe(response => {
      if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
        this.dialogRef.close(true)
      }
    })
  }
  getprojectRole() {
    this.personalInfomationService.getSharedData()
      .subscribe(data => {
        if (data['roleList']) {
          this.roles = data['roleList'] || [];
          this.roles = this.roles.sort((a: RoleModel, b: RoleModel) => a.name.localeCompare(b.name))
        }
      })
  }
  onInputTeamSize(event: any) {
    // Get the entered value from the input event
    const input = event.target.value;
    const numericValue = input.replace(/\D|^0+/g, '');
    this.form.patchValue({
      team_size: numericValue,
    });
  }
  endDateValidator = (control: AbstractControl): { [key: string]: any } | null => {
    
    let startDate = control.parent?.get('start_date')?.value;
    let endDate = control.value;
    
    if(!endDate) return null

    typeof(startDate) === "object" ? startDate = formatDate(startDate, 'dd/MM/yyyy', this.locale) : startDate
    typeof(endDate) === "object" ? endDate = formatDate(endDate, 'dd/MM/yyyy', this.locale) : endDate
    

    if (startDate && endDate && startDate >= endDate) {
      return { 'invalidEndDate': true };
    }
    
    return null;
  }
  techUsedValidator(control:AbstractControl): { [key: string]: any } | null {
    if(control.value){
      return null
    }
    return { 'invalidTechUsedValidator': true };
  }
  textValidator(control:AbstractControl): { [key: string]: any } | null {
    if(!control.value) return null
    if(control.value.trim() !== ''){
      return null
    }
    return { 'invalidTextValidator': true };
  }
  projectManagerNameValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      if (this.managerList && this.managerList.find(manager => manager.name === control.value)) {
        return null; 
      }
      return { 'invalidProjectManagerName': true };
    };
  }
  isAllowAddNewSkillTag(skillTagName: string){
    if(skillTagName.trim() === '') return true
    if(typeof(skillTagName) !== 'string') return true;
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
  convertStringToArrOfObjWithName(name:string) {
    const arr = name.split(',');
    const result = arr.map((e) => ({ name: e }));
    return result;
  }
  isSkillTagSelected(skillTagName : string){
    let isExists = [];
    isExists = this.form.controls.skill_tags?.value.filter((skillTagObject: any)=>
        skillTagObject.name.trim().toLowerCase() === skillTagName.trim().toLowerCase()
    )
    if(isExists.length === 0) return false;
    return true;
  }
  onRoleChange(e:any) {
    const role_name = this.roles.filter((role) => {
      if(role.id===e.value){
        this.form.controls.role_name.setValue(role.name)
      }
    })
  }
  onChangeStatus(e:any) {
    this.form.controls.status.setValue(e.value)
  }
  addNewSkillTag(skillTagName: string){
    let isExists = [];
    if(typeof(skillTagName) !== 'string') return;
    if(this.form.controls.skill_tags.value.length > 0){
      isExists = this.form.controls.skill_tags?.value.filter((skillTagObject: any)=>
        skillTagObject.name.trim().toLowerCase() === skillTagName.trim().toLowerCase()
      )
    }
    if(isExists.length === 0){
      this.form.controls.skill_tags.setValue([...this.form.controls.skill_tags.value,{name: skillTagName}])
      this.allowCreateSkillTag = this.isAllowAddNewSkillTag(this.skillTagSearchControl.value);
      }
    this.skillTagNativeTagInput.nativeElement.value='';
    this.skillTagNativeTagInput.nativeElement.focus()
    this.skillTagInput.closePanel();
    this.filteredSelectedSkillTag()
    this.form.controls.skill_tags.value?.length !== 0 ? this.inValidFieldTechnologyUsed = false : this.inValidFieldTechnologyUsed = true
  }
  addNewNonBoschProject() {
    const loader = this.loader.showProgressBar();
    this.trimAllTextInput()
    this.projectsService.addNonBoschProject(this.personalInfomationService._idUser,this.form.getRawValue())
      .pipe(finalize(() => this.loader.hideProgressBar(loader)))
      .subscribe((res: any) => {
        this.dialogRef.close(res);
      })
  }
  
  trimAllTextInput() {
    this.form.controls.name.setValue(this.form.controls.name.value.trim())
    this.form.controls.pm_name.setValue(this.form.controls.pm_name.value.trim())
    this.form.controls.objective.setValue(this.form.controls.objective.value.trim())
    this.form.controls.challenge.setValue(this.form.controls.challenge.value?.trim())
    this.form.controls.additional_tasks.setValue(this.form.controls.additional_tasks.value.trim())
    this.form.controls.description.setValue(this.form.controls.description.value.trim())
  }

  selectedSkillTag(e: any){
    if(!this.form.controls.skill_tags.value.includes(e.option.value)){
      this.form.controls.skill_tags.setValue([...this.form.controls.skill_tags.value,{name: e.option.value.name}])
    }
    this.filteredSelectedSkillTag();
    this.skillTagNativeTagInput.nativeElement.value='';
    this.skillTagNativeTagInput.nativeElement.focus()
    this.form.controls.skill_tags.value?.length !== 0 ? this.inValidFieldTechnologyUsed = false : this.inValidFieldTechnologyUsed = true

  }
  onSelectPm(value: any) {
    this.form.controls.pm_name.setValue(value.option.value)
  }
  removeSkillTag(tech:any){
    this.form.controls.skill_tags.setValue(this.form.controls.skill_tags.value.filter((skill : any) => 
      skill.name !== tech.name
    ));
    this.filteredSelectedSkillTag();
    this.form.controls.skill_tags.value?.length !== 0 ? this.inValidFieldTechnologyUsed = false : this.inValidFieldTechnologyUsed = true
  }

  filterManager(name: string): managerDTO[] {
    const filterValue = name.toLowerCase().trim();
    return this.managerList.filter(option => option.name.toLowerCase().trim().includes(filterValue));
  }
  getAllSkillTags(){
    const loader = this.loader.showProgressBar();
    this.projectsService.getAllSkillTags()
    .pipe(finalize(()=> this.loader.hideProgressBar(loader)))
    .subscribe((response)=>{
      if(response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS){
        this.originalSkillTagList = response.data.map((skillTag:any) => 
          ({name : skillTag.name})
        );
        this.filteredSelectedSkillTag();
      }
    });
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
  preventDefault(event: MouseEvent) {
    event.preventDefault();
  }
  onFocusElement(element: any){
    element.focus();
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
  //the selected tag will not show in autocomplete panel until user remove it in chip list
  filteredSelectedSkillTag(){
    this.filteredSkillTagList = Helpers.cloneDeep(this.originalSkillTagList);
    for(let skTag of this.form.controls.skill_tags.value){
      this.filteredSkillTagList = this.filteredSkillTagList.filter((filterskTag: any) => filterskTag.name !== skTag.name);
    }
  }
  skillTagClickOutSide(outSide: boolean){
    this.skillTagClickOut = outSide;
  }
  onBlurInput(type:string) {
    if(type===this.TECHNOLOGY_USED) {
      this.form.controls.skill_tags.value?.length !== 0 ? this.inValidFieldTechnologyUsed = false : this.inValidFieldTechnologyUsed = true
    }
  }
  isElementOverflow(element : any){
    return element.offsetHeight < element.scrollHeight ||
      element.offsetWidth < element.scrollWidth;
  }
  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }
  scrollEvent = (event: any): void => {
    CONFIG
    if(event.target.id !== CONFIG.COMMON_DIALOG.DIALOG_CONTAIN_CONTENT_ID) return;
    if(this.skillTagInput && this.skillTagInput.panelOpen)
      this.skillTagInput.closePanel();
  };
  ngOnDestroy(): void {
    window.removeEventListener('scroll', this.scrollEvent, true);
  }
}
