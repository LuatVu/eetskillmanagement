import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogMakeSkillHighlightComponent } from './dialog-make-skill-highlight.component';

describe('DialogMakeSkillHighlightComponent', () => {
  let component: DialogMakeSkillHighlightComponent;
  let fixture: ComponentFixture<DialogMakeSkillHighlightComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogMakeSkillHighlightComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogMakeSkillHighlightComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
