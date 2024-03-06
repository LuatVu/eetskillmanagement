import { Component, HostListener, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { BaseResponseModel } from 'projects/eet-core-demo/src/app/shared/models/base.model';
import { ReplaySubject, forkJoin, takeUntil } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { CONFIG } from '../../../shared/constants/config.constants';
import { GroupsModel, RoleModel, UserDistributionModel } from '../../../shared/models/group.model';
import { LoadingService } from '../../../shared/services/loading.service';
import { NotificationService } from '../../../shared/services/notification.service';
import { AddUserAndDistributionsComponent } from '../common/components/add-list-users-and-distributions/add-users-and-distributions.component';
import { CommonCreateItemDialogComponent } from '../common/components/common-create-item-dialog/common-create-item-dialog.component';
import { CommonDetailCopyComponent } from '../common/components/common-detail-copy/common-detail-copy.component';
import { CommonDetailComponent } from '../common/components/common-detail/common-detail.component';
import * as constants from '../common/constants/constants';
import { UserManagementService } from '../common/services/user-management.service';
import { DialogCommonService } from '../../../shared/services/dialog-common.service';

interface User {
  createdBy: string;
  displayName: string;
  distributionList: boolean;
  email: string;
  id: string;
  name: string;
  status: string;
  type: string;
}

@Component({
  selector: 'eet-users-and-groups',
  templateUrl: './users-and-groups.component.html',
  styleUrls: ['./users-and-groups.component.scss'],
})
export class UsersAndGroupsComponent implements OnInit, OnDestroy {
  @HostListener('window:resize', ['$event'])
  onResize(event: Event) {
    this.setHeightContentForClass('source-list', 100);
  }
  @ViewChild('detailForm') detailForm!: CommonDetailCopyComponent;
  public groupList: GroupsModel[] = [];
  public selectedGroup: GroupsModel | null | undefined;
  public userAndDistributionList: UserDistributionModel[] = [];
  public type = constants.TYPE.TYPE_GROUP;
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  public selectedItem: any;
  public selectedUser: any;

  constructor(
    public dialog: MatDialog,
    public dialogService: DialogCommonService,
    public translate: TranslateService,
    private userManagementService: UserManagementService,
    public loader: LoadingService,
    private notify: NotificationService
  ) {
  }

  ngOnInit() {
    this.setHeightContentForClass('source-list', 100);
    this.getGroupData();
  }

  getGroupData() {
    const loader = this.loader.showProgressBar();
    this.userManagementService
      .getAllGroups()
      .pipe(
        finalize(() => {
          this.loader.hideProgressBar(loader);
        }),
        takeUntil(this.destroyed$)
      )
      .subscribe((res: BaseResponseModel) => {
        if (res.code == CONFIG.API_RESPONSE_STATUS.SUCCESS && res.data) {
          this.groupList = res?.data;
          if (this.groupList && this.groupList.length != 0) {
            this.groupList.map((element) => {
              element.id = element.groupId;
              element.name = element.groupName;
            });
          }
        } else {
          this.groupList = [];
        }
      });
  }

  onSelectItemInList(group: GroupsModel | null | undefined) {
    if(this.detailForm){
      this.detailForm.resetRoleInput();
    }
    this.selectedItem = group;
    if (!group) return;
    if (this.detailForm.showActionButton == true) this.detailForm.displayBtn = false;

    this.getGroupDetail(group.groupId);
  }

  setHeightContentForClass(className: string, number: number): any {
    let offsetHeight = window.innerHeight - 200;
    let col1 = document.getElementsByClassName(className);
    if (col1) {
      for (let i = 0; i <= col1.length; i++) {
        let temp = col1[i] as HTMLElement;
        if (temp) {
          temp.style.maxHeight = offsetHeight - number + 'px';
        }
      }
    }
  }

  openDialog(): void {
    const dialogRef = this.dialogService.onOpenCommonDialog({
      component: CommonCreateItemDialogComponent,
      height: 'auto',
      passingData: { type: this.type },
      icon:'a-icon boschicon-bosch-ic-add',
      width: constants.WIDTH_OF_CREATE_DIALOG,
      title: 'user_management.dialog.title_create_group',
      type: 'edit',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.status == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
        this.getGroupData();
      }
    });
  }

  openAddListUsersAndDistributionsDialog(): void {
    let btnName: string = '';
    this.translate
      .get('user_management.dialog.button_name.button_create')
      .subscribe((item) => {
        btnName = item;
      });
    const dialogRef = this.dialogService.onOpenCommonDialog({
      component: AddUserAndDistributionsComponent,
      title: 'user_management.dialog.title_list_users_and_distributions', icon: 'a-icon boschicon-bosch-ic-add',
      width:constants.WIDTH_OF_ADD_LIST_USER_DIALOG, height:'auto',
      type: 'edit',
      passingData: {
        groupId: this.selectedGroup?.groupId
      }
    }
    );

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.status == 'SUCCESS') {
        this.onSelectItemInList(this.selectedGroup);
      }
    });
  }

  onSave(formValue: any) {
    const loader = this.loader.showProgressBar();
    this.userManagementService.updateGroup(formValue)
    .pipe(
      finalize(() => { this.loader.hideProgressBar(loader) }),
      takeUntil(this.destroyed$)
    )
    .subscribe((response: BaseResponseModel) => {
      if(response.code != CONFIG.API_RESPONSE_STATUS.SUCCESS){
        this.notify.error(response.message);
        return
      }
      this.notify.success(this.translate.instant('user_management.dialog.edit_user_group_successfully'));
      if(formValue.roles?.length == 0){
        this.getGroupData();
        this.detailForm.showActionButton = false;
        return
      } else {
        this.userManagementService.addRolesGroup({ groupId: formValue.groupId, 
          roles: formValue.roles.map((m: RoleModel) => m.id) }).subscribe((response: BaseResponseModel) => {
            if (response.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
              this.getGroupData();
              this.detailForm.showActionButton = false;
              this.detailForm.resetRoleInput();
              this.detailForm.setOriginRoleList();
            } else {
              this.notify.error(response.message);
            }
        })
      }
    })
  }

  onDeleteItem(group: GroupsModel) {
    if (!group) return;
    const loader = this.loader.showProgressBar();
    this.userManagementService.deleteGroup(group.groupId)
      .pipe(
        finalize(() => {
          this.loader.hideProgressBar(loader);
        }),
        takeUntil(this.destroyed$)
      )
      .subscribe(
        (res: BaseResponseModel) => {
          if (res?.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
            this.getGroupData();
            this.detailForm.onResetForm();
            this.userAndDistributionList = [];
            this.selectedGroup = null;
            this.notify.success(this.translate.instant('user_management.dialog.delete_user_group_successfully'))
          }
        });
  }

  onDeleteUserOrDistributionItem(item: UserDistributionModel) {
    if (!this.selectedGroup) return;

    const data = {
      groupId: this.selectedGroup['groupId'],
      userId: item.id,
      // userType: item.isDistributionList ? CONFIG.USER_DISTRIBUTION_TYPES.DISTRIBUTION : CONFIG.USER_DISTRIBUTION_TYPES.USER
    }
    const loader = this.loader.showProgressBar();
    this.userManagementService.deleteUserGroup(data)
      .pipe(
        finalize(() => {
          this.loader.hideProgressBar(loader);
        }),
        takeUntil(this.destroyed$)
      )
      .subscribe(
        (res: BaseResponseModel) => {
          if (res?.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
            this.onSelectItemInList(this.selectedGroup);
          }
        });
  }

  onSelectItemUserAndDistribution(item: User) {
    if (!item) return;
    this.selectedUser = item;
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  handleCancleEdit(selectedItem:any) {
    this.getGroupDetail(selectedItem.groupId);
  }

  getGroupDetail(id: string |  number){
    const loader = this.loader.showProgressBar();
    this.userManagementService
    .getGroupDetailById(id)
    .pipe(
      finalize(() => {
        this.loader.hideProgressBar(loader);
      }),
      takeUntil(this.destroyed$)
    )
    .subscribe((res: any) => {
      if (res.code == CONFIG.API_RESPONSE_STATUS.SUCCESS && res.data) {
        this.selectedGroup = res.data;
        this.selectedGroup!.name = res?.data.groupName;
        this.userAndDistributionList = [...this.selectedGroup!.distributionlists.slice(), ... this.selectedGroup!.users.slice()]
      }
    });
  }
}


