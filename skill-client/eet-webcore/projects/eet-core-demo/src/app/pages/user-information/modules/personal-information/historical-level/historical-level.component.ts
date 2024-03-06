import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { ReplaySubject, delay, takeUntil } from 'rxjs';
import { DEFAULT_PAGE_SIZE } from '../constants/constants';
import { LevelHistoryModel } from '../personal-infomation.model';
import { PersonalInfomationService } from '../personal-infomation.service';
import { HistoricalLevel } from './models/historical-level.model';
import { Helpers } from 'projects/eet-core-demo/src/app/shared/utils/helper';


@Component({
  selector: 'eet-historical-level',
  templateUrl: './historical-level.component.html',
  styleUrls: ['./historical-level.component.scss'],
})
export class HistoricalLevelComponent implements OnInit {
  @ViewChild(MatSort)
  sort: MatSort = new MatSort();
  
  @Input() paginator:MatPaginator


  public readonly paginationOptions = CONFIG.PAGINATION_OPTIONS;
  public dataSource = new MatTableDataSource<LevelHistoryModel>();
  public destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  public searchedWord: string;

  public displayedColumns: string[] = [
    'skillCluster',
    'skillName',
    'date',
    'levelChange',
    'expChange',
    'note',
  ];
  public columnDef: {[dynamic: string]: string} = {
    skillCluster: 'Skill Cluster',
    skillName: 'Skill Name',
    date: 'Date',
    levelChange: 'Level Change',
    expChange: 'Exp Change',
    note: 'Note'
  }
  DEFAULT_PAGE: number = 0;
  DEFAULT_SIZE: number = 10;


  constructor(
    private loadService: LoadingService,
    private personalInfomationService: PersonalInfomationService
  ) {}

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource();
    this.getPersonalHistorycalLevel();
  }

  applyFilter() {
    this.dataSource.filter = this.searchedWord;
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.paginator.pageSize = DEFAULT_PAGE_SIZE;
    this.applyFilter()
  }

  convertDataForDatasource(historicalLevels: HistoricalLevel[]){
    return historicalLevels.map(item =>  ({
      id: item.id,
      skillCluster: item.skill_cluster,
      skillName: item.skill_name,
      date: Helpers.parseDateTimeToString(new Date(item.date)) ,
      levelChange: `${item.old_level} -> ${item.new_level}`,
      expChange: `${item.old_exp} -> ${item.new_exp}`,
      note: item.note,
    }) as LevelHistoryModel) as Array<LevelHistoryModel>;
  }

  getPersonalHistorycalLevel(){
    this.personalInfomationService.getSharedData()
      .pipe(takeUntil(this.destroyed$), delay(0))  
      .subscribe(response =>{
        this.dataSource = new MatTableDataSource(this.convertDataForDatasource(response.historicalLevel));
        this.dataSource.paginator = this.paginator;
      })
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }
}
