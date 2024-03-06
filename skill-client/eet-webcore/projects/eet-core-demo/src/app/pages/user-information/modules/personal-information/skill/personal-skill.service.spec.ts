import { TestBed } from '@angular/core/testing';

import { PersonalSkillService } from './personal-skill.service';

describe('PersonalSkillService', () => {
  let service: PersonalSkillService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PersonalSkillService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
