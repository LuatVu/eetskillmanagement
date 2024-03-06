import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { ChangeDetectorRef, Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatAutocompleteSelectedEvent, MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatChipInputEvent } from '@angular/material/chips';
import { map, Observable, startWith } from 'rxjs';
import { Helpers } from '../../utils/helper';

@Component({
  selector: 'eet-common-chip',
  templateUrl: './common-chip.component.html',
  styleUrls: ['./common-chip.component.scss']
})
export class CommonChipComponent implements OnInit {
  @ViewChild('chipInput') chipInputElement: ElementRef<HTMLInputElement>;
  @ViewChild('chipInput', { static: false, read: MatAutocompleteTrigger }) autoCompleteTrigger: MatAutocompleteTrigger;
  @Output() selectedChipChange: EventEmitter<Array<any>> = new EventEmitter<Array<any>>();
  @Output() onSearchChange: EventEmitter<string> = new EventEmitter<string>();

  @Input() typeChip: 'string' | 'object' = 'string';
  @Input() objectDisplayName: string = 'name';
  @Input() objectValueName: string = '';
  @Input() isAddOnBlur: boolean = false;
  @Input() selectedChipList: Array<any> = [];
  @Input() isDisabled: boolean = false;
  @Input() originalChipList: Array<any> = [];
  @Input() isServerSearch: boolean = false;

  public separatorKeysCodes: number[] = [ENTER, COMMA];
  public chipControl: FormControl = new FormControl();
  public filteredChipList: Observable<Array<any>>;

  constructor() {
    this.filteredChipList = this.chipControl.valueChanges.pipe(
      startWith(null),
      map((chip: any | null) => {
        return chip ? this._filter(chip) : this.originalChipList.slice()
      })
    );
  }

  ngOnInit() {
  }

  onRemoveChip(chip: any) {
    const index = this.selectedChipList.indexOf(chip);

    if (index >= 0) {
      this.selectedChipList.splice(index, 1);
      this.selectedChipChange.emit(this.selectedChipList);
    }
  }

  onAddChip(event: MatChipInputEvent) {
    if (!this.isAddOnBlur) {
      return;
    }

    const value = (event.value || '').trim();

    // Add our chip
    if (value &&
      !this._checkIsChipExistedInList(value)
    ) {
      if (this.typeChip == 'object') {
        this.selectedChipList.push({ [this.objectDisplayName]: value });
      } else {
        this.selectedChipList.push(value);
      }
      this.selectedChipChange.emit(this.selectedChipList);
    }

    // Clear the input value
    event.chipInput!.clear();

    this.chipControl.setValue(null);
  }

  onSelectedChip(event: MatAutocompleteSelectedEvent) {
    this.selectedChipList.push(event.option.value);
    this.selectedChipChange.emit(this.selectedChipList);
  }

  private _filter(value: any): Array<any> {
    if (typeof (value) == 'object') {
      this.chipControl.reset();
      return [];
    }
    const filterValue = value?.toLowerCase();
    return this.originalChipList.filter(chip =>
      this.typeChip == 'string' ? chip?.toLowerCase().includes(filterValue) : (chip?.[this.objectDisplayName] as string).toLowerCase().includes(filterValue)
    );
  }

  private _checkIsChipExistedInList(value: string | any): boolean {
    if (this.typeChip == 'object') {
      return Boolean(this.selectedChipList.find((el: any) => el?.[this.objectDisplayName] == value));
    } else {
      return this.selectedChipList.includes(value)
    }
  }

}
