import { Component, OnInit } from '@angular/core';
import { NavigationExtras, Router } from '@angular/router';
import { CareerDevelopmentService } from '../../career-development.service';
import { CoreUrl } from '@core/src/lib/shared/util/url.constant';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { API } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';

@Component({
  selector: 'eet-career-roadmap',
  templateUrl: './career-roadmap.component.html',
  styleUrls: ['./career-roadmap.component.scss']
})
export class CareerRoadmapComponent implements OnInit {

  constructor(private router: Router, private careerDevelopmentService: CareerDevelopmentService) { }

  ngOnInit(): void {
  }

  nagivegateToTechComp(data: string) {
    const nagivationExtra: NavigationExtras = {
      queryParams: {
        param: data
      }
    };
    this.router.navigate([`${CoreUrl.COMPETENCE_DEVELOPMENT}/technical-competencies`], nagivationExtra);
    if (data == 'tech')
      this.careerDevelopmentService.techComp = false;
    else this.careerDevelopmentService.techComp = true;
  }

}
