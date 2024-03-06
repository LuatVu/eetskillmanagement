import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl } from '@angular/forms';
import { LdapUserDistributionModel } from 'projects/eet-core-demo/src/app/shared/models/group.model';
import { finalize, Subscription } from 'rxjs';
import { debounceTime, startWith } from 'rxjs/operators';
import { UserManagementService } from '../../../services/user-management.service';

@Component({
  selector: 'eet-add-user-or-group',
  templateUrl: './add-user-or-group.component.html',
  styleUrls: ['./add-user-or-group.component.scss']
})
export class AddUserOrGroupComponent implements OnInit {
  @Input() searchTitle: string = '';
  @Input() selectedTitle: string = '';
  @Input() searchType: 'User' | 'DistributionList' = 'User';
  @Input() selectedOptionList: LdapUserDistributionModel[] = [];
  @Output() selectedOptionChange: EventEmitter<LdapUserDistributionModel[]> = new EventEmitter();

  public searchControl = new FormControl();
  public optionList: any[] = [];

  public isShowLoading: boolean = false;
  private subscription!: Subscription;

  constructor(private userManagementService: UserManagementService) {
    this.searchControl.valueChanges.pipe(startWith(null), debounceTime(500)).subscribe(res => {
      if (res) {
        if (this.subscription) {
          this.subscription.unsubscribe();
        }
        this.fetchOptionData(this.searchType, res);
      } else {
        this.optionList = [];
        this.isShowLoading = false;
      }
    })
  }

  ngOnInit() {
  }

  displayFn(value: any): string {
    return value && value ? value : '';
  }

  selecteOption(value: any) {
    let duplicated = false;
    this.selectedOptionList.forEach(selectedItem => {
      if(selectedItem.displayName === value.displayName){
        duplicated = true;
      }
    })
    this.searchControl.reset();
    if(!duplicated){
      this.selectedOptionList.push(value);
    }
    this.isShowLoading = false;
    this.selectedOptionChange.emit(this.selectedOptionList);
  }

  removeOption(index: number) {
    this.selectedOptionList.splice(index, 1);
  }
  onSearchChange() {
    this.isShowLoading = true;
  }

  fetchOptionData(type: string, searchKey: string) {
    this.isShowLoading = true;
    if (type == 'User') {
      this.subscription = this.userManagementService.searchLDAPUsers(searchKey)
        .pipe(
          finalize(() => {
            this.isShowLoading = false;
          })
        )
        .subscribe((res: any) => {
          this.optionList = res?.data || [];
        });
    } else {
      this.subscription = this.userManagementService.searchLDAPDistribution(searchKey)
        .pipe(
          finalize(() => {
            this.isShowLoading = false;
          })
        )
        .subscribe((res: any) => {
          this.optionList = res?.data || [];
        });
    }
  }



}
