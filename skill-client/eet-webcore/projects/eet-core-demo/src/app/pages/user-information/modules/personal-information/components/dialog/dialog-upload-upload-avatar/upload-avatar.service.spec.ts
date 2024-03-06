import { TestBed } from '@angular/core/testing';

import { UploadAvatarService } from './upload-avatar.service';

describe('UploadAvatarService', () => {
  let service: UploadAvatarService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UploadAvatarService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
