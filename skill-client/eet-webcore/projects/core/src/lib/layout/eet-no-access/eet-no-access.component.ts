import { Component, OnInit, Inject } from '@angular/core';
import { CoreUrl } from '../../shared/util/url.constant';
import { APP_BASE_HREF } from '@angular/common';

@Component({
  selector: 'eet-eet-no-access',
  templateUrl: './eet-no-access.component.html',
  styleUrls: ['./eet-no-access.component.scss']
})
export class EetNoAccessComponent implements OnInit {


  requestLink = `/${CoreUrl.AUTHENTICATE}/${CoreUrl.REQUEST_ACCESS}`;

  constructor(
    @Inject(APP_BASE_HREF) public baseHref: string
  ) {}

  ngOnInit() {}

}
