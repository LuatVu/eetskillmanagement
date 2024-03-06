import { Component, OnInit, ViewChild } from '@angular/core';
import { BaseResponseModel } from '../../../shared/models/base.model';
import { PermissionModel } from '../../../shared/models/group.model';
import * as constants from '../common/constants/constants';
import { UserManagementService } from '../common/services/user-management.service';
import { LoadingService } from '../../../shared/services/loading.service';
import { NotificationService } from '../../../shared/services/notification.service';
import { finalize } from 'rxjs/operators';
import { CONFIG } from '../../../shared/constants/config.constants';
import { CommonDetailCopyComponent } from '../common/components/common-detail-copy/common-detail-copy.component';
import { Helpers } from '../../../shared/utils/helper';
@Component({
  selector: 'eet-user-permision',
  templateUrl: './user-permision.component.html',
  styleUrls: ['./user-permision.component.scss']
})
export class UserPermisionComponent implements OnInit {
  @ViewChild('detailForm') detailForm!: CommonDetailCopyComponent;
  public type = constants.TYPE.TYPE_PERMISSION;
  public permissions: PermissionModel[] = [];
  public roleWithPermissions = new Map<String, any[]>();
  private permissionDto: any[] = [];
  public selectedItem: any;
  constructor(
    private userManagementService: UserManagementService,
    private loader: LoadingService,
    private notify: NotificationService
    ) { }

  ngOnInit(): void {
    this.getPermissionData()
  }

  onSelectItemInList(item: any) {
    this.selectedItem = item;
  }

  getPermissionData(){
    this.userManagementService.getAllPermissions().subscribe(
      (res: BaseResponseModel) => {
        this.permissions = res.data.permissionCategoryDTOs || [];
        this.permissionDto = res?.data.permissionDTOs || [];
        if(this.selectedItem){
          this.selectedItem = this.permissionDto.find(p => p.id === this.selectedItem.id);
        }
        this.permissions.forEach((role: any) => {
          this.roleWithPermissions.set(role.id, this.permissionDto.filter(f => f.permissionCategoriesId === role.id));
        });
      });
  }

  onSave(formValue: any) {
    const loader = this.loader.showProgressBar();
    this.userManagementService.updatePermission(formValue)
    .pipe(
      finalize(() => { this.loader.hideProgressBar(loader) }),
    )
    .subscribe((response: BaseResponseModel) => {
      if(response.code == CONFIG.API_RESPONSE_STATUS.SUCCESS){
        this.getPermissionData();
        this.detailForm.showActionButton = false;
      }else {
        this.notify.error(response.message);
      }
    })
  }
  handleCancel(selectedItem:any) {
    this.selectedItem=Helpers.cloneDeep(selectedItem);
  }
}