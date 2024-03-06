import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { BciLayoutModule, BciSharedModule } from '@bci-web-core/core';
import { TranslateModule } from '@ngx-translate/core';
import { EetLayoutModule } from '../layout/eet-layout.module';
import { MaterialModule } from './material.module';

const MODULES: any[] = [
  MaterialModule,
  FlexLayoutModule,
  EetLayoutModule,
  BciLayoutModule,
  BciSharedModule,
  TranslateModule
];
const COMPONENTS: any[] = [];
const COMPONENTS_DYNAMIC: any[] = [];
const DIRECTIVES: any[] = [];
const PIPES: any[] = [];

@NgModule({
  imports: [...MODULES],
  exports: [...MODULES, ...COMPONENTS, ...DIRECTIVES, ...PIPES],
  declarations: [...COMPONENTS, ...COMPONENTS_DYNAMIC, ...DIRECTIVES, ...PIPES],
})
export class SharedModule {}
