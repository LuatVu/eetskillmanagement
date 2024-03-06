import { APP_BASE_HREF } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, HostListener, Inject, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ConfirmDialogComponent } from 'projects/eet-core-demo/src/app/shared/components/dialogs/confirm-dialog/confirm-dialog.component';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { catchError, finalize } from 'rxjs/operators';
import { TagSkill } from '../../../model/common-config/common-config.model';
import { CommonConfigService } from '../../../services/common-config.service';
import { CompetencyLeadService } from '../../../services/competency-lead.service';
import { ReplaceTagSkillDialogComponent } from '../../dialogs/common-config/replace-tag-skill-dialog/replace-tag-skill-dialog.component';
import { COMMON_CONFIG_CONSTANST } from './common-config.constanst';
import { CanDeactivateGuard } from 'projects/eet-core-demo/src/app/shared/utils/can-deactivate.guard';
@Component({
  selector: 'eet-common-config',
  templateUrl: './common-config.component.html',
  styleUrls: ['./common-config.component.scss']
})
export class CommonConfigComponent implements OnInit, CanDeactivateGuard {
  @HostListener('window:beforeunload', ['$event'])
  onWindowClose(event: any): void {
    if (this.isEditMode) {
      event.preventDefault();
      event.returnValue = false;
    }
  }
  public tagsOriginal: any[] = []
  public tempTagArray: TagSkill[] = []
  public tagsArrayResult: TagSkill[] = []
  public isEditMode: boolean = false
  public isChecked: boolean
  public isFilterMandatory: boolean = true
  private filterArrValue = [
    {
      id: COMMON_CONFIG_CONSTANST.LABEL_FILTER.MANDATORY_CHECKBOX,
      value: this.isFilterMandatory
    },
    {
      id: COMMON_CONFIG_CONSTANST.LABEL_FILTER.SEARCH_BOX,
      value: ''
    }
  ]
  constructor(
    @Inject(APP_BASE_HREF) public baseHref: string,
    public dialog: DialogCommonService,
    private translate: TranslateService,
    public comLoader: LoadingService,
    public notificationService: NotificationService,
    public competencyLeadService: CompetencyLeadService,
    public commonConfigService: CommonConfigService,
    public MatDialog: MatDialog,
  ) { }

  ngOnInit(): void {
    this.getTags()
  }
  getTags() {
    const loader = this.comLoader.showProgressBar()
    this.commonConfigService.getTagSkill().pipe(finalize(() => {
      this.comLoader.hideProgressBar(loader)
    })).subscribe((resp) => {
      this.tagsOriginal = (resp.data.map((e: TagSkill, i: number) => ({ ...e, index: i })) as Array<TagSkill>)
      this.tempTagArray = Helpers.cloneDeep(this.tagsOriginal)
      this.tagsArrayResult = Helpers.cloneDeep(this.tagsOriginal)
      this.combineFilter()
    })
  }
  showReplaceDialog(tagSkillSelected: any) {
    const dialogRef = this.dialog.onOpenCommonDialog({
      component: ReplaceTagSkillDialogComponent,
      title: this.translate.instant('system.common_config.dialog.replace_tag_skill.title'),
      width: "615px",
      height: 'auto',
      type: 'view',
      passingData: {
        tagSkillSelected,
        tagsAvailable: this.tagsOriginal.filter((e) => { return e.name != tagSkillSelected.name }),
        tempTagArray: this.tempTagArray,
        clearAndFocusInput: this.clearAndFocusInput,
      }
    });
    const dialogComponent = dialogRef.componentInstance as ReplaceTagSkillDialogComponent
    dialogComponent.replaceTagEvent.subscribe(res => {
      if (res) {
        this.resetFilter()
        this.getTags()
      }
    })
    dialogRef.afterClosed().subscribe((response) => {
    })
  }
  onSearch(event: any) {
    this.filterArrValue.find((e) => e.id === COMMON_CONFIG_CONSTANST.LABEL_FILTER.SEARCH_BOX)!.value = event.toLowerCase().trim()
    this.combineFilter()
  }

  filterByName(currentNameFilter: string, data: TagSkill[]) {
    return (JSON.parse(JSON.stringify(data)) as TagSkill[]).filter(
      filterValue => filterValue.name.toLowerCase().trim().includes(currentNameFilter.toLowerCase().trim())
    )
  }

  onSave() {
    const loader = this.comLoader.showProgressBar()
    this.commonConfigService.updateOrderTagSkill(this.tagsArrayResult).pipe(
      finalize(() => {
        this.comLoader.hideProgressBar(loader)
      }),
      catchError((error: HttpErrorResponse) => {
        return [error.error];
      })
    ).subscribe((response) => {
      if (response.code === CONFIG.API_RESPONSE_STATUS.UPDATE_ORDER_TAG_SUCCESSFUL) {
        this.notificationService.success(this.translate.instant('system.common_config.save_success'));
        this.isEditMode = false
        this.clearAndFocusInput()
        this.resetFilter()
        this.getTags()
      } else {
        this.notificationService.error(this.translate.instant('system.common_config.save_failed'));
      }
    })
  }
  handleCancel() {
    const dialogRef = this.MatDialog.open(ConfirmDialogComponent, {
      data: {
        title: this.translate.instant('editor.dialog.confirm.title'),
        content: this.translate.instant('editor.dialog.confirm.content'),
        btnConfirm: this.translate.instant('editor.dialog.confirm.yes'),
        btnCancel: this.translate.instant('editor.dialog.confirm.no'),
        icon: 'a-icon ui-ic-alert-warning',
      },
      width: "420px"
    },
    )
    dialogRef.afterClosed().subscribe((response: boolean) => {
      if (response) {
        this.tempTagArray = Helpers.cloneDeep(this.tagsOriginal);
        this.tagsArrayResult = Helpers.cloneDeep(this.tagsOriginal)
        this.isEditMode = false
        this.clearAndFocusInput()
        this.resetFilter()
      }
    })
  }
  clearAndFocusInput() {
    (document.getElementById("inputNameTag") as HTMLInputElement).value = "";
    (document.getElementById("inputNameTag") as HTMLInputElement).focus();
  }
  editTags() {
    this.isEditMode = true
  }
  handleCheckboxMandatoryForTag(e: any, tag: TagSkill) {
    tag.is_mandatory = e
    this.tagsArrayResult.map((item: TagSkill) => {
      if (item.id === tag.id) {
        item.is_mandatory = e
      }
    })
  }
  handleSetCheckMandatory(tag: TagSkill) {
    if (!this.isEditMode) return
    this.handleCheckboxMandatoryForTag(!tag.is_mandatory, tag)
  }

  filterMandatory(e: boolean) {
    this.isFilterMandatory = e
    this.filterArrValue.find((e) => e.id === COMMON_CONFIG_CONSTANST.LABEL_FILTER.MANDATORY_CHECKBOX)!.value = e
    this.combineFilter()
  }

  combineFilter() {
    this.tempTagArray = this.tagsArrayResult.filter((e: TagSkill) => {
      const c1 = !this.filterArrValue[0].value || e.is_mandatory
      const c2 = !this.filterArrValue[1].value || e.name.toLowerCase().includes(this.filterArrValue[1].value.toString().toLowerCase())
      return c1 && c2
    })
  }
  resetFilter() {
    this.onSearch('')
    this.filterMandatory(true)
  }

  canDeactivate(): boolean | Promise<boolean> | import('rxjs').Observable<boolean> { 
    if(this.isEditMode) {
      this.commonConfigService.setOldPageIndex(5)
      const confirmation = confirm('This page is asking you to confirm that you want to leave — information you\'ve entered may not be saved.');
      this.commonConfigService.setConfirm(confirmation);
      return confirmation;
    } else {
        this.commonConfigService.setConfirm(true)
        return true;
    }
  }
}

