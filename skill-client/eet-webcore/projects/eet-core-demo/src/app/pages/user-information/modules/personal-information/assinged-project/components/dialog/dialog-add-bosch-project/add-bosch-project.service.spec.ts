import { TestBed } from '@angular/core/testing';

import { AddBoschProjectService } from './add-bosch-project.service';

describe('AddBoschProjectService', () => {
  let service: AddBoschProjectService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AddBoschProjectService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
