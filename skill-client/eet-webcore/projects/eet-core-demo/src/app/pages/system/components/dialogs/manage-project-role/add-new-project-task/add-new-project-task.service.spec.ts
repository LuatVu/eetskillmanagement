import { TestBed } from '@angular/core/testing';

import { AddNewProjectTaskService } from './add-new-project-task.service';

describe('AddNewProjectTaskService', () => {
  let service: AddNewProjectTaskService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AddNewProjectTaskService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
