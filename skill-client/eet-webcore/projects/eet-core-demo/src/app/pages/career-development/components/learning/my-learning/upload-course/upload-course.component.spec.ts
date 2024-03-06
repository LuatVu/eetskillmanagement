import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadCourseComponent } from './upload-course.component';

describe('UploadCourseComponent', () => {
  let component: UploadCourseComponent;
  let fixture: ComponentFixture<UploadCourseComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UploadCourseComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadCourseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
