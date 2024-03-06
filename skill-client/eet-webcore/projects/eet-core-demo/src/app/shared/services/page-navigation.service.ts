import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface NavigationItemsModel {
  permissions: string[]
  child:NavigationItemsModel[]
}

@Injectable({
  providedIn: 'root'
})
export class PageNavigationService {
  public pageNavigationItems$: BehaviorSubject<NavigationItemsModel[]> = new BehaviorSubject<NavigationItemsModel[]>([]);

  constructor() { }

  setPageNavigationItems(items: NavigationItemsModel[]) {
    this.pageNavigationItems$.next(items);
  }

  getPageNavigationItems(): Observable<NavigationItemsModel[]> {
    return this.pageNavigationItems$.asObservable();
  }
}
