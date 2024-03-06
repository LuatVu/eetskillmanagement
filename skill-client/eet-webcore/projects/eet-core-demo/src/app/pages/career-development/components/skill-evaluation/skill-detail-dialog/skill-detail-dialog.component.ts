import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { LevelModel } from '../model/skill-evaluation.model';

export interface SkillInformation {
  skillTitle: string;
  levelList: LevelModel[];
}

@Component({
  selector: 'eet-skill-detail-dialog',
  templateUrl: './skill-detail-dialog.component.html',
  styleUrls: ['./skill-detail-dialog.component.scss'],
})
export class SkillDetailDialogComponent implements OnInit {
  public skillInfo!: SkillInformation;

  public levelTitle: string;
  public dataSource: MatTableDataSource<LevelModel> = new MatTableDataSource<LevelModel>([]);
  public displayedColumns: string[] = ["level", "description"]

  constructor(@Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit(): void {
    this.levelTitle = (this.data['data'] as SkillInformation).skillTitle;
    this.dataSource.data = (this.data['data'] as SkillInformation).levelList;
  }

}
