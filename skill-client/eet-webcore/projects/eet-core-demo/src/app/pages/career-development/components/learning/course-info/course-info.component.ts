import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { TOKEN_KEY } from '@core/src/lib/shared/util/system.constant';
import { finalize } from 'rxjs/operators';
import { CommonIdentifier, CourseDetail, LEARNING_PERMISSIONS, OrderBy } from '../my-learning/model/my-learning.model';
import { MyLearningService } from '../my-learning/my-learning.service';
import { CourseRegisterComponent } from './course-register/course-register.component';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';

@Component({
  selector: 'eet-course-info',
  templateUrl: './course-info.component.html',
  styleUrls: ['./course-info.component.scss'],
})
export class CourseInfoDialogComponent implements OnInit {
  public infoData: CourseDetail = {
    id: '',
    name: '',
    categoryName: '',
    trainer: '',
    duration: 0,
    date: new Date(),
    status: 'New',
    description: '',
    course_id: '',
    course_type: '',
    target_audience: ''
  };
  public typeOfCommand: 'add' | 'edit' | 'view' = 'view'

  private targets = new FormControl();
  private types = new FormControl();
  private categories = new FormControl();

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialog: MatDialog,
    private dialogCommonService: DialogCommonService,
    public courseService: MyLearningService,
    public dialogRef: MatDialogRef<CourseInfoDialogComponent>,
    private formBuilder: FormBuilder,
    public comLoader: LoadingService
  ) {
    this.typeOfCommand = data.data.type;
  }
  public targetList: OrderBy[] = []
  public typeList: OrderBy[] = CONFIG.LEARNING_COURSE_TYPE.concat()
  public categoryList: OrderBy[] = []
  public statusList: OrderBy[] = CONFIG.LEARNING_COURSE_STATUS.concat()
  
  ngOnInit(): void {
    const loader = this.comLoader.showProgressBar()
    this.courseService.getCourseDetails(this.data.data.id).pipe(finalize(() => this.comLoader.hideProgressBar(loader))).subscribe((response => {
      this.infoData = response as CourseDetail
      if (this.typeOfCommand === 'edit') {
        let finishedGetCategory = false
        let finishedGetLevel = false
        this.courseService.getLevel().pipe(finalize(() => {
          finishedGetLevel = true
          if (finishedGetLevel && finishedGetCategory) {
            this.comLoader.hideProgressBar(loader)
          }
        })).subscribe((response: any) => {
          this.targetList = (response.data as CommonIdentifier[]).map(({ id, name }) => ({ value: id, viewValue: name }))
          const targetValue = this.targetList.filter((targetData) => {
            return targetData.viewValue === this.infoData.target_audience
          })
          if (targetValue.length > 0) {
            this.infoData.target_audience = targetValue[0].value
          }
        })
        this.courseService.getCategory().pipe(finalize(() => {
          finishedGetCategory = true
          if (finishedGetLevel && finishedGetCategory) {
            this.comLoader.hideProgressBar(loader)
          }
        })).subscribe((response: any) => {
          this.categoryList = (response['data'] as CommonIdentifier[]).map(({ id, name }) => ({ value: id, viewValue: name }))
          const categoryValue = this.categoryList.filter((categoryData) => {
            return categoryData.viewValue === this.infoData.categoryName
          })
          if (categoryValue.length > 0) {
            this.infoData.categoryName = categoryValue[0].value
          }
        })
      }
    }))

  }
  
  canRegister() {
    const localStoragePerms = JSON.parse(localStorage.getItem(TOKEN_KEY) || "{\"permissions\":[]}").permissions
    const assignPermission = localStoragePerms.filter((val: any) => {
      return val.code === LEARNING_PERMISSIONS.PERMISSIONS.ASSIGN
    })
    return assignPermission.length > 0 ? true : false
  }
  
  onRegister() {
    this.dialogCommonService.onOpenCommonDialog({
      component: CourseRegisterComponent,
      title: 'learning.my_learning.register.title',
      width: '800px',
      height: 'auto',
      type: 'edit',
      passingData: {
        data: this.infoData
      }
    })
  }

  onSave() {
    const loader = this.comLoader.showProgressBar()
    this.courseService.updateCourse(this.infoData.id, this.infoData).pipe(
      finalize(() => {
        this.comLoader.hideProgressBar(loader)
      })
    ).subscribe((resp) => {
      this.dialogRef.close(true)
    }, (error) => {
      this.dialogRef.close()
    })
  }

  ngAfterViewInit() { }
  addCertificate() { }
}
