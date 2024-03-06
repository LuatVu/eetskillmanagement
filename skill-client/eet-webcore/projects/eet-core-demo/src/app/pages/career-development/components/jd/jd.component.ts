import { Component, OnInit, ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'eet-jd',
  templateUrl: './jd.component.html',
  styleUrls: ['./jd.component.scss']
})
export class JdComponent implements OnInit {
  listJD = [1,2,3,4,5,6,7,8,9,10]
  constructor(
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
  }
  ngAfterViewChecked(): void {
    this.changeDetectorRef.detectChanges();
  }
  isElementOverflow(element : any){
    return element.offsetHeight < element.scrollHeight ||
      element.offsetWidth < element.scrollWidth;
  }
}
