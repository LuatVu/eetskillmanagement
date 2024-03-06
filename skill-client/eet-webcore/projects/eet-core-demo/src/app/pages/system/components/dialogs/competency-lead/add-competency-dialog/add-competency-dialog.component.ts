import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'eet-add-competency-dialog',
  templateUrl: './add-competency-dialog.component.html',
  styleUrls: ['./add-competency-dialog.component.scss']
})
export class AddCompetencyDialogComponent implements OnInit {

  public competencyName: FormControl = new FormControl(null, [Validators.required])
  constructor(@Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<AddCompetencyDialogComponent>
  ) {

   }

  ngOnInit(): void {
  }

  onConfirm() {
    this.dialogRef.close(this.competencyName.value)
  }


}
