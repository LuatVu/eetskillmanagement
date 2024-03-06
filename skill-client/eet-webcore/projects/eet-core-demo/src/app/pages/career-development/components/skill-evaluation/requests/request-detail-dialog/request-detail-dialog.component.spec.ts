import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestDetailDialogComponent } from './request-detail-dialog.component';

describe('RequestDetailDialogComponent', () => {
  let component: RequestDetailDialogComponent;
  let fixture: ComponentFixture<RequestDetailDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RequestDetailDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestDetailDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
