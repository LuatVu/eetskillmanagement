import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-view-image',
  templateUrl: './view-image.component.html',
  styleUrls: ['./view-image.component.scss']
})
export class ViewImageComponent implements OnInit {
  private countOutsideClick: number = -1;

  constructor(private dialogRef: MatDialogRef<ViewImageComponent>,
    @Inject(MAT_DIALOG_DATA) public data: string) { }

  ngOnInit() {

  }

  onOutsideClick() {
    this.countOutsideClick++;
    if (this.countOutsideClick == 1) {
      this.dialogRef.close();
    }
  }

}
