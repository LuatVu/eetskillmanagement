import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ElasticService } from 'projects/eet-core-demo/src/app/shared/services/elastic.service';
import { debounceTime, finalize, ReplaySubject, startWith, Subscription, takeUntil } from 'rxjs';

@Component({
  selector: 'eet-add-competency-lead-dialog',
  templateUrl: './add-competency-lead-dialog.component.html',
  styleUrls: ['./add-competency-lead-dialog.component.scss']
})
export class AddCompetencyLeadDialogComponent implements OnInit {
  public competencyLead: FormControl = new FormControl(null, [Validators.required])

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private elasticService: ElasticService, public dialogRef: MatDialogRef<AddCompetencyLeadDialogComponent>) { }

  ngOnInit(): void {
    this.competencyLead.valueChanges.pipe(takeUntil(this.destroyed$),startWith(null), debounceTime(500)).subscribe(res => {
      if (res) {
        if (this.subscription) {
          this.subscription.unsubscribe()
        }
        this.findMembers(res)
      } else {
        this.optionList = []
        this.isShowLoading = false
      }
    })
  }
  onConfirm() {
    this.dialogRef.close(this.currentMemberSearch)
  }
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  private subscription: Subscription;
  public currentMemberSearch: any = null
  public isShowLoading: boolean = false;
  public optionList: any[] = [];
  onSearchChange() {
    this.isShowLoading = true;
  }
  displayFn(value: any): string {
    return value && value ? value : '';
  }

  selectOption(value: any) {
    if (!value || value.length === 0) {
      return;
    }
    this.currentMemberSearch = {
      status: true,
      personal_id: value.personalId,
      display_name: value.displayName
    }
    this.competencyLead.setValue(value.displayName)
    this.isShowLoading = false;
  }
  findMembers(searchKey: string) {
    if (!this.competencyLead.value 
      || this.competencyLead.value !== null && this.currentMemberSearch !== null && searchKey !== this.currentMemberSearch.display_name) {
      this.currentMemberSearch = null;
    }
    this.isShowLoading = true;
    this.elasticService.getDocument('personal', {
      query: searchKey
    }).pipe(
      takeUntil(this.destroyed$),
      finalize(() => {
        this.isShowLoading = false;
      })
    ).subscribe(data=>{
      const competencyLeads = this.data.data.competencyLeads;
      this.optionList = data.arrayItems.filter((user: any) => !competencyLeads.includes(user.id));
    })
    
  }


  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

}
