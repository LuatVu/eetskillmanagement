import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, ElementRef, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { BaseResponseModel } from 'projects/eet-core-demo/src/app/shared/models/base.model';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { debounceTime, finalize, startWith } from 'rxjs/operators';
import { GroupsModel, RoleModel } from '../../../../../shared/models/group.model';
import { UserManagementService } from '../../services/user-management.service';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';

@Component({
  selector: 'eet-common-detail-copy',
  templateUrl: './common-detail-copy.component.html',
  styleUrls: ['./common-detail-copy.component.scss']
})
export class CommonDetailCopyComponent implements OnInit {
  @Output() onFormSave: EventEmitter<GroupsModel> = new EventEmitter<GroupsModel>();
  @Input() formType: 'Group' | 'Role' | 'Permission' = 'Group';
  @Input() selectedItem: GroupsModel | null | undefined;
  @Output() onDisplayBtn: EventEmitter<any> = new EventEmitter<any>();
  @Output() onCancle: EventEmitter<any> = new EventEmitter<any>();
  public detailForm: FormGroup = new FormGroup({});
  public roleList: RoleModel[] = [];
  public selectedRoleList: RoleModel[] = [];
  public originRoleList: RoleModel[] = [];
  public filterRoleList: RoleModel[] = [];
  public separatorKeysCodes: number[] = [ENTER, COMMA];
  public searchRoleCtrl: FormControl = new FormControl(null);
  public showActionButton: boolean = false;
  public displayBtn: boolean = false;
  @ViewChild('newRoleInput') newRoleInput: ElementRef<HTMLInputElement>;
  constructor(private fb: FormBuilder, private userManagementService: UserManagementService, private loader: LoadingService, private notify: NotificationService) {
    this.detailForm = this.fb.group({
      id: this.fb.control(null),
      name: this.fb.control(null, [Validators.required, Validators.pattern(/^(\s+\S+\s*)*(?!\s).*$/)]),
      description: this.fb.control(null)
    });

    this.searchRoleCtrl.valueChanges.pipe(startWith(null), debounceTime(500))
      .subscribe((keyword: string | null) => {
        if (keyword) {
          this.filterRoleList = this.filterRoleList.filter(f =>
            f.name.toLowerCase().includes(keyword.toLowerCase()));
        } else {
          this.filterRoleList = this.selectedRoleList?.length !== 0 ? this.getDataFilter() : this.roleList;
        }
      });
  }

  ngOnInit() {
    this.getAllRole();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes?.selectedItem?.currentValue) {
      this.detailForm.setValue(
        {
          id: this.selectedItem?.id || this.selectedItem?.groupId,
          name: this.selectedItem?.name,
          description: this.selectedItem?.description || ''
        }
      );
      this.selectedRoleList = this.selectedItem?.roles!;
      if(this.formType === 'Group'){
        this.originRoleList = Helpers.cloneDeep(this.selectedItem?.roles!);
      }
      if (this.selectedRoleList && this.selectedRoleList?.length === 0) {
        this.filterRoleList = this.roleList;
      } else {
        this.getDataFilter();
      }
    }
  }

  private getDataFilter() {
    this.filterRoleList = [];
    this.roleList.forEach((val: any) => {
      let getRoleFilter = this.selectedRoleList?.filter(selectedRole => selectedRole.id === val.id);
      if (getRoleFilter && getRoleFilter?.length === 0) {
        this.filterRoleList.push(val);
      }

    })
    return this.filterRoleList;
  }

  onSave() {
    if (this.detailForm.invalid) {
      return;
    }
    this.selectedItem = { ...this.selectedItem, ...this.detailForm.value };
    if (this.formType == 'Group') {
      this.selectedItem!.roles = this.selectedRoleList;
    }
    this.selectedItem!.name = this.selectedItem!.name.trim();
    this.onFormSave.emit(this.selectedItem!);
  }

  onCancel() {
    this.resetRoleInput();
    this.showActionButton = false;
    this.onCancle.emit(this.selectedItem)
  }

  onResetForm() {
    this.detailForm.reset();
    this.searchRoleCtrl.reset();
    this.showActionButton = false;
  }

  onDisableForm() {
    this.detailForm.disable();
    this.searchRoleCtrl.disable();
    this.showActionButton = false;
  }

  onRemoveRole(role: RoleModel, index: number) {
    const loader = this.loader.showProgressBar();
    this.userManagementService.deleteRoleByGroupDetail(this.selectedItem!, role.id)
      .pipe(finalize(() => this.loader.hideProgressBar(loader)))
      .subscribe((res: BaseResponseModel) => {
        if (res.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.originRoleList = this.originRoleList.filter(role => role.id !== this.selectedRoleList[index].id);
          this.selectedRoleList?.splice(index, 1);
          this.getDataFilter();
        } else {
          this.notify.error(res?.message);
        }
      })
  }
  onSelectedRole(event: MatAutocompleteSelectedEvent) {
    this.showActionButton = true;
    this.selectedRoleList?.push(event.option.value);
    this.resetRoleInput();
  }

  onFieldFocus() {
    this.showActionButton = true;
  }

  onButtonShow() {
    this.displayBtn = true;
    this.onDisplayBtn.emit(this.displayBtn);
  }

  getAllRole() {
    this.userManagementService.getAllRoles().subscribe(
      (res: BaseResponseModel) => {
        this.roleList = res?.data || [];
      });
  }

  resetRoleInput(){
    if(this.newRoleInput){
      this.newRoleInput.nativeElement.value = '';
    }
    this.searchRoleCtrl.reset();
  }

  setOriginRoleList(){
    this.originRoleList = Helpers.cloneDeep(this.selectedRoleList);
  }

  setSelectedRoleList(){
    this.selectedRoleList = Helpers.cloneDeep(this.originRoleList);
  }

}
