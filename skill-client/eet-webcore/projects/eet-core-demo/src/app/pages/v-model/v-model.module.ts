import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VModelComponent } from './v-model.component';
import { VModelRoutingModule } from './v-model-routing.module';
import { SharedModule } from '@core/src/lib/shared/shared.module';
import { SharedCommonModule } from '../../shared/shared.module';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    SharedModule,
    SharedCommonModule,
    VModelRoutingModule,
  ],
})
export class VModelModule {}
