import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LineManagerDialogComponent } from './line-manager-dialog.component';

describe('LineManagerDialogComponent', () => {
  let component: LineManagerDialogComponent;
  let fixture: ComponentFixture<LineManagerDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LineManagerDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LineManagerDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
