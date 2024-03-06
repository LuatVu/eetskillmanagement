import { Component, OnInit } from '@angular/core';
import { BOSCH_COLORS, LoadingScreenService } from '@bci-web-core/core';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { Router } from '@angular/router';
import { TokenService } from '@core/src/lib/authentication';

@Component({
  selector: 'eet-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  public components: any[] = [
    {id: '1', name: 'dashboard_components.form_controls.title', description: 'dashboard_components.form_controls.description'},
    {id: '2', name: 'dashboard_components.navigation.title', description: 'dashboard_components.navigation.description'},
    {id: '3', name: 'dashboard_components.layout.title', description: 'dashboard_components.layout.description'},
    {id: '4', name: 'dashboard_components.button_indicators.title', description: 'dashboard_components.button_indicators.description'},
    {id: '5', name: 'dashboard_components.modals_popups.title', description: 'dashboard_components.modals_popups.description'},
    {id: '6', name: 'dashboard_components.master_detail.title', description: 'dashboard_components.master_detail.description'},
    {id: '7', name: 'dashboard_components.data_table.title', description: 'dashboard_components.data_table.description'},
    {id: '8', name: 'dashboard_components.services.title', description: 'dashboard_components.services.description'},
    {id: '9', name: 'dashboard_components.charts.title', description: 'dashboard_components.charts.description'},
    {id: '10', name: 'dashboard_components.colors.title', description: 'dashboard_components.colors.description'},
    {id: '11', name: 'dashboard_components.icons.title', description: 'dashboard_components.icons.description'}
  ];

  public masterBrandColors = [
    { name: 'BoschRed', colorCode: BOSCH_COLORS.BoschRed },
    { name: 'BoschViolet', colorCode: BOSCH_COLORS.BoschViolet },
    { name: 'BoschDarkBlue', colorCode: BOSCH_COLORS.BoschDarkBlue },
    { name: 'BoschTurquoise', colorCode: BOSCH_COLORS.BoschTurquoise },
    { name: 'BoschLightGreen', colorCode: BOSCH_COLORS.BoschLightGreen },
  ];

  constructor(private loadingScreenService: LoadingScreenService,
    private auth: TokenService,
    private router: Router) { };

  ngOnInit() {
  }

  showLoadingScreenIndeterminateCloseable() {
    this.loadingScreenService.show({
      closeable: true,
    });
  }
}
