import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EetNoAccessComponent } from './eet-no-access.component';

describe('EetNoAccessComponent', () => {
  let component: EetNoAccessComponent;
  let fixture: ComponentFixture<EetNoAccessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EetNoAccessComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EetNoAccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
