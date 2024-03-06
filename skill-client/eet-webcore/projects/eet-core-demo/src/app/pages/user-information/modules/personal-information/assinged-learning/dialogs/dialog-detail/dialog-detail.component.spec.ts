import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogDetailComponent } from './dialog-detail.component';

describe('DialogDetailComponent', () => {
  let component: DialogDetailComponent;
  let fixture: ComponentFixture<DialogDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogDetailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
