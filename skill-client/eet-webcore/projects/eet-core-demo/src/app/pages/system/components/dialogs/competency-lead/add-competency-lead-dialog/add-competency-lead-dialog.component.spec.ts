import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddCompetencyLeadDialogComponent } from './add-competency-lead-dialog.component';

describe('AddCompetencyLeadDialogComponent', () => {
  let component: AddCompetencyLeadDialogComponent;
  let fixture: ComponentFixture<AddCompetencyLeadDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddCompetencyLeadDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddCompetencyLeadDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
