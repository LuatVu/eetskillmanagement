import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommonConfigComponent } from './common-config.component';

describe('CommonConfigComponent', () => {
  let component: CommonConfigComponent;
  let fixture: ComponentFixture<CommonConfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CommonConfigComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CommonConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
