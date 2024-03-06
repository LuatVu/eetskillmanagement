import { Component, OnInit, EventEmitter, Inject, Output } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { TagSkill } from '../../../../model/common-config/common-config.model';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { ConfirmDialogComponent } from '../../../../../../shared/components/dialogs/confirm-dialog/confirm-dialog.component';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { TranslateService } from '@ngx-translate/core';
import { CommonConfigService } from '../../../../services/common-config.service';
import { finalize } from 'rxjs/operators';
@Component({
  selector: 'eet-replace-tag-skill-dialog',
  templateUrl: './replace-tag-skill-dialog.component.html',
  styleUrls: ['./replace-tag-skill-dialog.component.scss']
})
export class ReplaceTagSkillDialogComponent implements OnInit {
  public tagSkillSelected: TagSkill
  public tagsAvailable: TagSkill[]
  public tagsFilter: TagSkill[];
  public searchString?: string;
  public tempTagArray: TagSkill[]

  @Output() replaceTagEvent: EventEmitter<boolean> = new EventEmitter<boolean>();
  constructor(public replaceDialogRef: MatDialogRef<ReplaceTagSkillDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
    public dialog: MatDialog,
    public notificationService: NotificationService,
    public comLoader: LoadingService,
    private translate: TranslateService,
    public commonConfigService:CommonConfigService) {
    this.tagSkillSelected = this.dialogData.data.tagSkillSelected;
    this.tagsAvailable = this.dialogData.data.tagsAvailable;
  }

  ngOnInit(): void {
    this.tagsFilter = this.tagsAvailable
  }

  onSearch(event: any) {
    const currentNameFilter = (event.target as HTMLInputElement).value
    if (currentNameFilter.length && currentNameFilter.length !== 0) {
      this.tagsFilter = this.filterByName(currentNameFilter, JSON.parse(JSON.stringify(this.tagsAvailable)))
    }
    else {
      this.tagsFilter = (JSON.parse(JSON.stringify(this.tagsAvailable)))
    }
  }

  filterByName(currentNameFilter: string, data: TagSkill[]) {
    return (JSON.parse(JSON.stringify(data)) as TagSkill[]).filter(
      filterValue => filterValue.name.toLowerCase().trim().includes(currentNameFilter.toLowerCase().trim())
    )
  }

  handleReplaceTag(tagReplace: TagSkill) {
    const replaceDialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: this.translate.instant('system.common_config.dialog.confirm.title'),
        content: this.translate.instant('system.common_config.dialog.confirm.content.name_line') + " " + this.tagSkillSelected.name + " " + this.translate.instant('system.common_config.dialog.confirm.content.to') + " " + tagReplace.name,
        btnConfirm: this.translate.instant('system.common_config.dialog.confirm.yes'),
        btnCancel: this.translate.instant('system.common_config.dialog.confirm.no')
      },
      width: "420px"
    },
    )
    replaceDialogRef.afterClosed().subscribe(response => {
      if (response) {
        this.replaceTag([this.tagSkillSelected,tagReplace])
        this.replaceDialogRef.close()
        this.dialogData.data.clearAndFocusInput()
      }
    })
  }
  replaceTag(listTagSkillReplace:TagSkill[]) {
    const loader = this.comLoader.showProgressBar()
    this.commonConfigService.replaceTag(listTagSkillReplace).pipe(finalize(() => {
      this.comLoader.hideProgressBar(loader)
    })).subscribe((resp) => {
      if(resp){
        // emit replace event to get all tags
        this.replaceTagEvent.emit(true);
        this.notificationService.success(this.translate.instant('system.common_config.dialog.confirm.success'));
      }else{
        this.notificationService.success(this.translate.instant('system.common_config.dialog.confirm.failed'));
      }
      
    })
    

   

  }

}
