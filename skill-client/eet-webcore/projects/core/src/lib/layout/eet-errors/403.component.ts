import { Component } from '@angular/core';

@Component({
  selector: 'app-error-403',
  template: `
    <error-code
      code="403"
      [title]="'error.403.title'"
      [message]="'error.403.message'"
    ></error-code>
  `,
})
export class Error403Component {}
