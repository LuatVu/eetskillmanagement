import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewPreviousRequestDetailComponent } from './view-previous-request-detail.component';

describe('ViewRequestDetailComponent', () => {
  let component: ViewPreviousRequestDetailComponent;
  let fixture: ComponentFixture<ViewPreviousRequestDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewPreviousRequestDetailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewPreviousRequestDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
