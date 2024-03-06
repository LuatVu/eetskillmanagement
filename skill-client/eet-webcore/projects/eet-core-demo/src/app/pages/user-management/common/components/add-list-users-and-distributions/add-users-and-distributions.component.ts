import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogRef
} from '@angular/material/dialog';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { BaseResponseModel } from 'projects/eet-core-demo/src/app/shared/models/base.model';
import { AddUserDistributionModel, LdapUserDistributionModel } from 'projects/eet-core-demo/src/app/shared/models/group.model';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { ReplaySubject, takeUntil } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { UserManagementService } from '../../services/user-management.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'eet-users-and-distributions',
  templateUrl: './add-users-and-distributions.component.html',
  styleUrls: ['./add-users-and-distributions.component.scss']
})
export class AddUserAndDistributionsComponent implements OnInit, OnDestroy {
  public dataForm: AddUserDistributionModel;
  public separatorKeysCodes: number[] = [ENTER, COMMA];
  public selectedType: 'User' | 'Distribution' = 'User';
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    public dialogRef: MatDialogRef<AddUserAndDistributionsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private userManagementService: UserManagementService,
    public loader: LoadingService,
    private notify: NotificationService,
    private translate:TranslateService
  ) {
    this.dataForm = {
      groupId: data?.data['groupId'],
      distributionLists: [],
      ldapUsers: []
    }
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  ngOnInit(): void {
  }

  close(): void {
    this.dialogRef.close();
  }

  onSave() {
    const loader = this.loader.showProgressBar();
    this.userManagementService.addUsers(this.dataForm)
      .pipe(
        finalize(() => {
          this.loader.hideProgressBar(loader);
        }),
        takeUntil(this.destroyed$)
      )
      .subscribe(
        (res: BaseResponseModel) => {
          if (res?.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
            this.dialogRef.close({
              status: CONFIG.API_RESPONSE_STATUS.SUCCESS,
            });
            this.notify.success(this.translate.instant('user_management.dialog.add_user_to_group_successfully'))
          }
        })
  }

  selectedOptionChange(data: LdapUserDistributionModel[], type: string) {
    if (type == CONFIG.USER_DISTRIBUTION_TYPES.USER) {
      const _ldapUser = data.map((element: LdapUserDistributionModel | any) => ({
        country: element?.country || '',
        city: element?.city || '',
        displayName: element?.displayName || '',
        sAmAccountName: element?.userId || element?.sAmAccountName || '',
        mail: element?.mail || element?.email || '',
        phonenumber: element?.phonenumber || '',
      }));
      this.dataForm = {
        ...this.dataForm,
        ldapUsers: _ldapUser
      }
    } else {
      const _ldapDistribution = data.map((element: LdapUserDistributionModel | any) => ({
        country: element?.country,
        city: element?.city,
        displayName: element?.displayName,
        email: element?.email,
        phonenumber: element?.phonenumber,
        name: element?.name
      }));
      this.dataForm = {
        ...this.dataForm,
        distributionLists: _ldapDistribution
      }
    }

    // if (type == CONFIG.USER_DISTRIBUTION_TYPES.USER) {
    //   this.dataForm.ldapUsers = data as LdapUserModel[]
    //   // const _ldapUser: LdapUserModel[] = data.map((element: LdapUserDistributionModel) => ({
    //   //   country: element['country'],
    //   //   city: element['city'],
    //   //   displayName: element['displayName'],
    //   //   sAmAccountName: element['sAmAccountName'],
    //   //   mail: element['mail'],
    //   //   phonenumber: element['phonenumber'],
    //   // }));
    //   // this.dataForm = {
    //   //   ...this.dataForm,
    //   //   ldapUsers: _ldapUser
    //   // }
    // } else {
    //   this.dataForm = {
    //     ...this.dataForm,
    //     distributionLists: data as LdapDistributionModel[]
    //   }
    // }
  }

}

