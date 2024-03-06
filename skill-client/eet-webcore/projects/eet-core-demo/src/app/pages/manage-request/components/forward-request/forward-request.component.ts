import {
  AfterViewInit,
  Component,
  Inject,
  OnInit,
  ViewChild,
} from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { finalize } from 'rxjs/operators';
import { CompetencyGroupModel, CompetencyModel, SkillClusterModel } from '../../models/manage-request.model';
import { ForwardRequestService } from './forward-request.service';
import { TranslateService } from '@ngx-translate/core';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';

@Component({
  selector: 'eet-forward-request',
  templateUrl: './forward-request.component.html',
  styleUrls: ['./forward-request.component.scss'],
})
export class ForwardRequestComponent implements OnInit, AfterViewInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort, { static: false }) sort!: MatSort;

  public approverList: CompetencyGroupModel[] = [];
  public selectedApproverList: CompetencyModel[] = [];
  public isMenuOpened: boolean = true;
  public dataSource = new MatTableDataSource<CompetencyGroupModel>();
  public displayedColumns: string[] = ['name', 'competency', 'forward'];
  readonly paginationOption = CONFIG.PAGINATION_OPTIONS;
  private requestId!: string;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private forwardRequestService: ForwardRequestService,
    private notifyService: NotificationService,
    private translate: TranslateService,
    private loader: LoadingService
  ) { }
  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  ngOnInit(): void {
    this.requestId = this.data.data.requestId;
    this.getAllApproverList();
  }

  getAllApproverList() {
    const loader = this.loader.showProgressBar();
    this.forwardRequestService
      .getCompetencyLeadByRequest(this.requestId)
      .pipe(finalize(() => this.loader.hideProgressBar(loader)))
      .subscribe((response) => {
        if(response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS){
          this.approverList = this.convertToCompetencyGroupModel(response.data);

          this.dataSource = new MatTableDataSource(this.approverList);
          this.dataSource.paginator = this.paginator;
          this.dataSource.sort = this.sort;
        }
      });
  }

  onOpenMenu() {
    this.isMenuOpened = !this.isMenuOpened;
  }

  onSelctedChange(event: CompetencyModel[]) {
    this.selectedApproverList = event;
  }

  onSave() {
    const loader = this.loader.showProgressBar();
    this.forwardRequestService
      .forwardCompetencyLead(
        this.requestId,
        this.approverList.filter(f => f.isChecked).map(m => m.personal_id)
      )
      .pipe(finalize(() => this.loader.hideProgressBar(loader)))
      .subscribe(({ code }) => {
        if (code == 'SUCCESS') {
          this.notifyService.success(this.translate.instant('notification.forward_successfully'))
        }
      });
  }
  onChangeCheckbox(element: CompetencyGroupModel){
    const skillClusters = element.skill_clusters.map(item => item.skill_group_id);
    this.dataSource.data.forEach(item => {
      if(item.skill_clusters.some(item => skillClusters.includes(item.skill_group_id)) && item.personal_id !== element.personal_id){
        item.isChecked = false;
      }
    });
  }

  convertToCompetencyGroupModel(data: any[]){
    var uniquePersonalId: string[] = [];
    var competencyLeads: CompetencyGroupModel[] = [];
    data.forEach(item => {
      if(!uniquePersonalId.includes(item.personal_id)) {
        uniquePersonalId.push(item.personal_id);
        competencyLeads.push({
          display_name: item.display_name,
          personal_id: item.personal_id,
          skill_clusters: data.filter(pFilter => pFilter.personal_id === item.personal_id)
            .map(pMap => ({skill_cluster: pMap.skill_cluster, skill_group_id: pMap.skill_group_id})),
          isChecked: false
        })
      };
    });
    return competencyLeads;
  }
  getSkillClusterName(skillClusters: SkillClusterModel[]){
    return skillClusters.map(item => item.skill_cluster).join(', ');
  }
}
