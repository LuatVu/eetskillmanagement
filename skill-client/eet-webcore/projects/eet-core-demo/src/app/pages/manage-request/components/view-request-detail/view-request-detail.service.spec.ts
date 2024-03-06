import { TestBed } from '@angular/core/testing';

import { ViewRequestDetailService } from './view-request-detail.service';

describe('ViewRequestDetailService', () => {
  let service: ViewRequestDetailService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ViewRequestDetailService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
