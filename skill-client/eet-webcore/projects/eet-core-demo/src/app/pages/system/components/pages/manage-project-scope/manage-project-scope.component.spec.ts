import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageProjectScopeComponent } from './manage-project-scope.component';

describe('ManageProjectScopeComponent', () => {
  let component: ManageProjectScopeComponent;
  let fixture: ComponentFixture<ManageProjectScopeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManageProjectScopeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ManageProjectScopeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
