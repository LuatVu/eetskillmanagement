import { Injectable } from "@angular/core";
import { ActivatedRoute, ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from "@angular/router";
import { PageNavigationService } from "projects/eet-core-demo/src/app/shared/services/page-navigation.service";
import { AuthService } from ".";
import { CoreUrl } from "../shared/util/url.constant";

@Injectable({
  providedIn: 'root',
})
export class RequestAccessGuard implements CanActivate {
  constructor(
    private auth: AuthService,
    private router: Router,
    private pageNavigationService: PageNavigationService,
    private route: ActivatedRoute
  ) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const SKM_USER_INACTIVE = localStorage.getItem('SKM_USER_INACTIVE');
    if (SKM_USER_INACTIVE == 'USER_EXISTED_AND_WAITTING_FOR_APPROVAL') {
      this.router.parseUrl(`${CoreUrl.WAITING_ACCESS}`)
      return false;
    }

    return true;
  }
}
