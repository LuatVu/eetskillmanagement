import { AfterViewInit, ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatOption } from '@angular/material/core';
import { ReplaySubject } from 'rxjs';

@Component({
  selector: 'eet-select-with-search',
  templateUrl: './select-with-search.component.html',
  styleUrls: ['./select-with-search.component.scss']
})
export class SelectWithSearchComponent implements OnInit, AfterViewInit {
  @ViewChild('allSelected') allSelected!: MatOption;
  @Input() valueCtrl: FormControl = new FormControl();
  @Input() originalData: Array<any> = [];
  @Input() label: string = '';
  @Input() isMultiple: boolean = false;
  @Input() isRequired: boolean = false;
  @Input() panelClass: string = '';
  @Input() disableOptionCentering: boolean = false;
  @Input() filterDataFromParent: string[] = [];
  @Input() fieldDiaplayname: string = '';
  @Input() fieldValuename: string = '';
  @Input() compareObjects = (c1: any, c2: any) => {
    return c1 == c2;
  };
  @Input() searchCtrl = new FormControl();

  @Output() onFilterChangeEvent = new EventEmitter<any>();

  public filteredList: ReplaySubject<any[]> = new ReplaySubject<any[]>(1);
  public _noEntriesFoundLabel = 'Not found';
  public _searchPlaceHolder = 'Search';
  public isToggleAll: boolean = false;
  public isNoData: boolean = false;

  constructor(private changeDetectRef: ChangeDetectorRef) {
  }
  ngAfterViewInit(): void {
    this.filteredList.next(this.originalData.slice());
    this.changeDetectRef.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges): void {
    //Called before any other lifecycle hook. Use it to inject dependencies, but avoid any serious work here.
    //Add '${implements OnChanges}' to the class.
    if (changes?.originalData?.currentValue) {
      this.searchCtrl.setValue('');
    }
  }

  ngOnInit() {
    this.searchCtrl.valueChanges.subscribe(data => {
      this.filterData();
    });
    this.valueCtrl.valueChanges.subscribe(data => {
      if (data && this.isMultiple) {
        this.isToggleAll = data.length == this.originalData.length;
      }

      this.onFilterChangeEvent.emit({
        filterType: this.label,
        filterData: data
      });
    });
    this.filteredList.subscribe((hasData) => {
      this.isNoData = !hasData || hasData.length == 0;
    });

    this.valueCtrl.setValue(this.filterDataFromParent);
  }

  protected filterData() {
    if (!this.originalData) {
      return;
    }
    // get the search keyword
    let search = this.searchCtrl.value;
    if (!search) {
      this.filteredList.next(this.originalData.slice());
      return;
    } else {
      search = search.toLowerCase();
    }
    this.filteredList.next(
      this.originalData.filter(
        (value: any) =>
        (
          value?.[this.fieldDiaplayname] ?
            value?.[this.fieldDiaplayname].toLowerCase().indexOf(search) > -1
            :
            value?.toLowerCase()?.indexOf(search) > -1
        )
      )
    );
  }

  onToggleAll() {
    this.isToggleAll = !this.isToggleAll;
    this.valueCtrl.setValue(this.isToggleAll ? (this.originalData.map(el => this.fieldValuename ? el[this.fieldValuename] : el)) : []);
  }
}
