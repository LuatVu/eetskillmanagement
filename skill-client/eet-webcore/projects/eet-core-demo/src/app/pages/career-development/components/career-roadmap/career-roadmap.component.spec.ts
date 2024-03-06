import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CareerRoadmapComponent } from './career-roadmap.component';

describe('CareerRoadmapComponent', () => {
  let component: CareerRoadmapComponent;
  let fixture: ComponentFixture<CareerRoadmapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CareerRoadmapComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CareerRoadmapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
