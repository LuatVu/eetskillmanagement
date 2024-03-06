import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SupplyComponent } from './supply.component';
import { SharedModule } from '../../../../../core/src/lib/shared/shared.module';
import { SharedCommonModule } from '../../shared/shared.module';
import { SupplyService } from './supply.service';
import { AddNewSupplyComponent } from './add-new-supply/add-new-supply.component';
import { HistorySupplyComponent } from './show-history/history.component';
import { SupplyRoutingModule } from "./supply-routing.module";
import { EditSupplyComponent } from './edit-supply/edit-supply.component';
@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    SharedCommonModule,
    SupplyRoutingModule
  ],
  declarations: [SupplyComponent, AddNewSupplyComponent, HistorySupplyComponent, EditSupplyComponent],
  exports: [SupplyComponent],
  providers: [SupplyService]
})
export class SupplyModule { }