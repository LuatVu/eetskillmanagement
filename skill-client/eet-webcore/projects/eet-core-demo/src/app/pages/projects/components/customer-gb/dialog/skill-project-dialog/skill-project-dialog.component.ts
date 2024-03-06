import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { VModelService } from 'projects/eet-core-demo/src/app/pages/v-model/v-model.service';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { PermisisonService } from 'projects/eet-core-demo/src/app/shared/services/permisison.service';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';
import { finalize } from 'rxjs/operators';
import { VModelProjectModel } from '../../../../models/projects.model';
import { ProjectsService } from '../../../../services/projects.service';
import { BoschProjectDetailComponent } from '../../../bosch-project-detail/bosch-project-detail.component';

@Component({
  selector: 'eet-skill-project-dialog',
  templateUrl: './skill-project-dialog.component.html',
  styleUrls: ['./skill-project-dialog.component.scss'],
})
export class SkillProjectDialogComponent implements OnInit {
  projectLists: VModelProjectModel[] = [];
  projectListsFilter: VModelProjectModel[] = [];
  data: any;
  V_MODEL = CONFIG.PROJECT.V_MODEL;
  HAS_VIEW_PROJECT_DETAIL: boolean = false;
  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
    private dialogCommonService: DialogCommonService,
    private permissionService: PermisisonService,
    private translateService: TranslateService,
    private projectService: ProjectsService,
    private loadService: LoadingService,
    private vModelService: VModelService
  ) {}

  ngOnInit() {
    this.data = this.dialogData.data || {};
    this.HAS_VIEW_PROJECT_DETAIL = this.permissionService.hasPermission(
      CONFIG.PERMISSIONS.VIEW_PROJECT_DETAIL
    );
    if (this.data) {
      switch (this.data.skillTag) {
        case this.V_MODEL:
          this.getProjectByVModel(this.data.customerGb.name);
          break;
        default:
          this.getProjectList(this.data.customerGb.id, this.data.skillTag);
          break;
      }
    }
  }
  onSearch(event: any) {
    const searchValue = event.target?.value?.trim().toLowerCase();
    this.projectListsFilter = Helpers.cloneDeep(
      this.projectLists.filter((item: any) =>
        item.name.toLowerCase().includes(searchValue)
      )
    );
  }
  onClickProject(project: any) {
    if (!this.HAS_VIEW_PROJECT_DETAIL) return;
    this.dialogCommonService.onOpenCommonDialog({
      component: BoschProjectDetailComponent,
      title: this.translateService.instant('projects.detail.title'),
      width: '80vw',
      height: 'auto',
      icon: 'a-icon ui-ic-watch-on',
      type: 'view',
      passingData: {
        type: 'view',
        project_id: project.project_id,
      },
    });
  }
  getProjectList(id: string, skillTag: string) {
    const loader = this.loadService.showProgressBar();
    this.projectService
      .getProjectByCustomerIdAndSkillTag(id, skillTag)
      .pipe(finalize(() => this.loadService.hideProgressBar(loader)))
      .subscribe((response) => {
        if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.projectLists = response.data;
          this.projectListsFilter = Helpers.cloneDeep(response.data);
        }
      });
  }
  getProjectByVModel(gbName: string) {
    let phaseData: VModelProjectModel[] = [];
    let pjNames: string[] = [];
    const loader = this.loadService.showProgressBar();
    this.vModelService
      .getPhaseData()
      .pipe(
        finalize(() => {
          this.loadService.hideProgressBar(loader);
        })
      )
      .subscribe((result) => {
        if (result.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          result?.data?.phases?.forEach((phase: any) => {
            phase?.projects?.forEach((project: any) => {
              if (project?.customer_gb == gbName) {
                if (!pjNames.includes(project?.name)) {
                  const projectV: VModelProjectModel = {
                    name: project?.name,
                    project_id: project?.project_id,
                  };
                  phaseData.push(projectV);
                  pjNames.push(project?.name);
                }
              }
            });
          });
          this.projectLists = phaseData;
          this.projectListsFilter = Helpers.cloneDeep(this.projectLists);
        }
      });
  }
}
