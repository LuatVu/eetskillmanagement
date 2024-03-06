import { Injectable } from '@angular/core';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { BehaviorSubject, Observable } from "rxjs";
import { PersonalInfomationService } from './personal-infomation.service';
@Injectable({
  providedIn: 'root',
})
export class PersonalInfoService {
  private subject: BehaviorSubject<any> = new BehaviorSubject<any>('');
  private dataPersonalInfo: BehaviorSubject<any> = new BehaviorSubject<any>({});
  public messageResponseEdit$ = this.subject.asObservable();
  public messageResponseData$ = this.dataPersonalInfo.asObservable();
  public personalInfo: any = {};
  public projects: any[] = [];
  public courses: any[] = [];
  constructor(
    private personalInfomationService: PersonalInfomationService,
    private loader: LoadingService,
  ) { }

  public sendMessageEdit(value: any) {
    this.subject.next(value);
  }

  public sendMessageData(value: any) {
    this.dataPersonalInfo.next(value);
  }


  public savePersonalInfo() {
    this.personalInfo.projects = this.projects;
    this.personalInfo.courses = this.courses;
    const comLoader = this.loader.showProgressBar();
    this.personalInfomationService.savePersonalInfo(this.personalInfo).subscribe((response => {
      this.projects = [];
      this.courses = [];
      this.subject.next('saveSuccess');
      this.loader.hideProgressBar(comLoader);
    }));

  }
}
