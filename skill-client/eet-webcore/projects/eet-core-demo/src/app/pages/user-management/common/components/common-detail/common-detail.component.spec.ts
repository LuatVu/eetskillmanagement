import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommonDetailComponent } from './common-detail.component';

describe('CommonDetailComponent', () => {
  let component: CommonDetailComponent;
  let fixture: ComponentFixture<CommonDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CommonDetailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CommonDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
