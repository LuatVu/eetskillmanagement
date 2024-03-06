import { TestBed } from '@angular/core/testing';

import { UploadCertificateService } from './upload-certificate.service';

describe('UploadCertificateService', () => {
  let service: UploadCertificateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UploadCertificateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
