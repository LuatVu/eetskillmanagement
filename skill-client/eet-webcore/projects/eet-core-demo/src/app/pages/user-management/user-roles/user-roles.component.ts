import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { AfterViewInit, Component, HostListener, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { finalize } from 'rxjs';
import { CONFIG } from '../../../shared/constants/config.constants';
import { BaseResponseModel } from '../../../shared/models/base.model';
import { PermissionModel } from '../../../shared/models/group.model';
import { LoadingService } from '../../../shared/services/loading.service';
import { NotificationService } from '../../../shared/services/notification.service';
import { CommonCreateItemDialogComponent } from '../common/components/common-create-item-dialog/common-create-item-dialog.component';
import { CommonDetailCopyComponent } from '../common/components/common-detail-copy/common-detail-copy.component';
import * as constants from '../common/constants/constants';
import { RoleModel } from '../common/model/user-management.model';
import { UserManagementService } from '../common/services/user-management.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'eet-user-roles',
  templateUrl: './user-roles.component.html',
  styleUrls: ['./user-roles.component.scss']
})
export class UserRolesComponent implements OnInit, AfterViewInit {
  @ViewChild('detailForm') detailForm!: CommonDetailCopyComponent;
  @HostListener("window:resize", ["$event"])
  onResize(event: Event) {
    this.setHeightContentForClass('source-list', 100);
    this.setHeightContentForClass('permissions-list', 65);
  }

  public sourceList: any[] = [];
  public displayBtn: boolean = false;
  public permissions: any[] = [];
  public data: any;
  public readonly constant = constants;
  public allAccept: boolean = false;
  public type = constants.TYPE.TYPE_ROLE;
  public selectedItem: any;
  public name: string = "";
  public description: string = "";
  public isUpdated: boolean = false;
  public statusDelete: string = "";
  private permissionIds: string[] = [];
  private permissionDto: any[] = [];
  private detailPermission: any[] = [];
  private detailPermissionCategory: any[] = [];
  public selectedOption: any;

  constructor(
    public dialog: MatDialog,
    private dialogService: DialogCommonService,
    private userManagementService: UserManagementService,
    private loader: LoadingService,
    private notify: NotificationService,
    private translate:TranslateService
  ) { }
  ngAfterViewInit(): void {
    this.getAllRole();
    this.getAllPermissions();
  }

  ngOnInit(): void {
    this.setHeightContentForClass('source-list', 100);
    this.setHeightContentForClass('permissions-list', 60);
  }

  getAllRole() {
    this.userManagementService.getAllRoles().subscribe(
      (res: BaseResponseModel) => {
        this.sourceList = res?.data || [];
      });
  }

  getAllPermissions() {
    this.userManagementService.getAllPermissions().subscribe(
      (res: BaseResponseModel) => {
        this.permissions = res.data.permissionCategoryDTOs || [];
        this.permissionDto = res?.data.permissionDTOs || [];

      });
  }

  permissionByRole(role_id: string): any[] {
    return this.permissionDto.filter(f => f.permissionCategoriesId === role_id);
  }

  onSelectItemInList(item: any) {
    this.selectedOption = item;
    this.userManagementService.getRoleDetailById(item.id).subscribe(
      (res: BaseResponseModel) => {
        this.permissions.filter((permission: any) => {
          permission.accepted = false;
        })
        this.permissionDto.filter((permission: any) => {
          permission.accepted = false;
        })
        this.selectedItem = res.data;
        this.detailPermission = item.permissionDTOs;
        this.detailPermissionCategory = item.permissionCategoryDTOs;
        this.detailPermission.filter((permission: any) => {
          permission.accepted = false;
        })
        this.detailPermissionCategory.filter((permission: any) => {
          permission.accepted = false;
        })
        this.onMapingDetailToListPermision();
      });
  }

  private onMapingDetailToListPermision() {
    this.detailPermissionCategory.forEach((details: any) => {
      this.permissions.filter((permission: any) => {
        if (permission.id === details.id) {
          this.compareSubpermission(permission);
        }

      })
    })
    this.detailPermission.forEach((details: any) => {
      if (details.belongsToRole == true) {
        const filteredPermission = this.permissionByRole(details.permissionCategoriesId as string)
        filteredPermission.filter((permission: any) => {
          if (permission.id === details.id) permission.accepted = true;

        })
      }
    })
  }

  setHeightContentForClass(className: string, number: number): any {
    let offsetHeight = window.innerHeight - 200;
    let col1 = document.getElementsByClassName(className);
    if (col1) {
      for (let i = 0; i <= col1.length; i++) {
        let temp = col1[i] as HTMLElement;
        if (temp) {
          temp.style.maxHeight = (offsetHeight - number) + "px";
        }
      }
    }
  }

  updateAllAccept(accepted: boolean, subPermission: PermissionModel, permission: PermissionModel) {
    if (this.selectedItem) this.displayBtn = true;
    if (accepted) {
      subPermission.accepted = true;
    } else {
      permission.accepted = false;
      subPermission.accepted = false;
    }
    this.compareSubpermission(permission);
  }

  private compareSubpermission(permission: PermissionModel) {
    const allSubpermission = this.permissionByRole(permission.id as string)
    const acceptedSubpermission = allSubpermission.filter(f => f.accepted === true)
    if (allSubpermission.length === acceptedSubpermission.length) {
      permission.accepted = true;
    } else {
      permission.accepted = false;
    }
  }

  someAccept(permission: PermissionModel): boolean {
    let permissionFiltered = this.permissionByRole(permission?.id as string);
    return permission.accepted = true ? permissionFiltered.every(p => p.accepted == true) : false;
  }

  setAll(accepted: boolean, permission: PermissionModel) {
    if (this.selectedItem) this.displayBtn = true;
    if (permission == null) {
      return;
    }
    permission.accepted = accepted;
    let permissionFiltered = this.permissionByRole(permission?.id as string);
    if (accepted) {
      permissionFiltered.forEach((item: any) => {
        item.accepted = true;
      })
    } else {
      permissionFiltered.forEach((item: any) => {
        item.accepted = false;
      })
    }
  }

  openDialog(): void {
    const dialogRef = this.dialogService.onOpenCommonDialog({
      component: CommonCreateItemDialogComponent,
      height:'auto',
      icon:'a-icon boschicon-bosch-ic-add',
      width: constants.WIDTH_OF_CREATE_DIALOG,
      title: 'user_management.dialog.title_create_role',
      type: this.type,
      passingData: { name: this.name, description: this.description, type: this.type },
    });

    dialogRef.afterClosed().subscribe(result => {
      this.getAllRole();
    });
  }

  onSave(formValue: any) {
    const loader = this.loader.showProgressBar()
    this.userManagementService.updateRole(formValue)
      .pipe(finalize(() => this.loader.hideProgressBar(loader)))
      .subscribe((res: BaseResponseModel) => {
        if (res.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.getAllRole();
          this.detailForm.showActionButton = false;
        } else {
          this.notify.error(res.message);
        }
      })
  }

  onDisplayButton() {
    this.displayBtn = true;
  }

  onRemoveRole(role: RoleModel) {
    if (!role) return;
    const loader = this.loader.showProgressBar();
    this.userManagementService.deleteRole(role.id)
      .subscribe(
        (res: BaseResponseModel) => {
          if (res?.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
            this.loader.hideProgressBar(loader);
            this.getAllRole();
            this.detailForm.onResetForm();
            this.notify.success(this.translate.instant('user_management.dialog.delete_role_successfully'))
          }
        });
  }

  onEdit() {
    this.selectedItem = this.detailForm.detailForm.value;
    this.permissionDto.forEach((item: any) => {
      if (item.accepted == true) {
        this.permissionIds.push(item.id)
      }
    })
    this.selectedItem = Object.assign({ permissionIds: this.permissionIds }, this.selectedItem);
    const loader = this.loader.showProgressBar()
    this.userManagementService.updateRole(this.selectedItem)
      .pipe(finalize(() => this.loader.hideProgressBar(loader)))
      .subscribe((res: BaseResponseModel) => {
        if (res.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.getAllRole();
          this.displayBtn = false;
          this.permissionIds = [];
          this.notify.success(this.translate.instant('user_management.dialog.edit_role_successfully'))
        } else {
          this.notify.error(res.message);
        }
      })
  }

  onCancel() {
    this.displayBtn = false;
    this.detailForm.onResetForm();
    this.onSelectItemInList(this.selectedOption)
  }

}
