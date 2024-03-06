import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssingedProjectComponent } from './assinged-project.component';

describe('AssingedProjectComponent', () => {
  let component: AssingedProjectComponent;
  let fixture: ComponentFixture<AssingedProjectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssingedProjectComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssingedProjectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
