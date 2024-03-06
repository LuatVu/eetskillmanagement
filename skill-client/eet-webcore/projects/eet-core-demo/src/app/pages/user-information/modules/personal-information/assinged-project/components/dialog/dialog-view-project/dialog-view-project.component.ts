import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ProjectModel } from '../../../../personal-infomation.model';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
@Component({
  selector: 'app-dialog-view-project',
  templateUrl: './dialog-view-project.component.html',
  styleUrls: ['./dialog-view-project.component.scss']
})
export class DialogViewProjectComponent implements OnInit {

  public projectDetail: ProjectModel;
  public tasksDisplay: string = ''
  public skillTagsDisplay: string = ''
  constructor(@Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
    this.projectDetail = this.data['data'];
    if (this.projectDetail) {
      this.getTasksToDisplay()
      this.getSkillTagToDisplay()
    }
  }

  getTasksToDisplay() {
    if (this.projectDetail.project_type === CONFIG.PROJECT.PROJECT_TYPE.NON_BOSCH) {
      this.tasksDisplay = this.projectDetail.additional_tasks
    } else {
      if (this.projectDetail.additional_tasks) {
        this.projectDetail.tasks?.length > 0 ? this.tasksDisplay += this.projectDetail.additional_tasks + ', ' : this.tasksDisplay += this.projectDetail.additional_tasks
      }
      this.projectDetail.tasks?.length > 0 && this.projectDetail.tasks.forEach((e, i) => {
        i === this.projectDetail.tasks.length - 1 ? this.tasksDisplay += e.name : this.tasksDisplay += e.name + ', '
      })
    }
  }

  getSkillTagToDisplay() {
    this.skillTagsDisplay = this.projectDetail.skill_tags?.map((e) => e.name).join(', ')
  }
}
