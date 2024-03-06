import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogUploadUploadAvatarComponent } from './dialog-upload-upload-avatar.component';

describe('DialogUploadUploadAvatarComponent', () => {
  let component: DialogUploadUploadAvatarComponent;
  let fixture: ComponentFixture<DialogUploadUploadAvatarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogUploadUploadAvatarComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogUploadUploadAvatarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
