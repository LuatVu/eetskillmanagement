import {
  AfterViewInit,
  Component,
  EventEmitter,
  HostListener,
  Input,
  OnDestroy,
  OnInit,
  OnChanges,
  SimpleChanges,
  Output,
} from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { LoadingService } from '../../services/loading.service';
import { NotificationService } from '@bci-web-core/core';
import { finalize } from 'rxjs/operators';
import { LocalStorageService } from '@core/src/lib/shared/services/storage.service';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../dialogs/confirm-dialog/confirm-dialog.component';
import { TranslateService } from '@ngx-translate/core';
import { NavigationStart, Router } from '@angular/router';
import { LayoutService, statusFailedForSavingEditorModel } from '../../../pages/overview/layout.service';
import { isNull } from '@angular/compiler/src/output/output_ast';

@Component({
  selector: 'eet-editor',
  templateUrl: './editor.component.html',
  styleUrls: ['./editor.component.scss'],
})
export class EditorComponent implements OnInit, OnChanges,OnDestroy {
  @Output() typeEvent: EventEmitter<any> = new EventEmitter<any>();
  @Input() layoutType:string
  @Input() defaultData: string;
  @Input() hasPermission: boolean;
  @HostListener('window:beforeunload', ['$event'])
  onWindowClose(event: any): void {
    if (this.isEdit || this.isPreview) {
      event.preventDefault();
      event.returnValue = false;
    }
  }
  editor: string;
  public isEdit: boolean = false;
  public isInit: boolean = true;
  public isPreview: boolean = false;
  public currentData:string = ''
  constructor(
    private sanitizer: DomSanitizer,
    private localStorage: LocalStorageService,
    public dialog: MatDialog,
    private translate: TranslateService,
    private router: Router,
    private layoutService:LayoutService
  ) {}
  ngOnDestroy(): void {
    
  }

  ngOnInit(): void {
    this.initState();

    this.router.events.subscribe((event) => {
      // route change
      if (event instanceof NavigationStart) {
      }
    });


    this.layoutService._setDataForLayout.subscribe((res:any) => {
    if(res.layoutType === this.layoutType) {
      this.editor = res.data
      this.currentData = res.data
    }
    
    })

    this.layoutService.statusSaveEditor.subscribe((res:statusFailedForSavingEditorModel) => {
      if(res.layoutType === this.layoutType){
        if(res.isFailed) {
          this.onEdit()
          this.editor = res.data || ''
        }
      }
    })
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.defaultData && !changes.defaultData.firstChange) {
      this.editor = this.defaultData;
    }
  }

  onSave() {
    this.initState();
    if(this.layoutType) {
      this.typeEvent.emit({ type: 'save', data: this.editor ? this.editor : '<p></p>',layoutType:this.layoutType});
    }else{
      this.typeEvent.emit({ type: 'save', data: this.editor ? this.editor : '<p></p>' });
    }
  }
  onCancel() {
    const confirmDialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: this.translate.instant('editor.dialog.confirm.title'),
        content: this.translate.instant('editor.dialog.confirm.content'),
        btnConfirm: this.translate.instant('editor.dialog.confirm.yes'),
        btnCancel: this.translate.instant('editor.dialog.confirm.no'),
        icon: 'a-icon ui-ic-alert-warning',
      },
      width: '420px',
    });
    confirmDialogRef.afterClosed().subscribe((response) => {
      if (response) {
        this.editor = this.currentData;
        this.initState();
        this.typeEvent.emit({ type: 'cancel' });
      }
    });
  }
  onPreview() {
    this.previewState();
    this.typeEvent.emit({ type: 'preview', data: this.editor });
  }
  onEdit() {
    this.editState();
    this.typeEvent.emit({ type: 'edit' });
  }

  initState() {
    this.isInit = true;
    this.isEdit = false;
    this.isPreview = false;
  }
  editState() {
    this.isEdit = true;
    this.isPreview = false;
    this.isInit = false;
  }
  previewState() {
    this.isEdit = false;
    this.isPreview = true;
    this.isInit = false;
  }
  byPassHTML(html: string) {
    if(typeof(html)==='undefined') return
    return this.sanitizer.bypassSecurityTrustHtml(html);
  }
  onChange(e: any) {}
  // checkConditionForSetEditorValue() {
  //   this.editor = this.defaultData;
  //   if (typeof (this.localStorage.get(this.type)) === 'object') {
  //     this.editor = this.defaultData
  //   } else {
  //     this.editor = this.localStorage.get(this.type)
  //   }
  // }
}
