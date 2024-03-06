/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CommonChipComponent } from './common-chip.component';

describe('CommonChipComponent', () => {
  let component: CommonChipComponent;
  let fixture: ComponentFixture<CommonChipComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CommonChipComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CommonChipComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
