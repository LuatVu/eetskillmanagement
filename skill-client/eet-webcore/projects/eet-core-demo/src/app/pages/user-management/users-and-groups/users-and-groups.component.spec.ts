import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsersAndGroupsComponent } from './users-and-groups.component';

describe('UsersAndGroupsComponent', () => {
  let component: UsersAndGroupsComponent;
  let fixture: ComponentFixture<UsersAndGroupsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UsersAndGroupsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UsersAndGroupsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
