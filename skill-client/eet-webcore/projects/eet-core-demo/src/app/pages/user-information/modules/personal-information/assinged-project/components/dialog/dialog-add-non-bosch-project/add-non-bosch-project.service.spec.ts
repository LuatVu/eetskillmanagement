import { TestBed } from '@angular/core/testing';

import { AddnonBoschProjectService } from './add-non-bosch-project.service';

describe('AddNonBoschProjectService', () => {
  let service: AddnonBoschProjectService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AddnonBoschProjectService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
