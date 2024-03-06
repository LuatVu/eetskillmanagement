import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageProjectRoleComponent } from './manage-project-role.component';

describe('ManageProjectRoleComponent', () => {
  let component: ManageProjectRoleComponent;
  let fixture: ComponentFixture<ManageProjectRoleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManageProjectRoleComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ManageProjectRoleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
