import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviousRequestComponent } from './previous-request.component';

describe('PreviousRequestComponent', () => {
  let component: PreviousRequestComponent;
  let fixture: ComponentFixture<PreviousRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreviousRequestComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PreviousRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
