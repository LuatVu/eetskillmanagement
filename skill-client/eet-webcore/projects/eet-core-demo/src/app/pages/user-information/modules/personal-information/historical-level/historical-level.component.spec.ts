import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoricalLevelComponent } from './historical-level.component';

describe('HistoricalLevelComponent', () => {
  let component: HistoricalLevelComponent;
  let fixture: ComponentFixture<HistoricalLevelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HistoricalLevelComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HistoricalLevelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
