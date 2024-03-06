import { APP_BASE_HREF } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';

@Component({
  selector: 'app-eet-waiting-for-approval',
  templateUrl: './eet-waiting-for-approval.component.html',
  styleUrls: ['./eet-waiting-for-approval.component.scss']
})
export class EetWaitingForApprovalComponent implements OnInit {

  constructor(@Inject(APP_BASE_HREF) public baseHref: string) { }

  ngOnInit() {
  }

}
