import { AgGridModule } from '@ag-grid-community/angular';
import { ClientSideRowModelModule } from '@ag-grid-community/client-side-row-model';
import { ModuleRegistry } from '@ag-grid-community/core';
import { APP_BASE_HREF } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import {
  APP_INITIALIZER,
  CUSTOM_ELEMENTS_SCHEMA,
  NgModule
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
  BciCoreModule,
  BciLayoutModule,
  BciSharedModule
} from '@bci-web-core/core';
import { AppConfigService } from '@core/src/lib/authentication/app-conf.service';
import { BASE_URL, httpInterceptorProviders } from '@core/src/lib/interceptors';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { TranslateLangService } from 'projects/core/src/lib/shared/services/translate-lang.service';
import { EetCoreModule } from 'projects/core/src/public-api';
import { environment } from '../environments/environment';
import { AppComponent } from './app.component';
import { PagesModule } from './pages/pages.module';
ModuleRegistry.registerModules([ClientSideRowModelModule]);
export function InitServiceFactory(
  appConfigService: AppConfigService,
  translateLangService: TranslateLangService,
) {
  return async () => {
    // handle app config
    await appConfigService.loginSso()
    await translateLangService.load()
  }
}

export function TranslateHttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

@NgModule({
  declarations: [AppComponent],
  imports: [
    MatIconModule,
    BrowserModule,
    FormsModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: TranslateHttpLoaderFactory,
        deps: [HttpClient],
      },
    }),
    BciCoreModule.forRoot({
      prod_environment: environment.production,
      core_config_url: '/assets/config/config.json',
    }),
    FlexLayoutModule.withConfig({
      useColumnBasisZero: false,
      printWithBreakpoints: [
        'md',
        'lt-lg',
        'lt-xl',
        'lt-sm',
        'gt-md',
        'gt-sm',
        'gt-xs',
        'sm',
        'xs',
      ],
    }),
    BrowserAnimationsModule,
    BciLayoutModule,
    BciSharedModule,
    EetCoreModule,
    AgGridModule.withComponents(),
    PagesModule,
    FormsModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatDatepickerModule,
    MatInputModule,
    MatFormFieldModule,
    MatNativeDateModule
  ],
  providers: [
    {
      provide: APP_INITIALIZER,

      useFactory: InitServiceFactory,
      deps: [AppConfigService, TranslateLangService],
      multi: true,
    },

    { provide: BASE_URL, useValue: environment.baseUrl },
    { provide: APP_BASE_HREF, useValue: environment.baseHref },
    httpInterceptorProviders,
    MatIconRegistry
  ],
  bootstrap: [AppComponent],
  exports: [MatIconModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class AppModule { }
