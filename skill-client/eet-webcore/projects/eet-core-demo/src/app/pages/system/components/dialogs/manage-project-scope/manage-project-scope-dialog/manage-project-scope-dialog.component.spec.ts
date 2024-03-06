import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageProjectScopeDialogComponent } from './manage-project-scope-dialog.component';

describe('ManageProjectScopeDialogComponent', () => {
  let component: ManageProjectScopeDialogComponent;
  let fixture: ComponentFixture<ManageProjectScopeDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManageProjectScopeDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ManageProjectScopeDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
