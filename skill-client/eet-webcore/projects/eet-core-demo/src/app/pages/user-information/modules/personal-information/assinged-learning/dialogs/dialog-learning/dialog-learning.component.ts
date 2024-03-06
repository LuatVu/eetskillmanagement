import {
  animate,
  state,
  style,
  transition,
  trigger
} from '@angular/animations';
import { AfterViewInit, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { finalize } from 'rxjs/operators';
import { PersonalInfomationService } from '../../../personal-infomation.service';
import { CourseService } from './course.service';


export interface Course {
  name: string;
  trainer: string;
  category: string;
  duration: string;
  date: string;
  course_type: string;
  description: string;
  id: string;
  ischecked: boolean;
  course_id: string;
}
@Component({
  selector: 'eet-dialog-learning',
  templateUrl: './dialog-learning.component.html',
  styleUrls: ['./dialog-learning.component.scss'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '*' })),
      transition(
        'expanded <=> collapsed',
        animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')
      ),
    ]),
  ],
})
export class DialogLearningComponent implements OnInit, AfterViewInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  private seflStudy = 'Self Study';
  private typeOf = this.data.typeofUser;
  public columnsToDisplay: string[] = [
    'Cource',
    'Trainer',
    'Category',
    'Duration',
    'StartDay',
    'courceType',
    'id',
  ];
  private courses!: Course[];
  public expandedElement!: Course;
  public dataSource = new MatTableDataSource<Course>(this.courses);
  private columnsToDisplayWithExpand = [...this.columnsToDisplay, 'expand'];

  public readonly paginationSize = CONFIG.PAGINATION_OPTIONS;

  constructor(
    private courseService: CourseService,
    public dialogRef: MatDialogRef<DialogLearningComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private personalInfomationService: PersonalInfomationService,
    private loaderService: LoadingService
  ) {
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
  }

  ngOnInit(): void {
    this.getCourseList();
  }

  configCourseList() {
    if (this.typeOf !== CONFIG.TYPE_USER.ASSOCIATE) {
      this.courses = this.courses.filter(
        (u: Course) => u.course_type == this.seflStudy
      );
    }
    const _selectedCourse: string[] = this.data['data']?.selectedCourseId;

    this.courses = this.courses.filter(f => !_selectedCourse.find(selectedCourse => selectedCourse == f.id)).slice()

    this.dataSource = new MatTableDataSource(this.courses);
    this.dataSource.paginator = this.paginator;
  }

  getCourseList() {
    const loader = this.loaderService.showProgressBar();
    this.courseService.getListCourse()
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe((data) => {
        this.courses = data;
        this.configCourseList();
      })
  }

  addCourse() {
    const loader = this.loaderService.showProgressBar();
    const _courseIds: string[] = this.dataSource.data.filter(f => f.ischecked).map(m => m?.id)
    this.courseService.addCourse(this.personalInfomationService._idUser, _courseIds)
      .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
      .subscribe(data => {
        if (data) {
          this.dialogRef.close(true);
        }
      })
  }
}
