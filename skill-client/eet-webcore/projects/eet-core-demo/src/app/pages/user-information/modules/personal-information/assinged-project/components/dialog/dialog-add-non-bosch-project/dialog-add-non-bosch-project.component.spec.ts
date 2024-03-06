import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DialogNonBoschProjectComponent } from './dialog-add-non-bosch-project.component';


describe('DialogProjectComponent', () => {
  let component: DialogNonBoschProjectComponent;
  let fixture: ComponentFixture<DialogNonBoschProjectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogNonBoschProjectComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogNonBoschProjectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
