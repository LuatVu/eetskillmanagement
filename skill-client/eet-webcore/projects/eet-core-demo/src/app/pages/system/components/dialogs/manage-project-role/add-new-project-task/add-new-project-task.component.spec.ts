import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddNewProjectTaskComponent } from './add-new-project-task.component';

describe('AddNewProjectTaskComponent', () => {
  let component: AddNewProjectTaskComponent;
  let fixture: ComponentFixture<AddNewProjectTaskComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddNewProjectTaskComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddNewProjectTaskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
