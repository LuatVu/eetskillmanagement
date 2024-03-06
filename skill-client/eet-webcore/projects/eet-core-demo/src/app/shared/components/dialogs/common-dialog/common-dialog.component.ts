import { Component, Inject, Input, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CommonDialogModel } from '../../../models/confirm-dialog.model';
import { CanDeactivateGuard } from '../../../utils/can-deactivate.guard';

@Component({
  selector: 'eet-common-dialog',
  templateUrl: './common-dialog.component.html',
  styleUrls: ['./common-dialog.component.scss']
})
export class CommonDialogComponent implements OnInit, CanDeactivateGuard {
  @Input() isShowCloseButton: boolean = true;

  constructor(private dialogRef: MatDialogRef<CommonDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: CommonDialogModel) { }

  ngOnInit() {
  }

  onClose() {
    if (this.data.type === 'edit') {
      if (this.canDeactivate() == true) this.dialogRef.close();
    } else {
      this.dialogRef.close();
    }
  }

  canDeactivate(): boolean | Promise<boolean> | import('rxjs').Observable<boolean> {
    return confirm('Do you want to cancel? This action cannot be reverted.')
  }

}
