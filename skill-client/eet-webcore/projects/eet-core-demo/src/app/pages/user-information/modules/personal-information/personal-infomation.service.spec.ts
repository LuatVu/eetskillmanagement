import { TestBed } from '@angular/core/testing';

import { PersonalInfomationService } from './personal-infomation.service';

describe('PersonalInfomationService', () => {
  let service: PersonalInfomationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PersonalInfomationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
