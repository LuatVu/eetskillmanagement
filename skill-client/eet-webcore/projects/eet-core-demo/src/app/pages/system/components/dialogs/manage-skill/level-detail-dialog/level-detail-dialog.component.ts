import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ManageSkillComponent } from '../../../pages/manage-skill/manage-skill.component';

@Component({
  selector: 'eet-level-detail-dialog',
  templateUrl: './level-detail-dialog.component.html',
  styleUrls: ['./level-detail-dialog.component.scss']
})
export class LevelDetailDialogComponent implements OnInit {
  public levelContent: string;
  constructor(@Inject(MAT_DIALOG_DATA) public levelData: any) {
    this.levelContent = levelData.data.content
  }

  ngOnInit(): void {
  }

}
