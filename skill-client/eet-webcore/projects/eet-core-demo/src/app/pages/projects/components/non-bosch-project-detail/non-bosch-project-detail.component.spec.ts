import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NonBoschProjectDetailComponent } from './non-bosch-project-detail.component';

describe('NonBoschProjectDetailComponent', () => {
  let component: NonBoschProjectDetailComponent;
  let fixture: ComponentFixture<NonBoschProjectDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NonBoschProjectDetailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NonBoschProjectDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
