import { TestBed } from '@angular/core/testing';

import { PesonalLearningService } from './pesonal-learning.service';

describe('PesonalLearningService', () => {
  let service: PesonalLearningService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PesonalLearningService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
