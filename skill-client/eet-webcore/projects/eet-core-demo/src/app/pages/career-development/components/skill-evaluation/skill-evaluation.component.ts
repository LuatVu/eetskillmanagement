import { Component, OnInit } from '@angular/core';
import { PaginDirectionUtil } from 'projects/eet-core-demo/src/app/shared/utils/paginDirectionUtil';
@Component({
  selector: 'eet-skill-evaluation',
  templateUrl: './skill-evaluation.component.html',
  styleUrls: ['./skill-evaluation.component.scss']
})
export class SkillEvaluationComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }
  ngOnDestroy(): void {
    PaginDirectionUtil.removeEventListenerForDirectionExpandPagination()
  }
}
