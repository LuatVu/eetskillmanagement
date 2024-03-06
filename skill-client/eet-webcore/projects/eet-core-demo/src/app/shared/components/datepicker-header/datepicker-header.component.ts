import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Inject,
  OnDestroy,
} from '@angular/core';
import { MatCalendar } from '@angular/material/datepicker';
import { DateAdapter, MAT_DATE_FORMATS, MatDateFormats } from '@angular/material/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

/** Custom header component for datepicker. */
@Component({
  selector: 'example-header',
  styleUrls: ['./datepicker-header.component.scss'],
  template: `
    <div class="example-header">
      <!-- <button mat-icon-button class="example-double-arrow" (click)="previousClicked('year')">
        <i class="a-icon ui-ic-left-small" title="left-small"></i>
      </button> -->
      <button mat-icon-button (click)="previousClicked('month')">
     <i class="a-icon ui-ic-left-small" title="Previous Month"></i>
      </button>
      <span class="example-header-label" (click)="currentPeriodClicked()" >{{periodLabel}}</span>
      <button mat-icon-button (click)="nextClicked('month')">
      <i class="a-icon ui-ic-right-small" title="Next Month"></i>
      </button>
      <!-- <button mat-icon-button class="example-double-arrow" (click)="nextClicked('year')">
        <mat-icon>keyboard_arrow_right</mat-icon>
        <mat-icon>keyboard_arrow_right</mat-icon>
      </button> -->
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DatePickerHeader<D> implements OnDestroy {
  private _destroyed = new Subject<void>();

  constructor(
    private _calendar: MatCalendar<D>,
    private _dateAdapter: DateAdapter<D>,
    @Inject(MAT_DATE_FORMATS) private _dateFormats: MatDateFormats,
    cdr: ChangeDetectorRef,
  ) {
    _calendar.stateChanges.pipe(takeUntil(this._destroyed)).subscribe(() => cdr.markForCheck());
  }

  ngOnDestroy() {
    this._destroyed.next();
    this._destroyed.complete();
  }

  get periodLabel() {
    return this._dateAdapter
      .format(this._calendar.activeDate, this._dateFormats.display.monthYearLabel)
      .toLocaleUpperCase();
  }

  previousClicked(mode: 'month' | 'year') {
    this._calendar.activeDate =
      mode === 'month'
        ? this._dateAdapter.addCalendarMonths(this._calendar.activeDate, -1)
        : this._dateAdapter.addCalendarYears(this._calendar.activeDate, -1);
  }

  nextClicked(mode: 'month' | 'year') {
    this._calendar.activeDate =
      mode === 'month'
        ? this._dateAdapter.addCalendarMonths(this._calendar.activeDate, 1)
        : this._dateAdapter.addCalendarYears(this._calendar.activeDate, 1);
  }

  /** Handles user clicks on the period label. */
  currentPeriodClicked(): void {
    this._calendar.currentView = this._calendar.currentView == 'month' ? 'multi-year' : 'month';
  }

}
