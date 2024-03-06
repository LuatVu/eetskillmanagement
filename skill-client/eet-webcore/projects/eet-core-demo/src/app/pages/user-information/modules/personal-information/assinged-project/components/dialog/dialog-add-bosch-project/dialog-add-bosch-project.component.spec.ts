import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DialogBoschProjectComponent } from './dialog-add-bosch-project.component';


describe('DialogBoschProjectComponent', () => {
  let component: DialogBoschProjectComponent;
  let fixture: ComponentFixture<DialogBoschProjectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DialogBoschProjectComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogBoschProjectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
