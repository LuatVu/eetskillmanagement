import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectApproverDialogComponent } from './select-approver-dialog.component';

describe('SelectApproverDialogComponent', () => {
  let component: SelectApproverDialogComponent;
  let fixture: ComponentFixture<SelectApproverDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SelectApproverDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectApproverDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
