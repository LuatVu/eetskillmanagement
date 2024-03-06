import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EetHeaderComponent } from './eet-header.component';

describe('EetHeaderComponent', () => {
  let component: EetHeaderComponent;
  let fixture: ComponentFixture<EetHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EetHeaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EetHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
