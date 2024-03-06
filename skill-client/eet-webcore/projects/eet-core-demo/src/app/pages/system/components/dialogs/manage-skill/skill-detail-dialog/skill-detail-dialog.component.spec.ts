import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SkillDetailDialogComponent } from './skill-detail-dialog.component';

describe('SkillDetailDialogComponent', () => {
  let component: SkillDetailDialogComponent;
  let fixture: ComponentFixture<SkillDetailDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SkillDetailDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SkillDetailDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
