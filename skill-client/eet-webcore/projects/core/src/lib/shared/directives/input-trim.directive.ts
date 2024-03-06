import { Directive, HostListener, Input, Optional } from '@angular/core';
import { FormControlDirective, FormControlName } from '@angular/forms';

@Directive({
  selector: '[inputTrim]'
})
export class InputTrimDirective {
  @Input() type!: string;

  constructor(
    @Optional() private formControlDir: FormControlDirective, 
    @Optional() private formControlName: FormControlName) {}

  @HostListener('blur')
  @HostListener('window:keyup')
  public trimValue(): void {
    const control = this.formControlDir?.control || this.formControlName?.control;
    // if (typeof control.value === 'string' && this.type !== 'password') {
    //   control.setValue(control.value.trim(), {emitEvent: false,  onlySelf: true});
    // }
  }
}
