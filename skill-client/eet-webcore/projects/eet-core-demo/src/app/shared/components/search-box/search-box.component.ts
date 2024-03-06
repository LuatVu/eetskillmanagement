import { Component, OnInit, Output, EventEmitter, Input, OnDestroy } from '@angular/core';

@Component({
  selector: 'eet-search-box',
  templateUrl: './search-box.component.html',
  styleUrls: ['./search-box.component.scss']
})
export class SearchBoxComponent implements OnInit, OnDestroy {
  @Output() onApplySearch: EventEmitter<string> = new EventEmitter();
  public searchKey: string = '';

  constructor() { }

  ngOnInit() {
  }

  onKeyUp() {
    this.onApplySearch.emit(this.searchKey);
  }

  onRemove() {
    this.searchKey = '';
    this.onApplySearch.emit('');
  }

  onSearchClick() {
    this.onApplySearch.emit(this.searchKey);
  }

  ngOnDestroy(): void {
    this.searchKey = '';
    this.onApplySearch.emit('');
  }
}
