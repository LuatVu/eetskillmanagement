import { APP_BASE_HREF, Location } from '@angular/common';
import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  Inject,
  OnInit,
  ViewChild,
  HostListener
} from '@angular/core';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { PermisisonService } from 'projects/eet-core-demo/src/app/shared/services/permisison.service';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { debounceTime, finalize, switchMap, timer } from 'rxjs';
import { Subscription } from 'rxjs/internal/Subscription';
import { CustomerModel, GBFilterModel } from '../../models/projects.model';
import { ProjectsService } from '../../services/projects.service';
import { ProjectListComponent } from '../project-list/project-list.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'eet-our-customer',
  templateUrl: './our-customer.component.html',
  styleUrls: ['./our-customer.component.scss'],
})
export class OurCustomerComponent implements OnInit {
  @HostListener('window:beforeunload', ['$event'])
  onWindowClose(event: any): void {
    if (this.type === this.TYPES.EDIT) {
      event.preventDefault();
      event.returnValue = false;
    }
  }
  public tabList: { name: string }[] = [
    { name: 'our_customer.project_list' },
    { name: 'our_customer.v_model' },
  ];
  public permissions: {[dynamic: string]: string | boolean} = {
    HAS_EDIT_PROJECT: CONFIG.PERMISSIONS.EDIT_PROJECT,
    HAS_DELETE_PROJECT: CONFIG.PERMISSIONS.DELETE_PROJECT,
  };
  private readonly tabs : string[] = [CONFIG.PROJECT.V_MODEL, CONFIG.PROJECT.PROJECT_LIST];
  public selectedTabIndex: number = 0;
  public id: string;
  public customerGb: any = {};
  public filterData: GBFilterModel;
  public ourCustomer: CustomerModel;
  public corporations: string[] = [];
  isActiveTab: string = CONFIG.PROJECT.PROJECT_LIST;
  viewCheckedSubscription? = new Subscription();
  private readonly directions = CONFIG.CUSTOMER_GB.DIRECTIONS;
  private readonly CUSTOMER_GB = 'customer_gb'
  public ourCustomerSearchControl: FormControl = new FormControl();
  private readonly customerGbColor = Helpers.cloneDeep(
    CONFIG.CUSTOMER_GB.COLOR_STYLE
  );
  public TYPES = CONFIG.PROJECT.OPEN_PROJECT_TYPE;
  public type: string = this.TYPES.VIEW;
  public corporationControl: FormControl;
  public gbNameControl: FormControl;
  public highlightControl: FormControl;
  @ViewChild('delCorBtn', {read: ElementRef, static: false}) delCorBtn: ElementRef;
  @ViewChild('projectListComponentRef') projectListComponentRef: ProjectListComponent;  

  constructor(
    @Inject(APP_BASE_HREF) public baseHref: string,
    private loadingService: LoadingService,
    private notify: NotificationService,
    private projectService: ProjectsService,
    private permissionService: PermisisonService,
    private dialogCommonService: DialogCommonService,
    private translate: TranslateService,
    private changeDetectorRef: ChangeDetectorRef,
    private router: Router,
    private route: ActivatedRoute,
    private location: Location,
    public dialog: MatDialog,
  ) {
    const state = this.router.getCurrentNavigation()?.extras.state;
    this.id = state?.id;
    this.type = state?.type? state?.type: this.type;
    if(!state){
      this.returnToCustomerGb();
    }
  }

  ngOnInit(): void {
    this.corporationControl = new FormControl("");
    this.gbNameControl = new FormControl("");
    this.highlightControl = new FormControl("");
    this.permissions = this.filterPermission(this.permissions);
    this.ourCustomer = {name: '', hightlight:'',corporation:''} as CustomerModel;
    this.customerGb.detail = this.randomValue();
    if(this.type !== this.TYPES.ADD){
      this.getCustomerGbInfo();
      this.ourCustomerSearchControl.valueChanges
      .pipe(debounceTime(500))
      .subscribe((data) => {
        this.projectService.searchChangeEvent.emit({
          type: CONFIG.PROJECT.PROJECT_LIST,
          data: data,
        });
      });
    }
  }

  randomIndex(sourceLength: number, quantity: number) {
    let result: number[] = [];
    let tmp = 0;
    while (result.length < quantity) {
      tmp = Math.floor(Math.random() * sourceLength);
      if (!result.includes(tmp)) result.push(tmp);
    }
    return result;
  }

  randomNodePosition(element: any, index: number) {
    for (
      let direcIndex = 0;
      direcIndex < this.directions.length;
      direcIndex++
    ) {
      if (index == direcIndex) {
        switch (this.directions[direcIndex]) {
          case CONFIG.CUSTOMER_GB.DIRECTIONS[0]:
            element.top = {
              top: `${(Math.floor(Math.random() * 31) - 60).toString()}px`,
              left: `calc(${Math.floor(
                Math.random() * 101
              ).toString()}% +  ${Math.floor(
                Math.random() * -60
              ).toString()}px)`,
            };
            break;
          case CONFIG.CUSTOMER_GB.DIRECTIONS[1]:
            element.right = {
              right: `${(Math.floor(Math.random() * 31) - 60).toString()}px`,
              top: `${(Math.floor(Math.random() * 161) - 60).toString()}px`,
            };
            break;
          case CONFIG.CUSTOMER_GB.DIRECTIONS[2]:
            element.bottom = {
              bottom: `${(Math.floor(Math.random() * 31) - 60).toString()}px`,
              left: `calc(${Math.floor(
                Math.random() * 101
              ).toString()}% + ${Math.floor(
                Math.random() * -60
              ).toString()}px)`,
            };
            break;
          case CONFIG.CUSTOMER_GB.DIRECTIONS[3]:
            element.left = {
              left: `${(Math.floor(Math.random() * 31) - 60).toString()}px`,
              top: `${(Math.floor(Math.random() * 161) - 60).toString()}px`,
            };
            break;
          default:
            break;
        }
      }
    }
  }

  randomValue() {
    const random_positions = this.randomIndex(this.directions.length, 2);
    let position: any = {};
    random_positions.forEach((rand_pos) => {
      this.randomNodePosition(position, rand_pos);
    });
    const randomIndex = Math.floor(Math.random() * this.customerGbColor.length);
    position.gbStyle = this.customerGbColor[randomIndex];
    return position;
  }

  onTabChange() {
    switch (this.isActiveTab) {
      case CONFIG.PROJECT.V_MODEL:
        this.isActiveTab = CONFIG.PROJECT.PROJECT_LIST;
        break;
      case CONFIG.PROJECT.PROJECT_LIST:
        this.isActiveTab = CONFIG.PROJECT.V_MODEL;
        break;
    }
    this.emitFilterEvent(this.isActiveTab);
    this.projectService.switchTabEvent.emit(CONFIG.CUSTOMER_GB.PORTFOLIO);
    }

  emitFilterEvent(activeTab: string){
    switch (this.isActiveTab) {
      case CONFIG.PROJECT.PROJECT_LIST:
        this.projectService.setSelectedGB(this.ourCustomer.name);
        this.projectService.filterChangeEvent.emit();
        break;
      case CONFIG.PROJECT.V_MODEL:
        this.setFilterVModelData();
        break;
    }
  }

  returnToCustomerGb() {
    if(!this.id && this.type !== this.TYPES.ADD){
      this.router.navigate(['projects', 'customer-gb']);
    }
    this.location.back();
  }

  getCustomerGbInfo() {
    const loader = this.loadingService.showProgressBar();
    this.projectService
      .getDetailCustomerGb(this.id)
      .pipe(
        finalize(() => {
          this.loadingService.hideProgressBar(loader);
        })
      )
      .pipe(finalize(()=>this.loadingService.hideProgressBar(loader)))
      .subscribe((response)=>{
        if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.ourCustomer = response.data;
          this.getListCorporation();
          this.setFilterVModelData();
          this.gbNameControl.setValue(this.ourCustomer.name);
          this.highlightControl.setValue(this.ourCustomer.hightlight);
          this.getCurrentTab();
        }
      });
  }
  getListCorporation() {
    if(this.ourCustomer.corporation && this.ourCustomer.corporation.trim().length > 0){
      this.corporations = this.ourCustomer.corporation.split(',');
    }
  }
  ngAfterViewInit(): void {
    
  }
  ngOnDestroy() {
    this.viewCheckedSubscription?.unsubscribe();
  }

  onSearchProject() {
    this.projectService.searchChangeEvent.emit({
      type: CONFIG.PROJECT.PROJECT_LIST,
      data: this.ourCustomerSearchControl.value,
    });
  }

  filterPermission(permissions: any) {
    Object.keys(permissions).forEach(permis => {
      if(this.permissionService.hasPermission(permissions[permis])){
        permissions[permis] = true;
      }else{
        permissions[permis] = false;
      }
    });
    return permissions;
  }
  onEdit(){
    this.type = this.TYPES.EDIT
  }
  onCancel(){
    switch(this.type){
      case this.TYPES.ADD:
        this.dialogCommonService.onOpenConfirm({
          title: this.translate.instant('editor.dialog.confirm.title'),
          content: this.translate.instant('editor.dialog.confirm.content'),
          btnConfirm: this.translate.instant('editor.dialog.confirm.yes'),
          btnCancel: this.translate.instant('editor.dialog.confirm.no'),
          icon: 'a-icon ui-ic-alert-warning'
        }).afterClosed().subscribe(response => {
          if(response){
            this.returnToCustomerGb(); 
          }
        })
        break;
      case this.TYPES.EDIT:
        this.dialogCommonService.onOpenConfirm({
          title: this.translate.instant('editor.dialog.confirm.title'),
          content: this.translate.instant('editor.dialog.confirm.content'),
          btnConfirm: this.translate.instant('editor.dialog.confirm.yes'),
          btnCancel: this.translate.instant('editor.dialog.confirm.no'),
          icon: 'a-icon ui-ic-alert-warning'
        }).afterClosed().subscribe(response =>{
          if(response){
            this.type = this.TYPES.VIEW;
            this.getListCorporation();
            this.gbNameControl.setValue(this.ourCustomer.name);
            this.highlightControl.setValue(this.ourCustomer.hightlight);
            this.corporationControl.setValue('');
          }
        });
        break;
    }
  }
  deleteOurCustomer(id: string){
    const loader = this.loadingService.showProgressBar();
      this.projectService.deleteOurCustomer(id)
      .pipe(
        switchMap((response)=>{
          if(response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS){
            this.projectService.setUpdateCustomer({type: 'delete', message: 'our_customer.dialog.message.delete_success'});
          }
          return timer(1000);
        }),
        finalize(()=>{this.loadingService.hideProgressBar(loader)})
      )
      .subscribe(() => {
        this.returnToCustomerGb();
      });
  }
  onDelete(ourCustomer: CustomerModel){
    const dialogRef = this.dialogCommonService.onOpenConfirm({
      title: 'our_customer.dialog.confirm',
      icon: 'a-icon ui-ic-alert-warning',
      content: `${this.translate.instant('our_customer.dialog.remove_confirm')} ${ourCustomer.name}
      ${this.translate.instant('our_customer.dialog.remove_confirm_end')}`,
      btnConfirm: 'button.yes',
      btnCancel: 'button.no',
    });
    dialogRef.afterClosed().subscribe((response) => {
      this.delCorBtn.nativeElement.blur();
      if(response){
        this.deleteOurCustomer(ourCustomer.id);
      }
    });
  }
  onRemoveCorporation(cor: string){
    this.corporations = this.corporations.filter(f => f !== cor);
  }

  isCorporationValid(){
    const trimCor = this.corporationControl.value.trim().toLowerCase();
    const isDuplicate = this.corporations.filter(item => item.toLowerCase() === trimCor).length > 0;
    return trimCor.length === 0 || isDuplicate || (trimCor.length + 1 + this.corporations.join(",").length) > 1000;
  }

  onAddCorporation(){
    if(this.isCorporationValid()) return;
    const trimCor = this.corporationControl.value.trim();
    this.corporations.push(trimCor);
    this.corporationControl.setValue("");
  }
  onSave(){
    this.ourCustomer.corporation = this.corporations.join(",");
    this.ourCustomer.name = this.gbNameControl.value;
    this.ourCustomer.hightlight = this.highlightControl.value;
    this.corporationControl.setValue('');
    const loader = this.loadingService.showProgressBar();
    this.projectService.updateOurCustomer(this.ourCustomer)
    .pipe(finalize(()=>{this.loadingService.hideProgressBar(loader)}))
    .subscribe(response => {
      if(response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS){
        this.notify.success(this.translate.instant('our_customer.dialog.message.update_success'));
        this.projectService.setUpdateCustomer({type: 'edit', message: undefined});
        switch(this.isActiveTab){
          case CONFIG.PROJECT.V_MODEL:
            this.setFilterVModelData();
            break;
        }
      }else{
        this.notify.error(response.message);
      }
    })
    this.type = this.TYPES.VIEW;
  }
  
  isElementOverflow(element : any){
    return element.offsetHeight < element.scrollHeight ||
      element.offsetWidth < element.scrollWidth;
  }
  
  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }
  
  getCurrentTab(){
    const tabQuery = this.route.snapshot.queryParams.tab;
    this.isActiveTab = this.tabs.includes(tabQuery)? tabQuery: CONFIG.PROJECT.PROJECT_LIST;
    switch(this.isActiveTab){
      case CONFIG.PROJECT.V_MODEL:
        this.projectService.selectGbData(this.CUSTOMER_GB, this.ourCustomer.name);
        this.selectedTabIndex = this.tabs.indexOf(CONFIG.PROJECT.V_MODEL);
        break;
      case CONFIG.PROJECT.PROJECT_LIST: 
        this.projectService.setSelectedGB(this.ourCustomer.name);
        this.projectService.filterChangeEvent.emit();
        break;
    }
  }
  
  isFormValid(){
    return this.gbNameControl.value?.trim().length === 0;
  }

  onAdd(){
    this.ourCustomer.name = this.gbNameControl.value?.trim();
    this.ourCustomer.corporation = this.corporations.join(',');
    this.ourCustomer.hightlight = this.highlightControl.value?.trim();
    const loader = this.loadingService.showProgressBar();
    this.projectService.addCustomer(this.ourCustomer)
    .pipe(
      switchMap((response)=>{
        if(response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS){
          this.projectService.setUpdateCustomer({type: 'add', message: 'our_customer.dialog.message.add_success'});
        }
        return timer(1000);
      }),
      finalize(()=> this.loadingService.hideProgressBar(loader)))
    .subscribe(() => {
      this.returnToCustomerGb();
    });
  }

  setFilterVModelData(){
    this.filterData = {
      type: this.CUSTOMER_GB,
      data: this.ourCustomer.name,
    };
  }
}
