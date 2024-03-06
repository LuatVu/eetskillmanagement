import { TestBed } from '@angular/core/testing';

import { UploadCourseService } from './upload-course.service';

describe('UploadCourseService', () => {
  let service: UploadCourseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UploadCourseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
