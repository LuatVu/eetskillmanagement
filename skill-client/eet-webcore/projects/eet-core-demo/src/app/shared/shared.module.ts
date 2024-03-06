import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SharedModule } from '@core/src/lib/shared/shared.module';

import { CommonComponents } from './components/common-components';
import { CommonDialogs } from './components/dialogs/common-dialog';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import { MAT_SELECT_CONFIG } from '@angular/material/select';
import { BoschProjectDetailComponent } from '../pages/projects/components/bosch-project-detail/bosch-project-detail.component';
import { MembersInfoTableComponent } from '../pages/projects/components/bosch-project-detail/members-info-table/members-info-table.component';
import { ProjectMemberDialogComponent } from '../pages/projects/components/bosch-project-detail/project-member-dialog/project-member-dialog.component';
import { EditorComponent } from './components/editor/editor.component';
import { QuillModule } from 'ngx-quill';
import { ColorPickerModule } from 'ngx-color-picker';
import { ProjectItemComponent } from '../pages/projects/components/project-item/project-item/project-item.component';

const COMPONENT_BELONG_BOSCH_PROJECT = [ProjectMemberDialogComponent, BoschProjectDetailComponent, MembersInfoTableComponent, ProjectItemComponent]
@NgModule({
  declarations: [CommonDialogs, CommonComponents, ...COMPONENT_BELONG_BOSCH_PROJECT],
  imports: [CommonModule, FormsModule, SharedModule, NgxMatSelectSearchModule, QuillModule],
  exports: [CommonDialogs, CommonComponents, NgxMatSelectSearchModule, ColorPickerModule, ...COMPONENT_BELONG_BOSCH_PROJECT],
  providers: [
    {
      provide: MatDialogRef,
      useValue: {},
    },
    {
      provide: MAT_DIALOG_DATA,
      useValue: {},
    },
    {
      provide: MAT_SELECT_CONFIG,
      useValue: { overlayPanelClass: 'eet-mat-select-overlay' }
    }
  ],
})
export class SharedCommonModule { }
