import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TechnicalCompetenciesComponent } from './technical-competencies.component';

describe('TechnicalCompetenciesComponent', () => {
  let component: TechnicalCompetenciesComponent;
  let fixture: ComponentFixture<TechnicalCompetenciesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TechnicalCompetenciesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TechnicalCompetenciesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
