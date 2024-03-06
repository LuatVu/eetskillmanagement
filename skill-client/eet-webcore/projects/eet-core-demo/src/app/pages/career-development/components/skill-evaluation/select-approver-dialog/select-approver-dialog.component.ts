import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CompetencyLeaderModel } from '../model/skill-evaluation.model';
import { SkillEvaluationService } from '../skill-evaluation.service';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';

@Component({
  selector: 'eet-select-approver-dialog',
  templateUrl: './select-approver-dialog.component.html',
  styleUrls: ['./select-approver-dialog.component.scss'],
})
export class SelectApproverDialogComponent implements OnInit {
  public approverList: CompetencyLeaderModel[] = []
  public personalId: string = "";
  public chosenCompetencyLeader: CompetencyLeaderModel = {id: "", name: ""};
  constructor(
    private dialogRef: MatDialogRef<SelectApproverDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private skillEvaluationService: SkillEvaluationService,
    private loader: LoadingService,
  ) {}

  ngOnInit(): void {
    this.personalId = this.data?.['data'].personalId;
    this.setDefaultLineManager();
  }

  setDefaultLineManager() {
    const comLoader = this.loader.showProgressBar();
    this.skillEvaluationService.getLineManager(this.personalId).subscribe((rs) => {
      this.chosenCompetencyLeader = {
        id: rs?.data?.id,
        name: rs?.data?.name
      }
      this.approverList.push(this.chosenCompetencyLeader);
      this.loader.hideProgressBar(comLoader); 
    })
  }

  onConfirm() {
    this.dialogRef.close({
      result: true,
      id: this.chosenCompetencyLeader.id
    })
  }
}
