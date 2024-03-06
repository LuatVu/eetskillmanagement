import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ForwardRequestComponent } from './forward-request.component';

describe('ForwardRequestComponent', () => {
  let component: ForwardRequestComponent;
  let fixture: ComponentFixture<ForwardRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ForwardRequestComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ForwardRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
