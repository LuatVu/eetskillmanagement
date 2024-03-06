import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface CloseDialogResultModel {
  isConfirm: boolean;
  comment: string;
}


@Component({
  selector: 'eet-comment-dialog',
  templateUrl: './comment-dialog.component.html',
  styleUrls: ['./comment-dialog.component.scss']
})
export class CommentDialogComponent implements OnInit {
  public comment: string = '';
  public titleConfirm: string = '';

  constructor(@Inject(MAT_DIALOG_DATA) public dialogData: any,
    private dialogRef: MatDialogRef<CommentDialogComponent>) { }

  ngOnInit() {
    this.titleConfirm = this.dialogData['data']?.titleConfirm;
  }

  onSave(isConfirm: boolean) {
    this.dialogRef.close({
      isConfirm,
      comment: this.comment
    });
  }

}
