import { TestBed } from '@angular/core/testing';

import { SkillEvaluationService } from './skill-evaluation.service';

describe('SkillEvaluationService', () => {
  let service: SkillEvaluationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SkillEvaluationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
