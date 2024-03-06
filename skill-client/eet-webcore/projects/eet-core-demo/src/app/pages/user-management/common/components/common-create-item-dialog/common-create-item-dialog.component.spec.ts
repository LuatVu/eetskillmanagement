import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommonCreateItemDialogComponent } from './common-create-item-dialog.component';

describe('CommonCreateItemDialogComponent', () => {
  let component: CommonCreateItemDialogComponent;
  let fixture: ComponentFixture<CommonCreateItemDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CommonCreateItemDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CommonCreateItemDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
