import { TestBed } from '@angular/core/testing';

import { MakeSkillHighlightService } from './make-skill-highlight.service';

describe('MakeSkillHighlightService', () => {
  let service: MakeSkillHighlightService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MakeSkillHighlightService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
