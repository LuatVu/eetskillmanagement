import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CareerDevelopmentComponent } from './career-development.component';

describe('CareerDevelopmentComponent', () => {
  let component: CareerDevelopmentComponent;
  let fixture: ComponentFixture<CareerDevelopmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CareerDevelopmentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CareerDevelopmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
