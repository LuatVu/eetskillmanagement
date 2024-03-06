import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EetAppComponent } from './eet-app.component';

describe('EetAppComponent', () => {
  let component: EetAppComponent;
  let fixture: ComponentFixture<EetAppComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EetAppComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EetAppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
