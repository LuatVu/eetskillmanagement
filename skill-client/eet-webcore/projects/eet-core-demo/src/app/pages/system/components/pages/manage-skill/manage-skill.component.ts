import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { TranslateService } from '@ngx-translate/core';
import { ELASTIC_DOCUMENT } from 'projects/eet-core-demo/src/app/shared/constants/api.constants';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { DialogCommonService } from 'projects/eet-core-demo/src/app/shared/services/dialog-common.service';
import { ElasticService } from 'projects/eet-core-demo/src/app/shared/services/elastic.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { finalize } from 'rxjs';
import { SkillModel } from '../../../model/manage-skill/manage-skill.model';
import { LevelDetailDialogComponent } from '../../dialogs/manage-skill/level-detail-dialog/level-detail-dialog.component';
import { SkillDetailDialogComponent } from '../../dialogs/manage-skill/skill-detail-dialog/skill-detail-dialog.component';
import { ManageSkillService } from './manage-skill.service';
import { PaginDirectionUtil } from 'projects/eet-core-demo/src/app/shared/utils/paginDirectionUtil';
import i18const from '../../../../../../assets/i18n/i18n.json';

@Component({
  selector: 'eet-manage-skill',
  templateUrl: './manage-skill.component.html',
  styleUrls: ['./manage-skill.component.scss']
})
export class ManageSkillComponent implements OnInit,AfterViewInit {

  public typeList: any[] = [
    {'value': 'Front End', 'viewValue': 'Front End'},
    {'value': 'Testing', 'viewValue': 'Testing'},
    {'value': 'Data Analytics', 'viewValue': 'Data Analytics'},
    {'value': '.NET', 'viewValue': '.NET'},
  ]

  public isFilterTechnicalSkill: boolean;

  public displayedColumns: string[] = ['skill_group', 'skill_name' , 'level_0', 'level_1', 'level_2', 'level_3', 'level_4', 'action']
  // 'required','mandatory'
  private skillData: SkillModel[] = []
  public dataSource = new MatTableDataSource<SkillModel>();
  @ViewChild(MatPaginator)
  private paginator!: MatPaginator;
  @ViewChild(MatSort)
  sort: MatSort = new MatSort
  public pageSizeOption = CONFIG.PAGINATION_OPTIONS
  private searchVal: string = ""
  public isFilterTechnical: boolean = false;
  public isRequired: boolean = false;
  public isMandatory: boolean = false;
  public filterType: string;
  public _filterValue: string;
  constructor(
    public dialog: DialogCommonService,
    public translate: TranslateService,
    public deleteDialog: MatDialog,
    public manageSkillService: ManageSkillService,
    private elasticService: ElasticService,
    private notify: NotificationService,
    private loaderService: LoadingService) {
  }

  public technical: string = i18const.skill.type.technical;
  public behavorial: string = i18const.skill.type.behavioral;

  ngOnInit(): void {
    this.getSkillList()
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    PaginDirectionUtil.expandTopForDropDownPagination()
  }


  applyFilter(event?: any) {
    

    if (event) {
      if (event === this.technical) this.filterType = 'Technical';
      else if (event === this.behavorial) this.filterType = 'Behavioral';
      else if (event.data.includes(this.technical)) this.filterType = 'Technical';
      else this.filterType = 'Behavioral'
    }

    const filteredData = this.skillData.filter((el) => 
       el.skillType == this.filterType
    )

    if (this.dataSource.paginator) {
          this.dataSource.paginator.firstPage();
    }    
   
    this.dataSource = new MatTableDataSource<SkillModel>(filteredData);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this._filterValue = "";


  }


  getSkillList() {
    const loader = this.loaderService.showProgressBar();
    this.elasticService.getDocument(ELASTIC_DOCUMENT.SKILL, {
      size: 10000,
      query: "",
      from: 0
    }).pipe(finalize(() => {
      this.loaderService.hideProgressBar(loader)
    }))
      .subscribe(response => {

        const _tmpData = response?.arrayItems || []

        this.skillData =
          Object.keys(_tmpData).map((key: string) => {
            //get new object with skill_id as key
            const item = Object(_tmpData)[key];
            item.skill_name = item.skillName;
            item.skill_group = item.skillGroup;

            item.skillLevels.sort((a: any, b: any) => a.name.localeCompare(b.name))
            //map new key for element
            item.skillLevels.map((element: any, index: number) => item[`level_${index}`] = element.description)
            return {
              skill_id: key, ...item
            }
          })
        
        
          this.skillData.forEach(item => {
            const skillgroup = item.skill_group;
            this.typeList.push({value: skillgroup, viewValue: skillgroup});
          })
        
        this.dataSource = new MatTableDataSource<SkillModel>(this.skillData);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.applyFilter(this.filterType);
      });
  }

  viewLevelDetail(level_name: string, data: string) {
    const dialogRef = this.dialog.onOpenCommonDialog({
      component: LevelDetailDialogComponent,
      title: this.translate.instant(level_name),
      width: "60vw",
      height: 'auto',
      type: 'view',
      passingData: {
        content: data
      }
    });
  }

  onFilter(e: Event) {
    const filterValue = (e.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  addSkill() {
    const dialogRef = this.dialog.onOpenCommonDialog({
      component: SkillDetailDialogComponent,
      title: 'system.manage_skill.add_new_skill',
      width: "60vw",
      height: 'auto',
      type: 'edit',
      passingData: {

      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const loader = this.loaderService.showProgressBar();
        setTimeout(() => {
          this.loaderService.hideProgressBar(loader)
          this.getSkillList()
        },1000)
        this.notify.success(this.translate.instant("system.manage_skill.notification.add_skill_success"));
      }
    })
  }

  editSkill(skillDetail: SkillModel) {
    const loader = this.loaderService.showProgressBar();
    this.manageSkillService.getDetailSkillData(skillDetail.skillId)
      .pipe(
        finalize(() => this.loaderService.hideProgressBar(loader))
      )
      .subscribe((response: any) => {
        if (response && response.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          const dialogRef = this.dialog.onOpenCommonDialog({
            component: SkillDetailDialogComponent,
            title: 'system.manage_skill.edit_new_skill',
            height: 'auto',
            type: 'edit',
            passingData: { skillDetail: { ...response.data, skill_id: skillDetail.skillId } },
            width: "60vw"
          });
          dialogRef.afterClosed().subscribe(data => {
            if (data?.result) {
              const loader = this.loaderService.showProgressBar();
              setTimeout(() => {
                this.loaderService.hideProgressBar(loader);
                this.getSkillList();
              },1000)
              this.notify.success(this.translate.instant("system.manage_skill.notification.edit_skill_success"));
            }
          })
        }
      })
  }

  deleteSkill(element: any) {
    const dialogRef = this.dialog.onOpenConfirm({
      title: this.translate.instant('system.actions.delete.title'),
      content: this.translate.instant('system.actions.delete.content_title') + '\n' + this.translate.instant('system.actions.delete.content_assure'),
      btnConfirm: this.translate.instant('system.actions.delete.confirm'),
      btnCancel: 'system.actions.delete.cancel',
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const loader = this.loaderService.showProgressBar();
        this.manageSkillService.deleteSkillData(element.skillId)
          .pipe(finalize(() => this.loaderService.hideProgressBar(loader)))
          .subscribe((result) => {
            const loader = this.loaderService.showProgressBar();
            setTimeout(() => {
              this.loaderService.hideProgressBar(loader)
              this.getSkillList()
            },1000)
            this.notify.success(this.translate.instant('system.actions.delete.success'))
          });
      }
    })
  }

  updateSkillList(requestData: any, option: 'update' | 'add' | 'delete') {
    if (option === 'update') {
      this.skillData.filter((oldSkill) => {
        if (oldSkill.skillId === requestData?.id) {
          oldSkill.skill_name = requestData?.name;
          oldSkill.skill_group = requestData?.skill_group;
          oldSkill.level_0 = requestData?.skill_experience_levels[0].description;
          oldSkill.level_1 = requestData?.skill_experience_levels[1].description;
          oldSkill.level_2 = requestData?.skill_experience_levels[2].description;
          oldSkill.level_3 = requestData?.skill_experience_levels[3].description;
          oldSkill.level_4 = requestData?.skill_experience_levels[4].description;
          return;
        }
      });
    } else if (option === 'add') {
      let newSkill: SkillModel = new SkillModel();
      newSkill.skillId = requestData?.id;
      newSkill.skill_name = requestData?.name;
      newSkill.skill_group = requestData?.skill_group;
      newSkill.level_0 = requestData?.skill_experience_levels[0].description;
      newSkill.level_1 = requestData?.skill_experience_levels[1].description;
      newSkill.level_2 = requestData?.skill_experience_levels[2].description;
      newSkill.level_3 = requestData?.skill_experience_levels[3].description;
      newSkill.level_4 = requestData?.skill_experience_levels[4].description;
      this.skillData.push(newSkill);
    } else if (option === 'delete') {
      this.skillData = this.skillData.filter((item) => item.skillId !== requestData?.id);
    }

    this.dataSource = new MatTableDataSource<SkillModel>(this.skillData);
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

}
