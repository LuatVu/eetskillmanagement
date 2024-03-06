import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LevelDetailDialogComponent } from './level-detail-dialog.component';

describe('LevelDetailDialogComponent', () => {
  let component: LevelDetailDialogComponent;
  let fixture: ComponentFixture<LevelDetailDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LevelDetailDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LevelDetailDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
