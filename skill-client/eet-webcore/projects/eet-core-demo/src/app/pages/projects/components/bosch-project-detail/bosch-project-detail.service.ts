import { Injectable, EventEmitter } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BoschProjectDetailService {

  constructor() { }

  closeEvent: EventEmitter<void> = new EventEmitter<void>();
}
