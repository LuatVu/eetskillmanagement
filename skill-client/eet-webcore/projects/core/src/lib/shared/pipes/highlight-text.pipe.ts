import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'highlightText',
})
export class HighlightTextPipe implements PipeTransform {
  transform(value: string, ...args: string[]): string {
    if (typeof value === 'number' || !value || !args || args.length === 0) {
      return value;
    }
    if (args == null) {
      return value;
    }

    args = args
      .filter((val) => val != null)
      .filter((val) => (val + '').trim() !== '');

    if (args.length === 0) {
      return value;
    }
    const filter = args
      .map((val) => val.trim().replace(/([^\w])/g, '\\$1'))
      .join('|');
    return value.replace(
      new RegExp(filter, 'i'),
      `<span class="highlight">$&</span>`
    );
  }
}
