import { TestBed } from '@angular/core/testing';

import { ManageRequestService } from './manage-request.service';

describe('ManageRequestService', () => {
  let service: ManageRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ManageRequestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
