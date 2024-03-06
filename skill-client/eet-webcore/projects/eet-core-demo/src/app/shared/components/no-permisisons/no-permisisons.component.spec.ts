/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { NoPermisisonsComponent } from './no-permisisons.component';

describe('NoPermisisonsComponent', () => {
  let component: NoPermisisonsComponent;
  let fixture: ComponentFixture<NoPermisisonsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NoPermisisonsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NoPermisisonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
