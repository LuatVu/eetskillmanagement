import {
  AfterViewInit,
  Component,
  OnInit,
  ViewChild,
  OnDestroy,
} from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { TranslateService } from '@ngx-translate/core';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { ReplaySubject, finalize, startWith } from 'rxjs';
import { Helpers } from '../../shared/utils/helper';
import { CONFIG } from './../../shared/constants/config.constants';
import { AddNewSupplyComponent } from './add-new-supply/add-new-supply.component';
import { EditSupplyComponent } from './edit-supply/edit-supply.component';
import { HistorySupplyComponent } from './show-history/history.component';
import { DATA_CONFIG } from './supply.constant';
import { SupplyModel, FilterDataModel, FilterModel } from './supply.model';
import { SupplyService } from './supply.service';
import { SUPPLY_STATUS } from 'projects/eet-core-demo/src/app/pages/user-management/common/constants/constants';
import { PermisisonService } from '../../shared/services/permisison.service';
import { PaginDirectionUtil } from '../../shared/utils/paginDirectionUtil';

import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'eet-supply',
  templateUrl: './supply.component.html',
  styleUrls: ['./supply.component.scss'],
})
export class SupplyComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort: MatSort = new MatSort();

  public HAS_VIEW_USER_DEMAND_SUPPLY: boolean = false;
  public HAS_VIEW_ADMIN_DEMAND_SUPPLY: boolean = false;
  public arrayFilter: FilterModel[] = DATA_CONFIG.ARRAY_FILTER;
  public arrayTagSkillGroup: string[];
  public getAsso!: string;
  public dataSource: MatTableDataSource<SupplyModel> = new MatTableDataSource();
  public searchControl = new FormControl();
  public totalItems: number;
  public displayedColumns: string[] = DATA_CONFIG.DISPLAY_COLUMNS;
  public filterData: FilterDataModel = {
    projectName: [],
    skillClusterName: [],
    assignUserName: [],
    createdByName: [],
    status: [],
  };

  private originalData: SupplyModel[] = [];
  private myDemandsList: SupplyModel[] = [];
  private dataTableList: SupplyModel[] = [];
  public readonly PAGINATION_SIZE_OPTIONS = CONFIG.PAGINATION_OPTIONS;
  private currentPaginator: MatPaginator;
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  public teamFilterControl = new FormControl([]);
  public currentViewAssociateMode: string = '';
  public readonly pageSizeOptions = CONFIG.PAGINATION_OPTIONS;
  private currentUser: any;
  isMyDemands: boolean = true;
  maxCharacter = 10;
  maxCharacterProject = 50;

  competencyOptionsList: string[];

  constructor(
    private supplyService: SupplyService,
    private dialogCommonService: DialogCommonService,
    private translate: TranslateService,
    private loaderService: LoadingService,
    private notifyService: NotificationService,
    private permisisonService: PermisisonService,
    private route: ActivatedRoute
  ) {
    this.HAS_VIEW_USER_DEMAND_SUPPLY = this.permisisonService.hasPermission(
      CONFIG.PERMISSIONS.USER_DEMAND_SUPPLY
    );
    this.HAS_VIEW_ADMIN_DEMAND_SUPPLY = this.permisisonService.hasPermission(
      CONFIG.PERMISSIONS.ADMIN_DEMAND_SUPPLY
    );
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    PaginDirectionUtil.expandTopForDropDownPagination()
  }

  ngOnInit() {
    this.getCurrentUser();
    this.getAllSupply(true);
    this.watchOnSearchChange();
    this.handleUrlParam()
  }

  getAllSupply(isInit: boolean) {
    const loader = this.loaderService.showProgressBar();
    this.supplyService
      .getAll()
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe((response: any) => {
        if (
          response.code &&
          response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS
        ) {
          this.originalData = JSON.parse(JSON.stringify(response.data));
          this.originalData.sort((a, b) => {
            return <any>new Date(b.createdDate) - <any>new Date(a.createdDate);
          });
          this.myDemandsList = this.originalData.filter(
            (supply) =>
              supply.createdByNtid?.toLowerCase() ===
                this.currentUser?.name.toLowerCase() ||
              supply.assignNtId?.toLowerCase() ===
                this.currentUser?.name.toLowerCase()
          );
          if (isInit || this.isMyDemands) {
            this.dataTableList = this.myDemandsList;
          } else {
            this.dataTableList = this.originalData;
          }

          this.dataSource = new MatTableDataSource<SupplyModel>();
          this.dataSource.data = this.dataTableList || [];
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
          this.totalItems = this.dataTableList.length;
          this.getAllFilterData();
          this.watchOnSearchChange();
          this.route.queryParams.subscribe((params) => {
            const idParam = params['id'];
            if (idParam) {
              this.searchControl.setValue(idParam);
            }
          });
        }
      });
  }

  getCurrentUser() {
    this.currentUser = JSON.parse(
      localStorage.getItem('Authorization') || '{}'
    );
  }

  getAllFilterData() {
    if (this.dataTableList.length) {
      this.filterData = {
        projectName: [],
        skillClusterName: [],
        assignUserName: [],
        createdByName: [],
        status: [],
      };
      for (let suplly of this.dataTableList) {
        if (!this.filterData.projectName.includes(suplly.projectName)) {
          this.filterData.projectName.push(suplly.projectName);
        }
        if (!this.filterData.createdByName.includes(suplly.createdByName)) {
          this.filterData.createdByName.push(suplly.createdByName);
        }
        if (
          !this.filterData.skillClusterName.includes(suplly.skillClusterName)
        ) {
          this.filterData.skillClusterName.push(suplly.skillClusterName);
        }
        if (
          suplly.assignUserName != undefined &&
          suplly.assignUserName?.trim() != '' &&
          !this.filterData.assignUserName.includes(suplly.assignUserName)
        ) {
          this.filterData.assignUserName.push(suplly.assignUserName);
        }
        if (!this.filterData.status.includes(suplly.status)) {
          this.filterData.status.push(suplly.status);
        }
      }
    }
    this.arrayFilter.map((elment) => {
      elment.originalData =
        this.filterData?.[elment.key as keyof FilterDataModel] || [];
    });
    this.watchOnFilterChange();
  }

  watchOnSearchChange() {
    this.searchControl.valueChanges
      .pipe(startWith(null))
      .subscribe((keySearch: string) => {
        if (keySearch && keySearch.startsWith('DS-')) {
          const supply = this.dataSource.data.find(
            (i: any) => i.subId === keySearch
          );
          if (supply) keySearch = supply.id;
        }
        this.dataSource.filter = keySearch?.toLowerCase();
        this.totalItems = this.dataSource.filteredData.length;
        if (this.dataSource.paginator) {
          this.dataSource.paginator.firstPage();
        }
      });
  }

  watchOnFilterChange() {
    let tmpData: SupplyModel[] = Helpers.cloneDeep(this.dataTableList);
    this.arrayFilter
      .filter((f) => f?.selectedData && f.selectedData?.length != 0)
      .forEach((element) => {
        switch (element?.key) {
          case 'projectName':
            tmpData = Helpers.cloneDeep(
              tmpData.filter((data) =>
                element.selectedData.includes(data.projectName)
              )
            );
            break;
          case 'skillClusterName':
            tmpData = Helpers.cloneDeep(
              tmpData.filter((data) =>
                element.selectedData.includes(data.skillClusterName)
              )
            );
            break;
          case 'createdByName':
            tmpData = Helpers.cloneDeep(
              tmpData.filter((data) =>
                element.selectedData.includes(data.createdByName)
              )
            );
            break;
          case 'assignUserName':
            tmpData = Helpers.cloneDeep(
              tmpData.filter((data) =>
                element.selectedData.includes(data.assignUserName)
              )
            );
            break;
          case 'status':
            tmpData = Helpers.cloneDeep(
              tmpData.filter((data) =>
                element.selectedData.includes(data.status)
              )
            );
            break;
          default:
            break;
        }
      });
    this.dataSource.data = Helpers.cloneDeep(tmpData);
    this.totalItems = this.dataSource.data.length;
  }

  addNewSupply() {
    const MIN_DIALOG_WIDTH = 700;
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: AddNewSupplyComponent,
      title: this.translate.instant('manage_associate.add_associate.title'),
      width:
        ((MIN_DIALOG_WIDTH * 100) / CONFIG.TABLET_MIN_WIDTH).toString() + 'vw',
      icon: 'a-icon a-button__icon ui-ic-plus',
      maxWdith: '1170px',
      height: 'auto',
      type: 'edit',
      passingData: {},
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.getAllSupply(false);
      }
    });
  }

  showHistory(id: string) {
    const MIN_DIALOG_WIDTH = 700;
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: HistorySupplyComponent,
      title: this.translate.instant('supply.history-dialog-title'),
      width:
        ((MIN_DIALOG_WIDTH * 100) / CONFIG.TABLET_MIN_WIDTH).toString() + 'vw',
      icon: 'a-icon a-button__icon boschicon-bosch-ic-history',
      maxWdith: '1170px',
      height: 'auto',
      type: 'view',
      passingData: {
        id: id,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {});
  }

  btnEditSupply(data: any) {
    const MIN_DIALOG_WIDTH = 700;
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: EditSupplyComponent,
      title: this.translate.instant('supply.edit-dialog-title'),
      width:
        ((MIN_DIALOG_WIDTH * 100) / CONFIG.TABLET_MIN_WIDTH).toString() + 'vw',
      icon: 'boschicon-bosch-ic-document-edit',
      maxWdith: '1170px',
      height: 'auto',
      type: 'edit',
      passingData: {
        editData: data,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
          this.getAllSupply(false);
          // this.isMyDemands = true; //vlc1hc follow Mr.Tam comment          
          this.notifyService.success(this.translate.instant('supply.update_success'));
      }
    });
  }

  btnDeleteSupply(info: any) {
    const dialogRef = this.dialogCommonService.onOpenConfirm({
      title: 'learning.my_learning.delete_member.title',
      content:
        this.translate.instant(
          'learning.my_learning.delete_member.content.name_line'
        ) +
        ' the selected demand of the project name: ' +
        info.projectName +
        '?\n' +
        this.translate.instant(
          'learning.my_learning.delete_member.content.revert_line'
        ),
      btnConfirm: 'button.yes',
      btnCancel: 'button.no',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const loader = this.loaderService.showProgressBar();
        this.supplyService
          .delete(info.id)
          .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
          .subscribe((response: any) => {
            this.notifyService.success(this.translate.instant('supply.delete_success'));
            this.getAllSupply(false);
          });
      }
    });
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
    this.dialogCommonService.toggleCloseAllDialog();

    PaginDirectionUtil.removeEventListenerForDirectionExpandPagination()
  }

  buildColorStatus(status: string) {
    switch (status) {
      case SUPPLY_STATUS.DRAFT:
        return '#0088d4';
      case SUPPLY_STATUS.OPEN:
        return '#198f51';
      case SUPPLY_STATUS.ON_HOLD:
        return '#b3b3b3';
      case SUPPLY_STATUS.ON_GOING:
        return '#eba611';
      case SUPPLY_STATUS.CANCEL:
        return '#f24822';
      case SUPPLY_STATUS.FILLED:
        return '#394360';
      default:
        return '#0088d4';
    }
  }

  buildColorForecastDate(element: any) {
    if (
      element.forecastDate &&
      element.expectedDate &&
      element.forecastDate > element.expectedDate
    ) {
      return '#f24822';
    }
    return 'rgba(0, 0, 0, 0.87);';
  }

  showDeleteIcon(supply: SupplyModel) {
    return supply.status === SUPPLY_STATUS.DRAFT;
  }

  onChangeShowAll(isMyDemand: any) {
    this.dataTableList = [];
    if (isMyDemand) {
      this.dataTableList = this.myDemandsList;
    } else {
      this.dataTableList = this.originalData;
    }
    this.dataSource = new MatTableDataSource<SupplyModel>();
    this.dataSource.data = this.dataTableList || [];
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.totalItems = this.dataSource.data.length;
    this.arrayFilter = DATA_CONFIG.ARRAY_FILTER;
    this.watchOnSearchChange();
    this.getAllFilterData();
    this.watchOnFilterChange();
  }

  handleUrlParam(){
    let subId: string;
    let supplyDemand: SupplyModel
    this.route.queryParams.subscribe(params => 
      {
        subId = params?.id;
        if(!subId){ return }
        subId = subId.slice(3)

        const numberSubId = Number.parseInt(subId)
        if(!numberSubId){ return }

        const loader = this.loaderService.showProgressBar();
        this.supplyService.getSupplyDemandBySubId(numberSubId)
          .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
          .subscribe((res: any)=>{
            supplyDemand = res?.data
            this.btnEditSupply(supplyDemand)
          })
      }
    );
  }
}
