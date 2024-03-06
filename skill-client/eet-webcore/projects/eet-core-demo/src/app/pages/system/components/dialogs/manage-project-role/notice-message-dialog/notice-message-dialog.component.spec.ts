import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NoticeMessageDialogComponent } from './notice-message-dialog.component';

describe('NoticeMessageDialogComponent', () => {
  let component: NoticeMessageDialogComponent;
  let fixture: ComponentFixture<NoticeMessageDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NoticeMessageDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NoticeMessageDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
