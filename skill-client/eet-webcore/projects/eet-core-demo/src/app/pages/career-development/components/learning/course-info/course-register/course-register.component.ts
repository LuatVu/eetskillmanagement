import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { TranslateService } from '@ngx-translate/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { ReplaySubject, Subscription, debounceTime, startWith } from 'rxjs';
import { finalize, takeUntil } from 'rxjs/operators';
import { CourseDetail, CourseMembers, LEARNING_STATUS } from '../../my-learning/model/my-learning.model';
import { MyLearningService } from '../../my-learning/my-learning.service';

@Component({
  selector: 'eet-course-register',
  templateUrl: './course-register.component.html',
  styleUrls: ['./course-register.component.scss'],
})
export class CourseRegisterComponent implements OnInit {
  //flag to check manager permission!
  private isManager: boolean = true;
  public infoData: CourseDetail;
  public displayedColumns: string[] = [
    "display_name",
    "start_date",
    "end_date",
    "action"
  ]
  private membersInfo: CourseMembers[] = []
  private addedMembersInfo: CourseMembers[] = []
  public dataSource = new MatTableDataSource<CourseMembers>()
  @ViewChild(MatPaginator)
  public paginator!: MatPaginator;
  @ViewChild(MatSort)
  sort: MatSort = new MatSort()
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  private subscription: Subscription;
  private registeredMember: string[]

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialog: MatDialog,
    public comLoader: LoadingService,
    public learningService: MyLearningService,
    private dialogCommonService: DialogCommonService,
    private translate: TranslateService,
    private notifyService: NotificationService
  ) {
    this.infoData = data.data.data
    this.getMembers()
  }

  getMembers() {
    const loader = this.comLoader.showProgressBar()
    this.learningService.getMembersOfCourse(this.infoData.id).pipe(finalize(() => {
      this.comLoader.hideProgressBar(loader)
    })).subscribe((response: any) => {
      this.membersInfo = response.data
      this.dataSource.data = this.membersInfo
      for (let i = 0; i < this.addedMembersInfo.length; i++) {
        this.dataSource.data.push(this.addedMembersInfo[i])
      }
      this.dataSource.data = this.dataSource.data
      this.registeredMember = this.dataSource.data.map(
        (m) => m?.personal_id
      );
    })
  }
  ngOnInit(): void {
    this.searchControl.valueChanges.pipe(takeUntil(this.destroyed$), startWith(null), debounceTime(500)).subscribe(res => {
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
  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }
  public paginationOption = CONFIG.PAGINATION_OPTIONS;
  onDelete(info: any) {
    const dialogRef = this.dialogCommonService.onOpenConfirm({
      title: 'learning.my_learning.delete_member.title',
      content: this.translate.instant('learning.my_learning.delete_member.content.name_line') + info.display_name + '?\n' + this.translate.instant('learning.my_learning.delete_member.content.revert_line'),
      btnConfirm: 'learning.my_learning.delete_member.yes',
      btnCancel: 'learning.my_learning.delete_member.no',
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (!!info.isNotFromAPI) {
          this.addedMembersInfo = this.addedMembersInfo.filter(function (value, index, array) {
            return value.personal_id !== info.personal_id
          })
          this.notifyService.success("Remove successfully")
          this.getMembers()
        }
        else {
          const loader = this.comLoader.showProgressBar();
          this.learningService.deleteMemberFromCourse(info.personal_id, info.id)
            .pipe(finalize(() => this.comLoader.hideProgressBar(loader)))
            .subscribe((response: any) => {
              this.notifyService.success("Remove successfully")
              this.getMembers()
            }
            );
        }
      }
    })
  }
  onSave() {
    this.membersInfo = this.dataSource.data
    let payload = []
    for (let i = 0; i < this.membersInfo.length; i++) {
      payload.push({
        id: this.membersInfo[i].personal_id,
        start_date: this.membersInfo[i].start_date,
        end_date: this.membersInfo[i].end_date
      })
    }
    let loader = this.comLoader.showProgressBar()
    this.learningService.registerMember(this.infoData.id, payload).pipe(finalize(() => {
      this.comLoader.hideProgressBar(loader)
    })).subscribe((resp: any) => {
      if (resp.code === 'COURSE_ASSIGN_SUCCESS') {
        this.dialog.closeAll()
      }
      else {
        this.notifyService.error(resp.message)
      }
    })
  }
  isFieldBlocked(field: 'start_date' | 'end_date' | 'delete') {
    if (this.infoData.status === LEARNING_STATUS.CLOSED) {
      return true;
    }
    if (this.infoData.status === LEARNING_STATUS.ON_GOING && !(field === 'end_date')) {
      return true;
    }
    return null;
  }
  public currentMemberSearch: any = null
  public searchControl: FormControl = new FormControl();
  public isShowLoading: boolean = false;
  public optionList: any[] = [];
  onSearchChange() {
    this.isShowLoading = true;
  }
  displayFn(value: any): string {
    return value && value ? value : '';
  }
  blockNoResult(value: any) {
    if (value._source.displayName === '(No Result)' || value._source.personalId.length === 0 || !value._source.personalId) {
      return true;
    }
    return false;
  }
  selectOption(value: any) {
    if (value._source.displayName === '(No Result)' || value._source.personalId.length === 0 || !value._source.personalId) {
      return;
    }
    this.currentMemberSearch = {
      personal_id: value._source.personalId,
      display_name: value._source.displayName
    }
    this.searchControl.patchValue(value._source.displayName)
    this.isShowLoading = false;
  }
  findMembers(searchKey: string) {
    this.isShowLoading = true;
    this.subscription = this.learningService.elasticSearch(searchKey).pipe(
      takeUntil(this.destroyed$),
      finalize(() => {
        this.isShowLoading = false
      })
    ).subscribe((res: any) => {
      if (res.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
        if (res.data.numberOfResults !== 0) {
          this.optionList = JSON.parse(res.data.elements)
          this.optionList = this.optionList.filter(f => !this.registeredMember.find(member => member == f._source.id))
          if (this.optionList.length == 0) {
            this.optionList = [{
              _source: {
                personalId: "",
                displayName: "(No Result)"
              }
            }]
          }
        }
        else {
          this.optionList = [{
            _source: {
              personalId: "",
              displayName: "(No Result)"
            }
          }]
        }
      }
    })
  }
  refresh() {
    this.addMember()
    this.currentMemberSearch = null
    this.searchControl.reset()
    this.dataSource.data = this.dataSource.data
  }
  addMember() {
    let endDate = new Date()
    endDate.setDate(endDate.getDate() + this.infoData.duration / 24)
    let added = {
      id: "",
      display_name: this.currentMemberSearch.display_name || "",
      personal_id: this.currentMemberSearch.personal_id || "",
      start_date: new Date(),
      end_date: endDate,
      isNotFromAPI: true
    }
    this.dataSource.data.push(added)
    this.addedMembersInfo.push(added)
    this.registeredMember.push(added.personal_id)
  }
  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }
}
