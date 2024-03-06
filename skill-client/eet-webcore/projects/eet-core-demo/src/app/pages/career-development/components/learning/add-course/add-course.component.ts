import { formatDate } from '@angular/common';
import { Component, Inject, LOCALE_ID, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { finalize } from 'rxjs';

import { CommonIdentifier, CourseInformation, OrderBy } from '../my-learning/model/my-learning.model';
import { MyLearningService } from '../my-learning/my-learning.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from '@bci-web-core/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
@Component({
  selector: 'eet-add-course',
  templateUrl: './add-course.component.html',
  styleUrls: ['./add-course.component.scss'],
})
export class AddCourseDialogComponent implements OnInit {
  // For dropdowns
  public currentSelected: string;
  public name: string = ""
  public trainer: string = ""
  public date: string = ""
  public duration: string = ""
  public types: string = ""
  public categories: string = ""
  public description: string = ""
  public targets: string = ""

  public targetList: OrderBy[] = []
  public typeList: string[] = []
  public categoryList: OrderBy[] = []
  public durationUnit: 'h' | 'd' = 'h'
  private infoData: CourseInformation;
  private typeOfCommand: 'add' | 'edit' | 'view';

  public minDate: Date;

  constructor(

    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<AddCourseDialogComponent>,
    public comLoader: LoadingService,
    public learningService: MyLearningService,
    public notify: NotificationService,
    @Inject(LOCALE_ID) private locale: string
  ) {
    this.currentSelected = '';
    this.infoData = data.data;
    this.typeOfCommand = data.type;
    for (let i of CONFIG.LEARNING_COURSE_TYPE.concat()) {
      this.typeList.push(i.value)
    }

    this.minDate = new Date();
  }


  ngOnInit(): void {
    const loader = this.comLoader.showProgressBar()
    let finishedGetLevel: boolean = false;
    let finishedGetCategory: boolean = false;
    this.learningService.getLevel().subscribe((response: any) => {
      this.targetList = (response.data as CommonIdentifier[]).map(({ id, name }) => ({ value: id, viewValue: name }))
      finishedGetLevel = true
      if (finishedGetLevel && finishedGetCategory) {
        this.comLoader.hideProgressBar(loader)
      }
    })
    this.learningService.getCategory().subscribe((response: any) => {
      this.categoryList = (response.data as CommonIdentifier[]).map(({ id, name }) => ({ value: id, viewValue: name }))
      finishedGetCategory = true
      if (finishedGetLevel && finishedGetCategory) {
        this.comLoader.hideProgressBar(loader)
      }
    })
  }

  isMeetRequired = false;
  onFieldChange() {
    if (this.name != "" &&
      this.date != "" &&
      this.types != "" &&
      this.targets != "" &&
      this.duration != "" &&
      this.duration != null &&
      this.categories != "") {
      this.isMeetRequired = true;
    } else {
      this.isMeetRequired = false;
    }
  }

  onSave() {
    if (this.durationUnit === 'd') {
      const totalTime = parseInt(this.duration) * 24
      this.duration = totalTime.toString()
    }
    const loader = this.comLoader.showProgressBar()
    this.learningService.postCourse({
      name: this.name,
      category: this.categories,
      trainer: this.trainer,
      duration: parseInt(this.duration),
      date: formatDate(this.date, 'yyyy-MM-dd', this.locale),
      status: 'NEW',
      description: this.description,
      course_type: this.types,
      target_audience: this.targets
    }).pipe(finalize(() => {
      this.comLoader.hideProgressBar(loader)
    })).subscribe((response: any) => {
      if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
        this.dialogRef.close(true)
        this.notify.success("Add course successfully")
      }
      else {
        this.notify.error(response.message || "Error during adding course, please try again!")
      }
    })
  }
}
