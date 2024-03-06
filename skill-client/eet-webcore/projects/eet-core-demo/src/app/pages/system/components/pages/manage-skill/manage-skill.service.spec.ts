import { TestBed } from '@angular/core/testing';

import { ManageSkillService } from './manage-skill.service';

describe('ManageSkillService', () => {
  let service: ManageSkillService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ManageSkillService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
