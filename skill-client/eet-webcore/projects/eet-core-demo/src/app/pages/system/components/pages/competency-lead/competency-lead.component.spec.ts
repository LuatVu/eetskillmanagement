import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompetencyLeadComponent } from './competency-lead.component';

describe('CompetencyLeadComponent', () => {
  let component: CompetencyLeadComponent;
  let fixture: ComponentFixture<CompetencyLeadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CompetencyLeadComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CompetencyLeadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
