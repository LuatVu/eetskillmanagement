import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { DialogCommonService } from '../../../../../shared/services/dialog-common.service';
import * as constants from '../../constants/constants';

@Component({
  selector: 'eet-common-list',
  templateUrl: './common-list.component.html',
  styleUrls: ['./common-list.component.scss'],
})
export class CommonListComponent implements OnInit, OnChanges {
  @Input() sourceList: any[] = [];
  @Input() type: number = 1;
  @Input() showDeleteIcon: boolean = true;
  @Input() showEditIcon: boolean = false;
  @Input() truncateTextNumber:number
  @Input() defaultItem?: any;
  @Output() onSelectItemInList = new EventEmitter<any>();
  @Output() onItemDeleted = new EventEmitter<any>();
  @Output() onEdit = new EventEmitter<any>();
  public keyword: string = '';
  @Input() public selectedItem: any;
  public btnName: string = '';
  public name: string = '';
  public readonly constant = constants;

  private originSourceList: any[] = [];

  constructor(
    public dialog: MatDialog,
    private commonDialogService: DialogCommonService,
    public translate: TranslateService,
    private loader: LoadingService
  ) { }

  ngOnChanges(changes: SimpleChanges): void {
    if (!changes.selectedItem || this.originSourceList.length === 0) this.originSourceList = this.createTempSourceList(this.sourceList);
    if (changes.sourceList) {
      this.keyword = '';
      this.originSourceList = this.createTempSourceList(this.sourceList);
    }
  }
  
  ngOnInit(): void { 
  }
  
  ngAfterViewInit() {
    if(this.defaultItem) {
      this.selectItem(this.defaultItem);
    }
  }

  searchKeyword() {
    this.sourceList = this.originSourceList.filter((item) => {
      if (item?.name?.toLowerCase().indexOf(this.keyword.toLowerCase()) > -1 ||
      item?.displayName?.toLowerCase().indexOf(this.keyword.toLowerCase()) > -1) {
        return true;
      }
      return false;
    });
  }

  createTempSourceList(sourceList: any[]): any[] {
    let tempSourceList: any[] = [];
    // if(this.type == 1) {
    sourceList.forEach(function (element) {
      let tempObject = {};
      tempObject = element;
      tempSourceList.push(tempObject);
    });
    // }
    return tempSourceList;
  }

  selectItem(item: any) {
    this.onSelectItemInList.emit(item);
    // set scss for selected item in list
    this.selectedItem = this.selectedItem
  }

  deleteItem(item: any) {
    const dialogRef = this.commonDialogService.onOpenConfirm({
      content: this.translate.instant('user_management.dialog.title_message_content_confirmation_remove', { messageParam: item.name || item?.displayName }),
      title: this.translate.instant('user_management.dialog.title_confirm'),
      btnConfirm: this.translate.instant('Yes'),
      btnCancel: this.translate.instant('No'),
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.onItemDeleted.emit(item);
      }
    });
  }

  editItem(item: any) {
    this.onEdit.emit(item);
  }

  handleTruncateLabel(name:string) {
    return name?.length > this.truncateTextNumber? name.slice(0,this.truncateTextNumber) + "..." : name
  }
}
