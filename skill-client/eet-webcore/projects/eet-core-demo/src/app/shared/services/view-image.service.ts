import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ViewImageComponent } from '../components/dialogs/view-image/view-image.component';

@Injectable(
  {
    providedIn: 'platform'
  }
)
export class ViewImageService {

  constructor(private dialog: MatDialog) { }

  onViewImage(src: string) {
    this.dialog.open(ViewImageComponent,
      {
        maxWidth: '100vw',
        maxHeight: '100vh',
        height: '100%',
        width: '100%',
        panelClass: 'image-view-box',
        data: src
      })
  }

}
