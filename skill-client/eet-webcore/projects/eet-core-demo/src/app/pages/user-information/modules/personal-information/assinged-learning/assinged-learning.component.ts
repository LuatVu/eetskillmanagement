import { AfterViewInit, Component, Input, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { PesonalLearningService } from './pesonal-learning.service';

import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { finalize } from 'rxjs';
import { CourseModel } from '../personal-infomation.model';
import { PersonalInfomationService } from '../personal-infomation.service';
import { DialogDetailComponent } from './dialogs/dialog-detail/dialog-detail.component';
import { DialogLearningComponent } from './dialogs/dialog-learning/dialog-learning.component';
import { DialogUploadComponent } from './dialogs/dialog-upload/dialog-upload.component';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { PaginDirectionUtil } from 'projects/eet-core-demo/src/app/shared/utils/paginDirectionUtil';
import { DEFAULT_PAGE_SIZE } from '../constants/constants';

export interface PersonalLeanring {
  id: string;
  personal_course_name: string;
  course_type: string;
  category_name: string;
  duration: number;
  start_date: Date;
  end_date: Date;
  status: string;
  certificate: string;
  action: string;
  trainer: string;
  editstartDate: Date;
  editendDate: Date;
  course_id: string;
}
@Component({
  selector: 'eet-assinged-learning',
  templateUrl: './assinged-learning.component.html',
  styleUrls: ['./assinged-learning.component.scss'],
})
export class AssingedLearningComponent implements OnInit,AfterViewInit {
  @Input() isEditMode: boolean = false;

  public readonly pageSizeOptions = CONFIG.PAGINATION_OPTIONS;
  duration!: number;
  private selfStudy: boolean = false;
  private addCourse: boolean = false;
  @Input() typeUser!: string;
  private getidcourse!: string;
  private hideDetail: boolean = false;
  date: any;

  private selected: string = 'option2';
  @Input() isDisable: boolean = false;

  search(value: string): void { }

  public displayedColumns: string[] = [
    'cource',
    'type',
    'cagetory',
    'duration',
    'startDay',
    'endDay',
    'status',
    'certificate',
    'action',
  ];
  private personalLeanring!: CourseModel[];
  private personalleanring!: PersonalLeanring;

  public dataSource = new MatTableDataSource<CourseModel>();
  @Input() personalID!: string;
  form!: FormGroup;

  @Input() paginator:MatPaginator
  @ViewChild(MatSort)
  sort: MatSort = new MatSort();

  // statusList: string[] = ['NEW', 'ON-GOING', 'CLOSED'];
  public statusList: string[] = []


  public readonly paginationSize = CONFIG.PAGINATION_OPTIONS;
  constructor(
    public dialog: MatDialog,
    private pesonalLearningService: PesonalLearningService,
    private formBuilder: FormBuilder,
    private dialogCommonService: DialogCommonService,
    private personalInfomationService: PersonalInfomationService,
    private loaderService: LoadingService,
    private translate: TranslateService
  ) { 
    for (let i of CONFIG.LEARNING_COURSE_STATUS.concat()) {
      this.statusList.push(i.value)
    }
  }


  ngOnInit(): void {
    let iduser = JSON.parse(localStorage.getItem('Authorization') || '{}');
    let loginId = iduser.id;
    if (this.personalID == loginId) {
      this.addCourse = true;
    } else {
      this.selfStudy = true;
    }

    this.personalInfomationService.getPersonalInfoDetail().subscribe((data) => {
      this.personalLeanring = data.courses || [];
      this.dataSource = new MatTableDataSource(data.courses);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    });

    this.form = this.formBuilder.group({
      start_date: ['', [Validators.required]],
      end_date: ['', [Validators.required]],
      status: ['', [Validators.required]],
    });
  }

  applyFilter(searchKey: string) {
    this.dataSource.filter = searchKey?.trim()?.toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.paginator.pageSize =DEFAULT_PAGE_SIZE
    
  }

  onOpenUploadCertificate(course: CourseModel) {
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: DialogUploadComponent,
      title: 'Upload Certificate',
      width: '40vw',
      height: 'auto',
      icon: 'a-icon boschicon-bosch-ic-upload',
      type: 'view',
      passingData: {
        id_course: course.id,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result && result && result['certificate']) {
        course.certificate = result['certificate'];
        this.onFormChange();
      }
    });
  }

  getStatus(element : PersonalLeanring){
    var resultStatus = ""
    this.statusList.forEach(status => {
      if(status == element.status) resultStatus = status;
    });
    return resultStatus;
  }

  openDialog() {
    const _selectedCourseId: string[] = this.dataSource.data.map(
      (m) => m?.course_id
    );
    const dialogRef = this.dialogCommonService.onOpenCommonDialog({
      component: DialogLearningComponent,
      title: 'Assign Learning Course',
      icon: 'a-icon a-button__icon ui-ic-plus',
      width: '80vw',
      height: 'auto',
      maxWdith: '1300px',
      type: 'view',
      passingData: {
        selectedCourseId: _selectedCourseId,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.getAssignLearning();
      }
    });
  }

  openDetail(id: string) {
    const _dialog = this.dialogCommonService.onOpenCommonDialog({
      component: DialogDetailComponent,
      title: 'personal_learning.learning_detail.personal_detail',
      width: '800px',
      height: 'auto',
      icon: 'a-icon boschicon-bosch-ic-clipboard',
      type: 'view',
      passingData: {
        courseId: id,
      },
    });
  }

  view(certificate: string) {
    if (certificate) {
      const byteArray = new Uint8Array(
        atob(certificate)
          .split('')
          .map((char) => char.charCodeAt(0))
      );
      const blob = new Blob([byteArray], {
        type: 'application/pdf',
      });
      const url = window.URL.createObjectURL(blob);
      window.open(url);
    } else {
      this.dialogCommonService.onOpenConfirm({
        content: "You don't have certificate.",
        title: this.translate.instant('dialog.title_confirm'),
        btnConfirm: '',
        btnCancel: '',
        isShowOKButton: true,
        icon: 'a-icon ui-ic-alert-warning'
      })
    }
  }

  onDeleteCertificate(course: CourseModel) {
    const dialogRef = this.dialogCommonService.onOpenConfirm({
      content: 'Are you sure to delete this certificate ?',
      title: 'Confirm',
      btnConfirm: 'Yes',
      btnCancel: 'No',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.pesonalLearningService
          .deleteCourse(this.personalInfomationService._idUser, course.id)
          .subscribe((res) => {
            course.certificate = '';
            this.onFormChange();
          });
      }
    });
  }

  onStartDateChange(course: CourseModel) {
    const _startDate = new Date(course.start_date);
    const time = _startDate.setHours(
      _startDate.getHours() + Number(course.duration || 0)
    );
    course.end_date = Helpers.parseDateTimeToString(new Date(time));
    this.onFormChange();
  }

  onFormChange() {
    this.personalInfomationService.setPersonalInfoDetail({
      courses: this.dataSource.data,
    });
  }

  getAssignLearning() {
    const loader = this.loaderService.showProgressBar();
    this.pesonalLearningService
      .getPersonLearning(this.personalInfomationService._idUser)
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe((data) => {
        this.personalInfomationService.setPersonalInfoDetail({ courses: data });
      });
  }
}
