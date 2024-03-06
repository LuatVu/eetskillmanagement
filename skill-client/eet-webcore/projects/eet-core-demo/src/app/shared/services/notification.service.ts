import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarRef, MatSnackBarVerticalPosition, TextOnlySnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private matSnackBar: MatSnackBar) { }

  success(message: string, action?: string, xPos?: MatSnackBarHorizontalPosition, yPos?: MatSnackBarVerticalPosition, duration?: number): MatSnackBarRef<TextOnlySnackBar> {
    const _notifyRef = this.matSnackBar.open(message, action || undefined,
      { horizontalPosition: xPos || 'center', verticalPosition: yPos || 'top', panelClass: ['notifi-common', 'success-noti'], duration: duration || 3000 });
    return _notifyRef;
  }

  error(message: string, action?: string, xPos?: MatSnackBarHorizontalPosition, yPos?: MatSnackBarVerticalPosition, duration?: number): MatSnackBarRef<TextOnlySnackBar> {
    const _notifyRef = this.matSnackBar.open(message, action || undefined,
      { horizontalPosition: xPos || 'center', verticalPosition: yPos || 'top', panelClass: ['notifi-common', 'error-noti'], duration: duration || 3000 });
    return _notifyRef;
  }

  warning(message: string, action?: string, xPos?: MatSnackBarHorizontalPosition, yPos?: MatSnackBarVerticalPosition, duration?: number): MatSnackBarRef<TextOnlySnackBar> {
    const _notifyRef = this.matSnackBar.open(message, action || undefined,
      { horizontalPosition: xPos || 'center', verticalPosition: yPos || 'top', panelClass: ['notifi-common', 'warning-noti'], duration: duration || 3000 });
    return _notifyRef;
  }

  information(message: string, action?: string, xPos?: MatSnackBarHorizontalPosition, yPos?: MatSnackBarVerticalPosition, duration?: number): MatSnackBarRef<TextOnlySnackBar> {
    const _notifyRef = this.matSnackBar.open(message, action || undefined,
      { horizontalPosition: xPos || 'center', verticalPosition: yPos || 'top', panelClass: ['notifi-common', 'info-noti'], duration: duration || 3000 });
    return _notifyRef;
  }
}
