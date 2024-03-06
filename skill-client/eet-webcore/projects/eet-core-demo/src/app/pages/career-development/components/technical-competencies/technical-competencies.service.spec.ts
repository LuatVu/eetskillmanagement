import { TestBed } from '@angular/core/testing';

import { TechnicalCompetenciesService } from './technical-competencies.service';

describe('TechnicalCompetenciesService', () => {
  let service: TechnicalCompetenciesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TechnicalCompetenciesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
