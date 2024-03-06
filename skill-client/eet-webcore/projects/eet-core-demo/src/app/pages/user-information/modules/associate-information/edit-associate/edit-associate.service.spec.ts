import { TestBed } from '@angular/core/testing';

import { EditAssociateService } from './edit-associate.service';

describe('EditAssociateService', () => {
  let service: EditAssociateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EditAssociateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
