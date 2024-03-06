import {
  AfterViewInit,
  Component,
  OnInit,
  ViewChild
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { TOKEN_KEY } from '@core/src/lib/shared/util/system.constant';
import { TranslateService } from '@ngx-translate/core';
import { finalize } from 'rxjs/operators';

import {
  AddCourseDialogComponent,
} from '../add-course/add-course.component';
import { CourseInfoDialogComponent } from '../course-info/course-info.component';
import { CATEGORY_ORDER } from './model/dumb-data.constants';
import { CourseInformation, LEARNING_PERMISSIONS, OrderBy } from './model/my-learning.model';
import { MyLearningService } from './my-learning.service';
import { UploadCourseComponent } from './upload-course/upload-course.component';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { NotificationService } from '@bci-web-core/core';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { PaginDirectionUtil } from 'projects/eet-core-demo/src/app/shared/utils/paginDirectionUtil';


@Component({
  selector: 'eet-my-learning',
  templateUrl: './my-learning.component.html',
  styleUrls: ['./my-learning.component.scss'],
})
export class MyLearningComponent implements OnInit, AfterViewInit {
  public currentlyOrderBy: string;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  public localStoragePerms = JSON.parse(localStorage.getItem(TOKEN_KEY) || "{\"permissions\":[]}").permissions
  public PERMISSIONS = LEARNING_PERMISSIONS.PERMISSIONS
  public pageOptions = CONFIG.PAGINATION_OPTIONS
  public pageSize = 5
  public currentPage = 0
  //for sorting cards
  public categoryOrder: OrderBy[] = CATEGORY_ORDER
  public categoryOrderValue: string = "name"
  //NEED THIS TO CHECK PERMISSION!!!
  private currentNameFilter: string = ""
  public courseInfoList: CourseInformation[] = [];
  private originalData: CourseInformation[] = []
  private filteredCourseListButNotSearched: CourseInformation[] = []
  public filterdata = null;
  dataSource = new MatTableDataSource<CourseInformation>()
  public typeFilter: OrderBy[] = CONFIG.LEARNING_COURSE_TYPE.concat()
  public statusFilter: OrderBy[] = CONFIG.LEARNING_COURSE_STATUS.concat()
  public currentFilter: Map<'type' | 'status', Set<string>> = new Map<'type' | 'status', Set<string>>([
    ['type', new Set<string>()],
    ['status', new Set<string>()]
  ])
  // Checkbox
  constructor(
    public dialog: MatDialog,
    private dialogCommonService: DialogCommonService, private learningService: MyLearningService, private loadingService: LoadingService, public translate: TranslateService, public notifyService: NotificationService) {

  }

  ngAfterViewInit(): void {
    this.displayCourseList();

    PaginDirectionUtil.expandTopForDropDownPagination()
  }

  //Get Course List
  displayCourseList() {
    const loader = this.loadingService.showProgressBar();
    this.learningService
      .getCourseList(Array.from(this.currentFilter.get('type') as Set<string>).join(','), Array.from(this.currentFilter.get('status') as Set<string>).join(','))
      .pipe(finalize(() => this.loadingService.hideProgressBar(loader)))
      .subscribe((data) => {
        this.courseInfoList = data;
        this.originalData = JSON.parse(JSON.stringify(data))
        this.filteredCourseListButNotSearched = JSON.parse(JSON.stringify(this.originalData))
        this.currentPage = 0
        this.paginator.firstPage()
        this.onSort()
      });
  }

  ngOnInit(): void {

  }

  findPermissions(name: string) {
    const perm = this.localStoragePerms.filter((list: any) => {
      return list.code === name
    })
    return perm.length > 0 ? true : false;
  }

  onUpload() {
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: UploadCourseComponent,
      title: 'learning.my_learning.import.title',
      width: '600px',
      height: 'auto',
      type: 'edit',
      passingData: {}
    });
    dialogRef.afterClosed().subscribe(response => {
      if (response === true) {
        this.displayCourseList()
      }
    });

    // this.dialog.open(UploadCourseComponent, {
    //   width: '45%',
    // });
  }


  //action to view info
  viewItem(id: string) {
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: CourseInfoDialogComponent,
      title: 'learning.my_learning.info.title',
      width: '750px',
      height: 'auto',
      type: 'view',
      passingData: {
        type: 'view',
        id
      }
    })
    dialogRef.afterClosed().subscribe((response) => {
      this.displayCourseList()
    })
  }

  //action to add more course (for MANAGER only!)
  addItem() {
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: AddCourseDialogComponent,
      title: 'learning.my_learning.add_course.title',
      width: '80vw',
      height: 'auto',
      maxWdith: '1300px',
      type: 'edit',
      passingData: {
      }
    });
    dialogRef.afterClosed().subscribe((response) => {
      if (response === true) {
        this.displayCourseList()
      }
    });
  }

  //action to change course info (for MANAGER only!)
  changeItem(id: string) {
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: CourseInfoDialogComponent,
      title: 'learning.my_learning.info.title_edit',
      width: '98vw',
      maxWdith: '1170px',
      height: 'auto',
      type: 'edit',
      passingData: {
        type: 'edit',
        id
      }
    })
    dialogRef.afterClosed().subscribe((response) => {
      if (response === true) {
        this.displayCourseList()
      }
    })
  }

  //DeleteCourse
  removeCourse(data: string) {
    this.learningService.deleteCourse(data).subscribe((data) => { this.displayCourseList() });
  }

  //action to delete course info
  deleteItem(info: any) {
    const dialogRef = this.dialogCommonService.onOpenConfirm({
      title: 'learning.my_learning.delete_member.title',
      content: this.translate.instant('learning.my_learning.delete_member.content.name_line') + info.name + '?\n' + this.translate.instant('learning.my_learning.delete_member.content.revert_line'),
      btnConfirm: 'learning.my_learning.delete_member.yes',
      btnCancel: 'learning.my_learning.delete_member.no',
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const loader = this.loadingService.showProgressBar();
        this.learningService.deleteCourse(info.id)
          .pipe(finalize(() => this.loadingService.hideProgressBar(loader)))
          .subscribe((result) => {
            this.displayCourseList()
            this.notifyService.success("Delete successfully")
          })
      }
    })
  }

  onPaginationChange(event: any) {
    this.pageSize = event.pageSize
    this.currentPage = event.pageIndex
  }

  applyFilter(event: any) {
    if (event.checked) {
      this.currentFilter.get(event.source.name)?.add(event.source.ariaLabel)
    }
    else {
      this.currentFilter.get(event.source.name)?.delete(event.source.ariaLabel)
    }
    this.displayCourseList()
  }

  onSearch(event: any) {
    this.currentNameFilter = (event.target as HTMLInputElement).value
    if (!!this.currentNameFilter.length && this.currentNameFilter.length !== 0) {
      this.courseInfoList = this.filterByName(this.filteredCourseListButNotSearched)
    }
    else {
      this.courseInfoList = (JSON.parse(JSON.stringify(this.originalData)) as CourseInformation[])
    }
  }

  onSort() {
    const orderVal = this.categoryOrderValue

    this.courseInfoList.sort((a, b) => {
      if (orderVal === 'nameDescend') {
        return (a['name'].toLowerCase().trim() > b['name'].toLowerCase().trim()) ? -1 : (a['name'].toLowerCase().trim() < b['name'].toLowerCase().trim()) ? 1 : (a['id'] > b['id']) ? -1 : (a['id'] < b['id']) ? 1 : 0
      }
      const x = (a[orderVal as keyof CourseInformation]?.toString() as string).toLowerCase().trim()
      const y = (b[orderVal as keyof CourseInformation]?.toString() as string).toLowerCase().trim()
      return (x < y) ? -1 : (x > y) ? 1 : (a['id'] < b['id']) ? -1 : (a['id'] > b['id']) ? 1 : 0
    })
  }

  filterByName(data: CourseInformation[]) {
    const searchKey = this.currentNameFilter
    return (JSON.parse(JSON.stringify(data)) as CourseInformation[]).filter(
      filterValue => filterValue.name.toLowerCase().trim().includes(searchKey.toLowerCase().trim())
    )
  }

  ngOnDestroy(): void {
    PaginDirectionUtil.removeEventListenerForDirectionExpandPagination()
  }


}
