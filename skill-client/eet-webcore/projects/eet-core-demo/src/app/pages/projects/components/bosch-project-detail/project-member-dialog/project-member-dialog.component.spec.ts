import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectMemberDialogComponent } from './project-member-dialog.component';

describe('ProjectMemberDialogComponent', () => {
  let component: ProjectMemberDialogComponent;
  let fixture: ComponentFixture<ProjectMemberDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProjectMemberDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProjectMemberDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
