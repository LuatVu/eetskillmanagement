import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MembersInfoTableComponent } from './members-info-table.component';

describe('MembersInfoTableComponent', () => {
  let component: MembersInfoTableComponent;
  let fixture: ComponentFixture<MembersInfoTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MembersInfoTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MembersInfoTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
