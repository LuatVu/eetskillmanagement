import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { PersonalInfomationService } from '../../../personal-infomation.service';
import { PersonalInfomationModel } from '../../../personal-infomation.model';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { finalize } from 'rxjs/operators';
import { MakeSkillHighlightService } from './make-skill-highlight.service';
import { Skill } from 'projects/eet-core-demo/src/app/pages/career-development/components/skill-evaluation/model/skill-evaluation.model';

@Component({
  selector: 'eet-dialog-make-skill-highlight',
  templateUrl: './dialog-make-skill-highlight.component.html',
  styleUrls: ['./dialog-make-skill-highlight.component.scss']
})
export class DialogMakeSkillHighlightComponent implements OnInit {
  public listSkill: Skill[] = [];
  private userId: string = "";
  public isDisabled: boolean = false; // if number of selected highlight skill >= 6, disable button
  public personalInfoDetail!: PersonalInfomationModel;
  constructor(
    public dialogRef: MatDialogRef<DialogMakeSkillHighlightComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private makeSkillHighlightService: MakeSkillHighlightService,
    private loader: LoadingService,
    private notifyService: NotificationService,
    private translateService: TranslateService,
    private personalInfomationService: PersonalInfomationService
  ) { }

  ngOnInit() {
    this.userId = this.data.data;
    this.getSkillsByPersonalId(this.data.data);
  }

  onSelectedChange(event: Event) {
    const result = this.listSkill.filter(skill => skill.selected == true);
    if (result && result.length > CONFIG.SKILL_HIGHLIGHT.MAX_SKILL_HIGHLIGHT) {
      this.isDisabled = true;
    } else {
      this.isDisabled = false;
    }
  }

  save() {
    const loader = this.loader.showProgressBar();
    let skillIds: string[] = [];
    this.listSkill.forEach((skill: Skill) => {
      if (skill.selected == true) {
        skillIds.push(skill.skill_id);
      }
    });
      this.makeSkillHighlightService.postSkillsHighlightByPersonalId(skillIds, this.userId)
      .pipe(finalize(() => this.loader.hideProgressBar(loader)))
      .subscribe((data: any) => {
        if (data && data.code == CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.getUserInfor();
          this.dialogRef.close(data);
        } else {
          this.notifyService.success(this.translateService.instant('personal_information.dialog.make_skill_highlight.add_failed'));
        } 
      })
  }

  getUserInfor() {
    const loader = this.loader.showProgressBar();
    let iduser = this.userId ? this.userId : JSON.parse(localStorage.getItem('Authorization') || '{}').id;
    this.personalInfomationService.getPersonInfo(iduser)
    .pipe(finalize(() => this.loader.hideProgressBar(loader)))
    .subscribe(data => {
      this.personalInfoDetail = data;
      this.personalInfomationService.setPersonalInfoDetail(this.personalInfoDetail);
      //show message here due to getting content slowly
      this.notifyService.success(this.translateService.instant('personal_information.dialog.make_skill_highlight.add_success'));
    })
  }

  getSkillsByPersonalId(id: string) {
    const loader = this.loader.showProgressBar();
    this.makeSkillHighlightService.getSkillsByPersonalId(id)
    .pipe(finalize(() => this.loader.hideProgressBar(loader)))
    .subscribe((data: any) => {
      if (data) {
        this.listSkill = data.data;
        const result = this.listSkill.filter(skill => skill.selected == true);
        if (result && result.length > CONFIG.SKILL_HIGHLIGHT.MAX_SKILL_HIGHLIGHT) {
          this.isDisabled = true;
        } else {
          this.isDisabled = false;
        }
      }
    })

  }

}
