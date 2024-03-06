import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReplaceTagSkillDialogComponent } from './replace-tag-skill-dialog.component';

describe('ReplaceTagSkillDialog', () => {
  let component: ReplaceTagSkillDialogComponent;
  let fixture: ComponentFixture<ReplaceTagSkillDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReplaceTagSkillDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReplaceTagSkillDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
