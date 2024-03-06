import { CdkAccordionModule } from '@angular/cdk/accordion';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {
  BciLayoutModule,
  BciSharedModule,
  PicklistModule
} from '@bci-web-core/core';
import { TranslateModule } from '@ngx-translate/core';
import { EetCoreModule } from 'projects/core/src/public-api';
import { AddUserOrGroupComponent } from './common/components/add-list-users-and-distributions/add-user-or-group/add-user-or-group.component';
import { AddUserAndDistributionsComponent } from './common/components/add-list-users-and-distributions/add-users-and-distributions.component';
import { CommonCreateItemDialogComponent } from './common/components/common-create-item-dialog/common-create-item-dialog.component';
import { CommonDetailCopyComponent } from './common/components/common-detail-copy/common-detail-copy.component';
import { CommonDetailComponent } from './common/components/common-detail/common-detail.component';
import { CommonListComponent } from './common/components/common-list/common-list.component';
import { UserManagementService } from './common/services/user-management.service';
import { UserManagementRoutingModule } from './user-management-routing.module';
import { UserManagementComponent } from './user-management/user-management.component';
import { UserPermisionComponent } from './user-permision/user-permision.component';
import { UserRolesComponent } from './user-roles/user-roles.component';
import { UsersAndGroupsComponent } from './users-and-groups/users-and-groups.component';
import { SharedCommonModule } from "../../shared/shared.module";

@NgModule({
    declarations: [
        UserManagementComponent,
        UsersAndGroupsComponent,
        UserPermisionComponent,
        CommonListComponent,
        CommonDetailComponent,
        CommonCreateItemDialogComponent,
        UserRolesComponent,
        AddUserAndDistributionsComponent,
        CommonDetailCopyComponent,
        AddUserOrGroupComponent,
    ],
    exports: [
        CommonListComponent,
        CommonDetailComponent,
        CommonCreateItemDialogComponent,
        CommonDetailComponent,
    ],
    providers: [UserManagementService],
    imports: [
        CommonModule,
        UserManagementRoutingModule,
        PicklistModule,
        BciLayoutModule,
        BciSharedModule,
        EetCoreModule,
        TranslateModule,
        CdkAccordionModule,
        SharedCommonModule
    ]
})
export class UserManagementModule { }
