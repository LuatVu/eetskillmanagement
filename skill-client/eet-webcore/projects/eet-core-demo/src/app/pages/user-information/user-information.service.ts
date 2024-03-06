import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'platform'
})
export class UserInformationService {

  private currentViewAssociateMode$: BehaviorSubject<string> = new BehaviorSubject<string>('');

  private getCurrentAssociateInModeView$:BehaviorSubject<string> = new BehaviorSubject<string>('');
  public _getCurrentAssociateInModeView = this.getCurrentAssociateInModeView$.asObservable();

  private _confirm : BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public confirm$ = this._confirm.asObservable();

  setConfirm(confirm: boolean) {
    this._confirm.next(confirm);
  }

  public isEditMode: boolean = false;
  public oldIndex: number;

  setEditMode(isEditMode: boolean) {
    this.isEditMode = isEditMode;
  }

  getEditMode(): boolean {
    return this.isEditMode;
  }

  setOldIndex(oldIndex: number) {
    this.oldIndex = oldIndex;
  }

  getOldIndex(): number {
    return this.oldIndex;
  }

  constructor() { }

  public getCurrentViewAssociateMode(): Observable<string> {
    return this.currentViewAssociateMode$.asObservable();
  }

  public setCurrentViewAssociateMode(selectedTab: string): void {
    this.currentViewAssociateMode$.next(selectedTab);
  }

  public getCurrentAssociateInModeView(data:string) {
    this.getCurrentAssociateInModeView$.next(data)
  }
}
