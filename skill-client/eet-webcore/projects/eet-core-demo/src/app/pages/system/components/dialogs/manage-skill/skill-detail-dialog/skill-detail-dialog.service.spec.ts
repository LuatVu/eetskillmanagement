import { TestBed } from '@angular/core/testing';

import { SkillDetailDialogService } from './skill-detail-dialog.service';

describe('SkillDetailDialogService', () => {
  let service: SkillDetailDialogService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SkillDetailDialogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
