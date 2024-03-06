import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output, OnChanges, AfterViewInit, SimpleChanges } from '@angular/core';
import { ToggleDataEmitModel } from './model';
@Component({
  selector: 'eet-toggle',
  templateUrl: './toggle.component.html',
  styleUrls: ['./toggle.component.scss']
})

export class ToggleComponent implements OnInit, OnChanges {
  @Input() listData: Array<string> = [];
  @Input() checked:boolean=false
  @Input() data:string=''
  @Input() disabled:boolean=false
  @Output() toggleChange: EventEmitter<ToggleDataEmitModel> = new EventEmitter<ToggleDataEmitModel>();
  constructor(private cd: ChangeDetectorRef) { }


  ngOnInit(): void {
    if(this.data && this.listData.includes(this.data)) return;
    this.onToggle()
  }
  onToggle() {
    if(this.checked){
      this.data = this.listData[1]
    }else{
      this.data = this.listData[0]
    }
    this.toggleChange.emit({checked:this.checked,data:this.data})
  }
  truncateLabel(label:string,num:number=15) {
    return label?.length > num? label.slice(0,num) + "..." : label
  }
  handleLeftLabel() {
    if (!this.disabled) {
      this.checked = !this.checked
      this.onToggle();
    }
  }
  onChange(){
    this.data = this.checked? this.listData[1]: this.listData[0];
    this.toggleChange.emit({checked:this.checked,data:this.data});
  }
  ngOnChanges(changes: SimpleChanges): void {
    if(changes?.checked){
      setTimeout(() => {
        this.data = changes?.checked?.currentValue? this.listData[1]: this.listData[0];
        this.toggleChange.emit({checked:this.checked,data:this.data});
      }, 100);
    }
  }
}
