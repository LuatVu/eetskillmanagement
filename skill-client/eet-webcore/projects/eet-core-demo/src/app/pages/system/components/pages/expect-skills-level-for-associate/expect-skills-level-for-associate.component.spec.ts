import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpectSkillsLevelForAssociateComponent } from './expect-skills-level-for-associate.component';

describe('ExpectSkillsLevelForAssociateComponent', () => {
  let component: ExpectSkillsLevelForAssociateComponent;
  let fixture: ComponentFixture<ExpectSkillsLevelForAssociateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExpectSkillsLevelForAssociateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExpectSkillsLevelForAssociateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
