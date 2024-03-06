import { Injectable } from '@angular/core';
import { TokenService } from '../../../../../core/src/lib/authentication/token.service';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PermisisonService {
  private permission$: BehaviorSubject<string[]> = new BehaviorSubject<string[]>([]);

  constructor() { }

  getPermission(): string[] {
    return this.permission$.value;
  }

  setPermission(permission: string[]) {
    this.permission$.next(permission);
  }

  hasPermission(permission: string): boolean {
    return this.getPermission().includes(permission) || false;
  }

}
