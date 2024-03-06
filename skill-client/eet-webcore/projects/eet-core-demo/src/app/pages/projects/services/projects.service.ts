import { BaseReponseModel } from './../../../../../../../nestjs/core-backend/src/groups/group.model';
import { HttpClient } from '@angular/common/http';
import { Injectable, EventEmitter, LOCALE_ID, Inject } from '@angular/core';
import { ProjectDetailBosch } from '../models/dialog-data/project-detail-bosch.model';
import { ProjectDetailNonBosch } from '../models/dialog-data/project-detail-non-bosch.model';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { API, CORE_URL, ELASTIC_DOCUMENT } from '../../../shared/constants/api.constants';
import { DEFAULT_ELASTICSEARCH_PARAMETERS } from '../constants/projects-default-parameters.constants';
import { Observable } from 'rxjs/internal/Observable';
import { Subject } from 'rxjs/internal/Subject';
import { BaseResponseModel } from '../../../shared/models/base.model';
import { CustomerUpdateEventModel, FilterProjectModel, GBFilterModel, ProjectScope, Projects } from '../models/projects.model';
import { formatDate } from '@angular/common';
import { take } from 'rxjs/operators';
import { BehaviorSubject } from 'rxjs/internal/BehaviorSubject';
@Injectable({
  providedIn: 'root'
})
export class ProjectsService {
  private selectedGb = new Subject();
  public Gb$ = this.selectedGb.asObservable();

  private changeProject$:BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public _changeProject = this.changeProject$.asObservable();

  private getProjectScope$:BehaviorSubject<ProjectScope[]> = new BehaviorSubject<ProjectScope[]>([]);
  public _getProjectScope = this.getProjectScope$.asObservable();

  private fetchListProjectSuccessful$:BehaviorSubject<Projects[]> = new BehaviorSubject<Projects[]>([]);
  public _fetchListProjectSuccessful = this.fetchListProjectSuccessful$.asObservable();

  constructor(protected http: HttpClient,@Inject(LOCALE_ID) private locale: string) {
   }
  
  getFilterTagList() {
    return this.http.get<any>(`/${CoreUrl.API_PATH}/${API.SKILLTAG}`);
  }

  getFilterList() {
    return this.http.get<any>(`/${CoreUrl.API_PATH}/${API.PROJECT}/filter`)
  }

  getProjectDetail(project_id: string) {
    return this.http.get<any>(`/${CoreUrl.API_PATH}/${API.PROJECT}/` + project_id)
  }

  postProjects(projectData: ProjectDetailNonBosch | any) {
    return this.http.post<ProjectDetailBosch>(`/${CoreUrl.API_PATH}/${API.PROJECT}`, {
      ...projectData,
      end_date: projectData?.end_date ? formatDate(projectData?.end_date, 'yyyy-MM-dd', this.locale) : null,
      start_date: formatDate(projectData?.start_date, 'yyyy-MM-dd', this.locale)
    })
  }

  putProjects(project_id: string, projectData: ProjectDetailNonBosch | any) {
    return this.http.post<any>(`/${CoreUrl.API_PATH}/${API.PROJECT}/${project_id}/update`, {
      ...projectData,
      end_date: projectData?.end_date ? formatDate(projectData?.end_date, 'yyyy-MM-dd', this.locale) : null,
      start_date: formatDate(projectData?.start_date, 'yyyy-MM-dd', this.locale)
    })
  }

  deleteProjects(id: string) {
    return this.http.post<any>(`/${CoreUrl.API_PATH}/${API.PROJECT}/` + id + `/delete`, {})
  }
  deleteMember(id: string, assocId: string) {
    return this.http.post<any>(`${CoreUrl.API_PATH}/${API.ASSOCIATE}/${assocId}/${API.PROJECT}/${id}/${API.DELETE}`, null)
  }
  getProjectRole() {
    return this.http.get(`${CoreUrl.API_PATH}/${API.ROLE}`)
  }
  getCommonTasks() {
    return this.http.get(
      `${CoreUrl.API_PATH}/${API.COMMON_TASK}`,
      {}
    );
  }
  getGBUnit() {
    return this.http.get(`${CoreUrl.API_PATH}/${API.GB}`)
  }

  getCustomerGbs(): Observable<BaseResponseModel> {
    return this.http.get<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.PROJECT}/${API.CUSTOMER_GB}`)
  }

  getCustomerGb(): Observable<BaseResponseModel> {
    return this.http.get<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.PROJECT}/${API.FILTER}/${API.CUSTOMER_GB}`)

  }

  updateOurCustomer(data: any): Observable<BaseResponseModel>{
    return this.http.post<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.PROJECT}/${API.CUSTOMER_GB}/${data.id}`, data);
  }

  deleteOurCustomer(id: string): Observable<BaseResponseModel>{
    return this.http.post<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.PROJECT}/${API.CUSTOMER_GB}/${id}/${API.DEL}`, {});
  }

  getDetailCustomerGb(id: string): Observable<BaseResponseModel>{
    return this.http.get<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.PROJECT}/${API.CUSTOMER_GB}/${id}`);
  }

  getManager() {
    return this.http.get(`${CoreUrl.API_PATH}/${API.MANAGER}`)
  }
  importProject(file: File, id: string) {
    const formData: FormData = new FormData()
    formData.append('file', file, file.name)
    return this.http.post(`${CoreUrl.API_PATH}/${API.PROJECT}/import/${id}`, formData)
  }
  getProjectPhase() : Observable<any> {
    return this.http.get(`${CoreUrl.API_PATH}/${CoreUrl.PHASE}/${CoreUrl.DROPDOWN}`)
  }
  searchLDAPUsers(searchUser: string) {
    return this.http.get<BaseResponseModel>(`${API.ACCOUNT.LDAP_USERS}?queryParams=${searchUser}`);
  }
  // ------------------------- FILTER GETTER AND SETTER
  private tagList: FilterProjectModel[] = [];
  private selectedType: string;
  private selectedGB: string;
  private selectedStatus: string;
  private selectedProjetScope:string
  private selectedGbUnit:string
  filterChangeEvent: EventEmitter<void> = new EventEmitter<void>();
  tagChangeEvent: EventEmitter<void> = new EventEmitter<void>();
  searchChangeEvent: EventEmitter<GBFilterModel> = new EventEmitter<GBFilterModel>();
  topChangeEvent: EventEmitter<boolean> = new EventEmitter<boolean>();
  addProjectEvent: EventEmitter<void> = new EventEmitter<void>();
  switchTabEvent: EventEmitter<string> = new EventEmitter<string>();
  private updateCustomer$: BehaviorSubject<CustomerUpdateEventModel> = new BehaviorSubject<CustomerUpdateEventModel>({type:'',message:undefined});
  public _updateCustomer = this.updateCustomer$.asObservable();
  public setUpdateCustomer(event: CustomerUpdateEventModel){
    this.updateCustomer$.next(event);
  }
  public resetUpdateCustomer(){
    this.updateCustomer$.next({type:'',message:undefined});
  }
  

  setTagList(tagList: FilterProjectModel[]) { this.tagList = tagList};
  getTagList(): FilterProjectModel[] { return this.tagList};
  setSelectedType(selectedType: string) { this.selectedType = selectedType };
  getSelectedType() { return this.selectedType};
  setSelectedGB(selectedGB: string) { this.selectedGB = selectedGB };
  getSelectedGB() { return this.selectedGB };
  setSelectedStatus(selectedStatus: string) { this.selectedStatus = selectedStatus };
  getSelectedStatus() { return this.selectedStatus };
  setSelectProjectScope(selectedProjectScope: string) {this.selectedProjetScope=selectedProjectScope}
  getSelectProjectScope() { return this.selectedProjetScope };
  setSelectGbUnit(selectedGbUnit: string) {this.selectedGbUnit=selectedGbUnit}
  getSelectGbUnit() { return this.selectedGbUnit };


  //-----------------------------
    
  selectGbData(type: string, data: any) {
    this.selectedGb.next({ type, data })
  } 
  getAllSkillTags() : Observable<any>{
    return this.http.get(`${CoreUrl.API_PATH}/${API.SKILL_TASK}`)
  }
  postSkillTag(skillTag : any) : Observable<any>{
    return this.http.post(`${CoreUrl.API_PATH}/${API.SKILL_TASK}`, skillTag);

  }
  addNonBoschProject(idUser: any, formData: any) {
    return this.http
      .post(`${CORE_URL}/${API.ASSOCIATE}/${idUser}/${API.PROJECT}/${API.NON_BOSCH}`, {
        ...formData,
        end_date: formData?.end_date ? formatDate(formData?.end_date, 'yyyy-MM-dd', this.locale) : null,
        start_date: formatDate(formData?.start_date, 'yyyy-MM-dd', this.locale)
      }, {

      })
      .pipe(take(1));
  }
  updateNonBoschProject(associate_id:string,id:string,formData:any):Observable<BaseResponseModel> {
    return this.http
    .post<BaseResponseModel>(`${CORE_URL}/${API.ASSOCIATE}/${associate_id}/${API.PROJECT}/${id}/${API.UPDATE}`, {
      ...formData,
      end_date: formData?.end_date ? formatDate(formData?.end_date, 'yyyy-MM-dd', this.locale) : null,
      start_date: formatDate(formData?.start_date, 'yyyy-MM-dd', this.locale)
    }, {

    })
    .pipe(take(1));
  }
 
  updateProject(isChange:boolean) {
    this.changeProject$.next(isChange)
  }

  getProjectScopeEvent(data:ProjectScope[]) {
    this.getProjectScope$.next(data)
  }

  fetchListProjectSuccessful(data:Projects[]){
    this.fetchListProjectSuccessful$.next(data)
  }

  viewProjectDetailEvent: EventEmitter<string> = new EventEmitter<string>();

  getProjectByCustomerIdAndSkillTag(id: string, skillTag: string): Observable<BaseReponseModel>{
    return this.http.get<BaseReponseModel>(`${CORE_URL}/${API.CUSTOMER}/${API.PROJECTS}`, {params: {
      customer_id: id,
      skill_tag: skillTag
    }});
  }
  
  getAllProjectScope():Observable<BaseResponseModel> {
    return this.http.get<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.PROJECT_SCOPE}`)
  }

  addCustomer(data: any): Observable<BaseResponseModel>{
    return this.http.post<BaseResponseModel>(`${CORE_URL}/${API.PROJECT}/${API.CUSTOMER_GB}`, data);
  }
  getProjectPortfolio(projectId:string) {
    return this.http.get<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.PROJECT}/${projectId}/${API.PORTFOLIO}`)
    
  }
  getProjectPortfolioLayout(projectId:string,projectLayout:string):Observable<BaseResponseModel> {
    return this.http.get<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.PROJECT}/${projectId}/${API.PORTFOLIO}/${projectLayout}`)
  }
  saveLayoutForProjectPortfolio(projectId:string,projectLayout:string,data:any):Observable<BaseResponseModel> {
    return this.http.post<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.PROJECT}/${projectId}/${API.PORTFOLIO}/${projectLayout}`,data)
  }
  saveHighlightProject(projectId:string,data:any) {
    return this.http.post<BaseResponseModel>(`${CoreUrl.API_PATH}/${API.PROJECT}/${projectId}/${API.PORTFOLIO}`,data)
  }
}
