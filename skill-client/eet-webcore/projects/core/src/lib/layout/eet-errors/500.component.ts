import { Component } from '@angular/core';

@Component({
  selector: 'app-error-500',
  template: `
    <error-code
      code="500"
      [title]="'error.500.title'"
      [message]="'error.500.message'"
    >
    </error-code>
  `,
})
export class Error500Component {}
