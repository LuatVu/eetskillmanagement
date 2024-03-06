import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ChildVModel } from '../../v-model.model';

export interface CloseDialogResultModel {
  isConfirm: boolean;
  comment: string;
}


@Component({
  selector: 'eet-list-project-dialog',
  templateUrl: './list-project-dialog.component.html',
  styleUrls: ['./list-project-dialog.component.scss']
})
export class ListProjectDialogComponent implements OnInit {
  public listProject:ChildVModel[] = this.dialogData.data?.phase?.children
  public filterListProject:ChildVModel[] = this.dialogData.data?.phase?.children
  constructor(@Inject(MAT_DIALOG_DATA) public dialogData: any,
    private dialogRef: MatDialogRef<ListProjectDialogComponent>) { }

  ngOnInit() {
  }
  
  onClickProject(project:ChildVModel) {
    this.dialogData.data.onClickProject(project)
  }
  
  onSearch(event: any) {
    const searchValue = (event.target as HTMLInputElement).value
    if (searchValue.length && searchValue.length !== 0) {
      this.filterListProject = this.filterByName(searchValue,this.listProject)
    }
    else {
      this.filterListProject = (JSON.parse(JSON.stringify(this.listProject)) as ChildVModel[])
    }
  }

  filterByName(searchValue:string,data: ChildVModel[]) {
    return (JSON.parse(JSON.stringify(data)) as ChildVModel[]).filter(
      filterValue => filterValue.label.toLowerCase().trim().includes(searchValue.toLowerCase().trim())
    )
  }

}
