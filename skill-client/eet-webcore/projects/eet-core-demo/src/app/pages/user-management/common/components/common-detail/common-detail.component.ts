import {
  Component,
  Inject,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { DialogData } from '../../model/dialog-data.model';

@Component({
  selector: 'eet-common-detail',
  templateUrl: './common-detail.component.html',
  styleUrls: ['./common-detail.component.scss'],
})
export class CommonDetailComponent implements OnInit {
  public sourceList: any[] = [];
  public keyword: string = '';
  public tittle: string = ''
  public dataUser: any;
  public dataApi: any = {
    "status": "Success",
    "message": "Remove role to Group 06 successfully",
    "data": {
      "groupId": 8,
      "groupName": "Group 06",
      "description": "Group 06",
      "status": "Active",
      "users": [

        { "Id": 1, "displayName": "User01(MS/EE12)" },
        { "Id": 2, "displayName": "User02(MS/EE12)" }

      ],
      "distributionLists": null,
      "roles": null,
      "timestamp": "2022-06-14T19:14:42.5660595+07:00",
      "code": ""
    }
  }
  constructor(
    public dialog: MatDialog,
    public loader: LoadingService,
    public dialogRef: MatDialogRef<CommonDetailComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {
    this.dataUser = data;
    this.tittle = this.dataUser.item.displayName;
    this.sourceList = this.dataApi.data.users;
  }

  ngOnInit() {
  }


  searchKeyword() {

  }

  ngOnChanges(changes: SimpleChanges) {

  }

  close() {
    this.dialogRef.close();
  }

}
