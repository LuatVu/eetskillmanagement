import { TestBed } from '@angular/core/testing';

import { ManageProjectRoleService } from './manage-project-role.service';

describe('ManageProjectRoleService', () => {
  let service: ManageProjectRoleService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ManageProjectRoleService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
