import { Injectable } from '@angular/core';
import { EventEmitter } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CareerDevelopmentService {

  public isEditted: boolean = false;
  public confirmation: boolean = false;

  private _confirm : BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  public confirm$ = this._confirm.asObservable();

  public oldPageIndex: number;

  setConfirm(confirm: boolean) {
    this._confirm.next(confirm);
  }

  setOldPageIndex(oldIndex: number) {
    this.oldPageIndex = oldIndex;
  }

  getOldPageIndex(): number {
    return this.oldPageIndex;
  }

  constructor() {

  }
  private _editSkillMode : BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public editSkillMode$ = this._editSkillMode.asObservable();


  editSkillMode(data:any) {
    this._editSkillMode.next(data)
  }

  setEditted(isEditted: boolean) {
    this.isEditted = isEditted;
  }

  getEditted(): boolean {
    return this.isEditted;
  }


  public careerRoadMapEmitter = new EventEmitter<string>();

  public techComp: boolean;

}
