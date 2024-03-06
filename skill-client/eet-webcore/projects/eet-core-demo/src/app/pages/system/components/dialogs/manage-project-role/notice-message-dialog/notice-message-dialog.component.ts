import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'eet-notice-message-dialog',
  templateUrl: './notice-message-dialog.component.html',
  styleUrls: ['./notice-message-dialog.component.scss']
})
export class NoticeMessageDialogComponent implements OnInit {
  public error_message: string
  constructor(public dialogRef: MatDialogRef<NoticeMessageDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) {
    this.error_message = data.data.message
  }

  ngOnInit(): void {
  }

}
