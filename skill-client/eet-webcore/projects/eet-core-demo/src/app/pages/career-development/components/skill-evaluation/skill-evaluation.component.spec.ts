import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SkillEvaluationComponent } from './skill-evaluation.component';

describe('SkillEvaluationComponent', () => {
  let component: SkillEvaluationComponent;
  let fixture: ComponentFixture<SkillEvaluationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SkillEvaluationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SkillEvaluationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
