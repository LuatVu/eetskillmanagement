import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LineManagerDialogComponent } from '../../dialogs/line-manager/line-manager/line-manager-dialog.component';
import { TranslateService } from '@ngx-translate/core';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { MatSort } from '@angular/material/sort';
import { Team } from '../../../model/line-manager/line-manager.model';
import { LineManagerService } from '../../../services/line-manager.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { finalize, startWith } from 'rxjs/operators';
import { BaseResponseModel } from 'projects/eet-core-demo/src/app/shared/models/base.model';
import { FormControl } from '@angular/forms';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { PaginDirectionUtil } from 'projects/eet-core-demo/src/app/shared/utils/paginDirectionUtil';

@Component({
  selector: 'eet-line-manager',
  templateUrl: './line-manager.component.html',
  styleUrls: ['./line-manager.component.scss']
})
export class LineManagerComponent implements OnInit {
  public displayedColumns: string[] = [
    'team',
    'line_manager',
    'action'
  ];
  public dataSource: MatTableDataSource<Team> = new MatTableDataSource();
  @ViewChild('input') inputSearch!: ElementRef
  @ViewChild(MatPaginator)
  private paginator!: MatPaginator;
  @ViewChild(MatSort) 
  sort: MatSort = new MatSort();
  public listTeam:Team[] = []
  public totalItems: number
  public lineManager: any[] = []
  public pageSizeOption = CONFIG.PAGINATION_OPTIONS
  constructor(
    public dialog: DialogCommonService,
    private translate: TranslateService,
    private lineManagerService: LineManagerService,
    private loaderService: LoadingService

  ) { }

  ngOnInit(): void {
    this.getAllLineManager()
    this.getAllTeam()
    
    this.lineManagerService.updateTeam$.subscribe((respone: any) => {
      if (respone) {
        this.getAllTeam()
      }
    })

  }
  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    PaginDirectionUtil.expandTopForDropDownPagination()

  }

  getAllLineManager() {
    const loader = this.loaderService.showProgressBar();
    this.lineManagerService.getAllLineManager().pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe((res: BaseResponseModel) => {
        this.lineManager = Helpers.cloneDeep(res.data)
      })
  }
  getAllTeam() {
    const loader = this.loaderService.showProgressBar();
    this.lineManagerService.getAllTeam().pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe((res: BaseResponseModel) => {
        if(res.code ===CONFIG.API_RESPONSE_STATUS.SUCCESS){
          this.dataSource=new MatTableDataSource<Team>(res.data)
          this.dataSource.sort=this.sort
          this.dataSource.paginator = this.paginator
          this.dataSource.filterPredicate = (data, filter: string): boolean =>{
            return data.name.toLowerCase().includes(filter) || data.line_manager?.displayName.toLowerCase().includes(filter);
          };
          this.totalItems=res.data.length
          this.listTeam=res.data
        }
      })
    
  }
  addAssociate() {
    const dialogRef = this.dialog.onOpenCommonDialog({
      component: LineManagerDialogComponent,
      title: this.translate.instant('system.line_manager.dialog.associate.title_add'),
      width: "615px",
      height: 'auto',
      type: 'edit',
      passingData: {
        type:"add",
        lineManager: this.lineManager,
        listTeam : this.listTeam
      }
    })
    dialogRef.afterClosed().subscribe((response) => {
      if (response) {
        this.inputSearch.nativeElement.value=''
      }
    })
  }
  adjustLineManager(teamInfo: Team) {
    const dialogRef = this.dialog.onOpenCommonDialog({
      component: LineManagerDialogComponent,
      title: this.translate.instant('system.line_manager.dialog.associate.title_update'),
      width: "615px",
      height: 'auto',
      type: 'edit',
      passingData: {
        type:"edit",
        currentTeamInfo: teamInfo,
        lineManager: this.lineManager,
        listTeam : this.listTeam
      }
    })
    dialogRef.afterClosed().subscribe((response) => {
      if (response) {
        this.inputSearch.nativeElement.value=''
      }
    })
  }
 
  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
