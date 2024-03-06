import { Component, OnInit, ChangeDetectorRef, ElementRef, ViewChild,ViewChildren, QueryList } from '@angular/core';
import { ProjectsService } from '../../services/projects.service';
import { finalize } from 'rxjs/operators';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { CustomerGbModel, CustomerUpdateEventModel, GBFilterModel } from '../../models/projects.model';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { BoschProjectDetailComponent } from '../bosch-project-detail/bosch-project-detail.component';
import { TranslateService } from '@ngx-translate/core';
import { ElasticService } from 'projects/eet-core-demo/src/app/shared/services/elastic.service';
import { ReplaySubject, Subscription, takeUntil, startWith, debounceTime } from 'rxjs';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { PermisisonService } from 'projects/eet-core-demo/src/app/shared/services/permisison.service';
import { SkillProjectDialogComponent } from './dialog/skill-project-dialog/skill-project-dialog.component';
import { ActivatedRoute, Router } from '@angular/router';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';

@Component({
  selector: 'eet-customer-gb',
  templateUrl: './customer-gb.component.html',
  styleUrls: ['./customer-gb.component.scss']
})
export class CustomerGbComponent implements OnInit {
  private directions = CONFIG.CUSTOMER_GB.DIRECTIONS;
  private customerGbColor = Helpers.cloneDeep(CONFIG.CUSTOMER_GB.COLOR_STYLE);
  private projectTagBgColor = Helpers.cloneDeep(CONFIG.VMODEL.COLORS);
  private searchSubscription : Subscription;
  private changeCustomerSubscription : Subscription;
  public customerGbs : CustomerGbModel[];
  public isFirstInit = false;
  public HAS_VIEW_PROJECT_DETAIL: boolean;
  public ourCustomerId: string = '';
  public CUSTOMER_GB = CONFIG.CUSTOMER_GB.CUSTOMER_GB;
  public PORTFOLIO = CONFIG.CUSTOMER_GB.PORTFOLIO;
  public V_MODEL = CONFIG.PROJECT.V_MODEL;
  public isTab: string = CONFIG.CUSTOMER_GB.CUSTOMER_GB;
  constructor(
    private projectService : ProjectsService,
    private elasticService : ElasticService,
    private loaderService : LoadingService,
    private dialogCommonService: DialogCommonService,
    private translateService: TranslateService,
    private permissionService : PermisisonService,
    private changeDetectorRef: ChangeDetectorRef,
    private router: Router,
    private notify: NotificationService
  ) {
    this.HAS_VIEW_PROJECT_DETAIL = permissionService.hasPermission(CONFIG.PERMISSIONS.VIEW_PROJECT_DETAIL);
  }

  ngOnInit() {
    this.projectService.switchTabEvent.emit(this.CUSTOMER_GB);
    this.changeCustomerSubscription = this.projectService._updateCustomer.subscribe((event: CustomerUpdateEventModel)=>{
      if(['add', 'delete'].includes(event.type) && event.message){
        this.notify.success(this.translateService.instant(event.message));
      }
    });

    this.searchSubscription = this.projectService.searchChangeEvent
    .pipe(takeUntil(new ReplaySubject(1))).pipe(startWith(null), debounceTime(500))
    .subscribe((data : any) => {
      if(data == null || data.type !== this.CUSTOMER_GB) return;
      if(data !== null && data.data.trim().length > 0){
        this.getCustomerGbElastic(data.data);
      }
      else if(this.isFirstInit){
        this.getCustomerGbElastic('');
      }
      else{
        this.isFirstInit = !this.isFirstInit;
      }
    });
    this.getCustomerGbElastic('');

  }
  randomNodePosition(element : any, index : number){
    for(let direcIndex = 0 ; direcIndex <this.directions.length; direcIndex++){
      if(index == direcIndex){
        switch(this.directions[direcIndex]){
          case  CONFIG.CUSTOMER_GB.DIRECTIONS[0]:
            element.top = {
              top : `${(Math.floor(Math.random()*31) - 60).toString()}px`,
              left: `calc(${Math.floor(Math.random()*101).toString()}% +  ${(Math.floor(Math.random()*-60)).toString()}px)`
            }
            break;
          case CONFIG.CUSTOMER_GB.DIRECTIONS[1]:
            element.right = {
              right : `${(Math.floor(Math.random()*31) - 60).toString()}px`,
              top: `${(Math.floor(Math.random()*161) - 60).toString()}px`
            }
            break;
          case CONFIG.CUSTOMER_GB.DIRECTIONS[2]:
            element.bottom = {
              bottom : `${(Math.floor(Math.random()*31) - 60).toString()}px`,
              left: `calc(${Math.floor(Math.random()*101).toString()}% + ${(Math.floor(Math.random()*-60)).toString()}px)`
            }
            break;
          case CONFIG.CUSTOMER_GB.DIRECTIONS[3]:
            element.left = {
              left : `${(Math.floor(Math.random()*31) - 60).toString()}px`,
              top: `${(Math.floor(Math.random()*161) - 60).toString()}px`
            }
            break;
          default:
            break;
        }
      }
    }
  }
  swapPosition(first_index: number, second_index: number, array: any[]){
    const tmp = array[second_index];
    array[second_index] = array[first_index];
    array[first_index] = tmp;
  }
  randomvalue(){
    let position : any = {};
    let position_one = Math.floor(Math.random()*this.directions.length);
    let position_two = Math.floor(Math.random()*this.directions.length);
    while(position_two == position_one){
      position_two = Math.floor(Math.random()*this.directions.length);
    }
    this.randomNodePosition(position, position_one);
    this.randomNodePosition(position, position_two);
    const randomIndex = Math.floor(Math.random()*this.customerGbColor.length);
    position.gbStyle = this.customerGbColor[randomIndex];
    this.swapPosition(randomIndex, this.customerGbColor.length-1, this.customerGbColor);
    return position;
  }
  applyRandomValue(customerGbs : CustomerGbModel[]){
    customerGbs.forEach(item => {
      item.detail = this.randomvalue();
    })
  }
  getListCustomerGb(){
    const loader = this.loaderService.showProgressBar();
    this.projectService.getCustomerGbs()
      .pipe(finalize(()=> this.loaderService.hideProgressBar(loader)))
      .subscribe((response)=>{
        if(response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS){
          this.customerGbs = response.data;
        }
      })
  }
  onProjectTagClick(event : any){
    event.stopPropagation();
    if(!this.HAS_VIEW_PROJECT_DETAIL) return
    const projectId = event.target.value;
    this.dialogCommonService.onOpenCommonDialog({
      component: BoschProjectDetailComponent,
      title: this.translateService.instant('projects.detail.title'),
      width: '80vw',
      height: 'auto',
      icon:'a-icon ui-ic-watch-on',
      type: 'view',
      passingData: {
        type: 'view',
        project_id: projectId
      }
    })
  }
  getCustomerGbElastic(query: string){
    const loader = this.loaderService.showProgressBar();
    this.elasticService.getDocument('customer_gb', {
      size: 10000,
      query: query,
      from: 0
    }).pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe(response => {
        this.customerGbs = response.arrayItems;
        this.applyRandomValue(this.customerGbs);
        this.randomProjectTagColor(this.customerGbs);
        this.applySortListByNameGB()

      });
  }
  ngOnDestroy(){
    if(this.searchSubscription)  
    {
      this.searchSubscription.unsubscribe();
    }
    if(this.changeCustomerSubscription){
      this.changeCustomerSubscription.unsubscribe();
    }
    this.projectService.resetUpdateCustomer();
  }
  get customerGbToShowLength() {
    return (this.customerGbs && this.customerGbs.length) ? this.customerGbs.length : 0; 
  }

  isElementOverflow(element : any){
    return element.offsetWidth < element.scrollWidth;
  }
  translate(instantInput: string){
    return this.translateService.instant(instantInput);
  }
  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  onDetailCustomerGb(customerGb: any){
    this.projectService.switchTabEvent.emit(this.PORTFOLIO);
    this.ourCustomerId = customerGb.id;
    this.router.navigate(['projects', 'customer-portfolio'], {state: {id: this.ourCustomerId}});
  }

  randomProjectTagColor(customerGbs: CustomerGbModel[]){
    const arrLength = this.projectTagBgColor.length;
    customerGbs.forEach(element => {
      element.projects.forEach(project => project.bgColor = this.projectTagBgColor[this.randomIndex(arrLength)])
    })
    const randomIndex = Math.floor(Math.random()*this.projectTagBgColor.length);
  }

  randomIndex(length: number){
    return Math.floor(Math.random()*length);
  }

  onSkillTagClick(event: any, customerGb: any, skillTag: string){
    event.stopPropagation();
    this.dialogCommonService.onOpenCommonDialog({
      component: SkillProjectDialogComponent,
      width: '958px',
      height: 'auto',
      title: this.translateService.instant('vmodel.dialog.list_project.title'),
      icon: '',
      type: 'view',
      passingData: {
        customerGb: customerGb,
        skillTag: skillTag
      }
    });
  }
  
  stopPropagation($event: MouseEvent){
    $event.stopPropagation();
  }
  applySortListByNameGB() {
    this.customerGbs.sort((a:CustomerGbModel, b:CustomerGbModel) => {
      if(a.numOfHC !== b.numOfHC){
        return b.numOfHC - a.numOfHC;
      }
      else if(a.numOfProject !== b.numOfProject){
        return b.numOfProject - a.numOfProject;
      }
      else return a.name.localeCompare(b.name);
    });
  }
}
