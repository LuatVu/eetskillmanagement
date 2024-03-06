import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MyLearningComponent } from '../my-learning/my-learning.component';
import { CourseInformation } from '../my-learning/model/my-learning.model';
import { NotificationService } from '@bci-web-core/core';
@Component({
  selector: 'eet-delete-prompt',
  templateUrl: './delete-prompt.component.html',
  styleUrls: ['./delete-prompt.component.scss'],
})
export class DeletePromptComponent implements OnInit {
  public name: string;
  private courseInfoList!: CourseInformation[];

  constructor(@Inject(MAT_DIALOG_DATA) public data: string, private dialogRef: MatDialogRef<DeletePromptComponent>, private notifyService: NotificationService ) {
    this.name = data;
  }

  deleteConfirm(isConfirm : boolean){
    if ( isConfirm) {
      this.notifyService.success("Delete success");
      this.dialogRef.close(isConfirm);}
      else{
        this.dialogRef.close()
      }
    }


  ngOnInit(): void {}
}
