import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PendingRequestComponent } from './pending-request.component';

describe('ManageRequestComponent', () => {
  let component: PendingRequestComponent;
  let fixture: ComponentFixture<PendingRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PendingRequestComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PendingRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
