import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssingedLearningComponent } from './assinged-learning.component';

describe('AssingedLearningComponent', () => {
  let component: AssingedLearningComponent;
  let fixture: ComponentFixture<AssingedLearningComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssingedLearningComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssingedLearningComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
