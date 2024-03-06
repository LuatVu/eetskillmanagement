import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserPermisionComponent } from './user-permision.component';

describe('UserPermisionComponent', () => {
  let component: UserPermisionComponent;
  let fixture: ComponentFixture<UserPermisionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserPermisionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserPermisionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
