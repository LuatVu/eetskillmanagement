import { TestBed } from '@angular/core/testing';

import { PersonalProjectService } from './personal-project.service';

describe('PesonalProjectService', () => {
  let service: PersonalProjectService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PersonalProjectService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
