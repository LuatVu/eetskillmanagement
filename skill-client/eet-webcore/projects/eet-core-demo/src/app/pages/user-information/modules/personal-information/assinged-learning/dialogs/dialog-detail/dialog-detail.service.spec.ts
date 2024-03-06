import { TestBed } from '@angular/core/testing';

import { DialogDetailService } from './dialog-detail.service';

describe('DialogDetailService', () => {
  let service: DialogDetailService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DialogDetailService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
