import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatOption } from '@angular/material/core';
import { Observable, Subscription, startWith } from 'rxjs';
import { Helpers } from '../../utils/helper';

@Component({
  selector: 'eet-common-select',
  templateUrl: './eet-common-select.component.html',
  styleUrls: ['./eet-common-select.component.scss'],
})
export class EetCommonSelectComponent implements OnInit, OnChanges {
  @ViewChild('allSelected') allSelected: MatOption;

  @Output() onSearchChange: EventEmitter<string> = new EventEmitter<string>();
  @Output() onSelectedItemChange: EventEmitter<any> = new EventEmitter<any>();
  @Input() originalData: Array<any> = [];
  @Input() selectedData: any='';
  @Input() nameKey: any;
  @Input() valueKey: any;
  @Input() isMultipleChoise: boolean = false;
  @Input() isDisabled: boolean = false;
  @Input() isShowUnSelect: boolean = true;
  @Input() placeholder: string = '';
  @Input() labelAllForOptionUnselect: boolean = false
  @Input() event: Observable<boolean>;
  private forceUnselectAllTagSkillGroup: Subscription
  public searchControl = new FormControl();
  public isShowLoading$: Subscription;
  public filterData: Array<any> = [];
  public isSelectAll: boolean = false;

  constructor(private cd: ChangeDetectorRef) { }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes?.originalData?.currentValue) {
      this.filterData = Helpers.cloneDeep(this.originalData);
    }
  }

  ngOnInit() {
    this.watchOnSearchChange();
    if(this.event) {
      this.forceUnselectAllTagSkillGroup = this.event.subscribe((data) => {
        if (data) {
          this.isSelectAll = false
          this.onSelectedItemChange.emit([]);
          this.searchControl.setValue(null)
          
        }
      })
    }
  }

  onSelectedItem() {
    if (this.isMultipleChoise) {
      this.isSelectAll =
        (this.selectedData as Array<any>).length == this.originalData.length;
    }
    this.onSelectedItemChange.emit(this.selectedData);
  }

  watchOnSearchChange() {
    this.searchControl.valueChanges
      .pipe(startWith(null))
      .subscribe((value: string) => {
        if (value) {
          this.filterData = Helpers.cloneDeep(
            this.originalData.filter((item) =>
              this.textCompare(item?.[this.nameKey] || item, value)
            )
          );
        } else {
          this.filterData = Helpers.cloneDeep(this.originalData);
        }
      });
      
  }

  textCompare(a: string, b: string): boolean {
    return a?.toLowerCase()?.includes(b?.toLowerCase());
  }
  onToggleAll() {
    this.isSelectAll = !this.isSelectAll;
    this.onSelectedItemChange.emit(
      this.isSelectAll
        ? this.originalData.map((el) =>
          this.valueKey ? el[this.valueKey] : el
        )
        : []
    );
  }
}
