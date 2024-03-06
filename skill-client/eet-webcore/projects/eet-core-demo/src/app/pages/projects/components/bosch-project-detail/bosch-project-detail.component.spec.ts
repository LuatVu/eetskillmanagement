import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BoschProjectDetailComponent } from './bosch-project-detail.component';

describe('BoschProjectDetailComponent', () => {
  let component: BoschProjectDetailComponent;
  let fixture: ComponentFixture<BoschProjectDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BoschProjectDetailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BoschProjectDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
