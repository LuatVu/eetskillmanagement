import { TestBed } from '@angular/core/testing';

import { MyLearningService } from './my-learning.service';

describe('MyLearningService', () => {
  let service: MyLearningService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MyLearningService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
