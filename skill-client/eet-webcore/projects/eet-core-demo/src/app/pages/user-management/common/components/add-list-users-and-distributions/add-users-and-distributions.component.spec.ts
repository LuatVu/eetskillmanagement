import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddUserAndDistributionsComponent } from './add-users-and-distributions.component';

describe('AddUserAndDistributionsComponent', () => {
  let component: AddUserAndDistributionsComponent;
  let fixture: ComponentFixture<AddUserAndDistributionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddUserAndDistributionsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddUserAndDistributionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
