import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../components/dialogs/confirm-dialog/confirm-dialog.component';
import { CommonDialogModel, ConfirmDialogModel } from '../models/confirm-dialog.model';

@Injectable({
  providedIn: 'root'
})
export class DialogCommonService {

  constructor(private dialog: MatDialog) { }

  onOpenConfirm(data: ConfirmDialogModel) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent,
      {
        width: '500px',
        data
      }
    );
    return dialogRef;
  }

  onOpenCommonDialog(data: CommonDialogModel) {
    const dialogRef = this.dialog.open(data.component,
      {
        width: data.width,
        height: data.height,
        maxWidth: data.maxWdith,
        disableClose: true,
        data: {
          title: data.title,
          icon: data.icon,
          data: data.passingData,
          type: data.type
        }
      });
    return dialogRef;
  }

  toggleCloseAllDialog() {
    this.dialog.closeAll();
  }

  closeDialogById(dialogId: string){
    this.dialog.getDialogById(dialogId)?.close();
  }

}
