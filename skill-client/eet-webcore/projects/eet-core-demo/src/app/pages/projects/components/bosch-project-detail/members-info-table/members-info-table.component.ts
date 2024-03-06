import { AfterViewInit, Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort, MatSortModule, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmDialogComponent } from 'projects/eet-core-demo/src/app/shared/components/dialogs/confirm-dialog/confirm-dialog.component';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { CommonTask, MembersInfo } from '../../../models/dialog-data/members-info/members-info.model';
import { ProjectsService } from '../../../services/projects.service';
import { ProjectMemberDialogComponent } from '../project-member-dialog/project-member-dialog.component';
import { NotificationService } from '@bci-web-core/core';
import { Router } from '@angular/router';
import { UserInformationService } from '../../../../user-information/user-information.service';
import { BoschProjectDetailService } from '../bosch-project-detail.service';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { DatePipe } from '@angular/common';
import { PermisisonService } from 'projects/eet-core-demo/src/app/shared/services/permisison.service';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';

@Component({
  selector: 'eet-members-info-table',
  templateUrl: './members-info-table.component.html',
  styleUrls: ['./members-info-table.component.scss']
})
export class MembersInfoTableComponent implements OnInit, OnChanges, AfterViewInit {
  public displayedColumns: string[] = ['name', 'role', 'common_task', 'additional_task', 'start_day', 'end_day']
  @Input() id: string = ""
  @Input() members: MembersInfo[] 
  public oldMembersInfo: MembersInfo[]
  @Input() typeOfCommand: string = 'view'
  @Input() projectStartDate: Date
  @Input() projectEndDate: Date
  @Output() onEditChangedMember = new EventEmitter<MembersInfo[]>()
  public dataSource: MatTableDataSource<any>
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort: MatSort = new MatSort;
  public VIEW_ASSOCIATE_INFO_PERMISSION:boolean=false
  constructor(
    private dialogCommonService: DialogCommonService, public translate: TranslateService,
    public comLoader: LoadingService,
    public projectsService: ProjectsService,
    private notificationService: NotificationService,
    private router: Router,
    private userInformationService: UserInformationService,
    private service: BoschProjectDetailService,
    private permisisonService: PermisisonService) {
      this.VIEW_ASSOCIATE_INFO_PERMISSION = this.permisisonService.hasPermission(CONFIG.PERMISSIONS.VIEW_ASSOCIATE_INFO_PERMISSION);
  }
  ngAfterViewInit(): void {
    setTimeout(() => {
      this.handleSortTable()
    },0)
   
    
  }

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource<any>(this.members)

    if (this.typeOfCommand !== 'view' && !this.displayedColumns.includes('action')) {
      this.displayedColumns.push('action')
    }

    this.oldMembersInfo = Helpers.cloneDeep(this.members)
   
    this.handleSortTable()


  }

  ngDoCheck() {
    if (JSON.stringify(this.members) !== JSON.stringify(this.oldMembersInfo)) {
      this.ngOnInit()
    }
  }
  handleSortTable() {
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (item, property) => {
      switch (property) {
        case 'name':
          return item.name.toLowerCase();
          break;
        case 'role':
          return item.role.toLowerCase();
          break;
        case 'common_task':
          const lowercaseTask = item.common_task.map((task: CommonTask) => task.name.toLowerCase())
          return lowercaseTask;
          break;
        case 'additional_task':
          return item.additional_task?.toLowerCase();
          break;
        case 'start_day':
          return new Date(item.start_date);
        case 'end_day':
          if(item.end_date){
            return new Date(item.end_date);
          }
          return new Date(new Date().getFullYear() + 10, new Date().getMonth(), new Date().getDate())

      }
    };

    this.dataSource.sort.active = 'end_day';
    this.dataSource.sort.direction = 'desc'
  }
  ngOnChanges(changes: SimpleChanges) {
    if(changes.member){
      this.members = changes.members?.currentValue 
    }
  }
  editInfo(row: any) {
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: ProjectMemberDialogComponent,
      title: 'projects.detail.Member_Info.Member_Add.title_update',
      icon: 'a-icon boschicon-bosch-ic-edit',
      width: '80vw',
      height: 'auto',
      type: 'edit',
      passingData: {
        type: 'update',
        memberInfo: Helpers.cloneDeep(row),
        members: this.members,
        project_startDay: this.projectStartDate,
        project_endDay: this.projectEndDate
      }
    })
    dialogRef.afterClosed().subscribe((response) => {
      if (response?.status === true) {
        this.members[this.members.findIndex(i => i.uuid === response.members.uuid)] = response.members
        this.onEditChangedMember.emit(this.members)
        this.notificationService.success(this.translate.instant('notification.update_project_member'));
      }
    })
  }
  removeInfo(row: MembersInfo) {
    const content = this.members.length === 1 ? this.translate.instant('projects.delete_prompt.required_one_member') : this.translate.instant('projects.delete_prompt.content.name_line') + '. ' + '\n ' + this.translate.instant('projects.delete_prompt.content.revert_line')
    const dialogRef = this.dialogCommonService.onOpenConfirm({
      title: this.translate.instant('projects.delete_prompt.title'),
      content: content,
      btnConfirm: this.translate.instant('projects.delete_prompt.yes'),
      btnCancel: this.translate.instant('projects.delete_prompt.no')
    })
    dialogRef.afterClosed().subscribe(response => {
      if (response === true) {
        this.members = this.members.filter((val) => {
          return val.uuid !== row.uuid
        })
        this.onEditChangedMember.emit(this.members)
      }
    })

  }
  joinCommonTask(taskList: CommonTask[]): string {
    let joinedString: string = "";
    for (let i = 0; i < taskList.length; i++) {
      joinedString += taskList[i].name
      if (i !== taskList.length - 1) {
        joinedString += ', '
      }
    }
    return joinedString
  }

  isElementOverflow(element: any) {
    return element.offsetWidth < element.scrollWidth;
  }

  btnClick(id: string) {
    if(!this.VIEW_ASSOCIATE_INFO_PERMISSION) return
    this.router.navigate([`${CoreUrl.USER_INF}/${CoreUrl.ASSOCIATE_INF}`], { state: { 'id': id, 'mode': 'VIEW_ASSOCIATE_PROFILE' } });
    this.service.closeEvent.emit();
  }
}

