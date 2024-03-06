import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateChild, Router,
  RouterStateSnapshot,
  UrlTree
} from '@angular/router';
import { NavigationItemsModel, PageNavigationService } from '../../../../eet-core-demo/src/app/shared/services/page-navigation.service';
import { CoreUrl } from '../shared/util/url.constant';
import { AuthService } from './auth.service';
import { PermisisonService } from 'projects/eet-core-demo/src/app/shared/services/permisison.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate, CanActivateChild {
  constructor(private auth: AuthService, private router: Router, private pageNavigationService: PageNavigationService,private permissionService : PermisisonService) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.authenticate(route);
  }

  canActivateChild(
    childRoute: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,

  ): boolean | UrlTree {
    return this.authenticate(childRoute);
  }

  private authenticate(route: ActivatedRouteSnapshot): boolean | UrlTree {
    if (this.auth.check()) {
      const currentUrl = route.routeConfig?.path ? route.routeConfig.path : '';
      if (currentUrl.indexOf(`${CoreUrl.AUTH}`) !== -1) {
        // return dashboard
        this.router.parseUrl('/')
      } else if (currentUrl) {
        if (route.data && route.data.permissions) {
          let temp:boolean=false;
          if(route.data.permissions.length===0) return true
          route.data.permissions.forEach((e:string) => {
            if(this.permissionService.getPermission().includes(e)) {
              temp = true
              return 
            }
          })
          if(!temp){
            this.router.navigate([`${CoreUrl.NO_PERMISSION}`])
          }
        }
      }
    } 
    return true;
  }
}
