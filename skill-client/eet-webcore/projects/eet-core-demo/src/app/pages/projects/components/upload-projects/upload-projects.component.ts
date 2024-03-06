import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { NotificationService } from 'projects/eet-core-demo/src/app/shared/services/notification.service';
import { CONFIG } from 'projects/eet-core-demo/src/app/shared/constants/config.constants';
import { LoadingService } from 'projects/eet-core-demo/src/app/shared/services/loading.service';
import { finalize, take } from 'rxjs/operators';
import { ProjectsService } from '../../services/projects.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'eet-upload-projects',
  templateUrl: './upload-projects.component.html',
  styleUrls: ['./upload-projects.component.scss'],
})
export class UploadProjectsComponent implements OnInit {
  public files!: File
  @ViewChild('fileUpload') fileUpload!: ElementRef
  public displayedColumns: string[] = [
    "name"
  ];
  constructor(
    private projectsService: ProjectsService,
    private loader: LoadingService,
    private notify: NotificationService,
    public translate: TranslateService,
    private dialogRef: MatDialogRef<UploadProjectsComponent>) { }

  public isUserUploaded: boolean = false;
  public isProjectExisted: boolean = false;
  public existedList: string[] = []
  public errorList: string[] = []
  ngOnInit(): void { }

  private getAssociateId(): string {
    const _localStorage = JSON.parse(localStorage.getItem('Authorization') || '{}');
    return _localStorage.id
  }

  private getAssociateNTID(): string {
    const _localStorage = JSON.parse(localStorage.getItem('Authorization') || '{}');
    return _localStorage.name
  }
  onChange(event: any) {
    if (event.target.files && event.target.files[0]) {
      this.files = event.target.files[0]
      this.isUserUploaded = true
    }
  }
  onImport() {
    const comLoader = this.loader.showProgressBar()
    this.projectsService.importProject(this.files,this.getAssociateNTID()).pipe(take(1),finalize(() => this.loader.hideProgressBar(comLoader))).subscribe((response: any) => {
      if (response.code === CONFIG.API_RESPONSE_STATUS.SUCCESS) {
          this.dialogRef.close(true)
      } 
      else if(response?.data?.existed_project_list?.length != 0){
        this.isProjectExisted = true;
        this.notify.error(this.translate.instant('projects.import.import_failed'))
        this.existedList = response.data.existed_project_list.map((item: { name: any; }) => item.name)
      } 
      else {
        this.errorList.push(response.message || response.code)
      }
    }
    )
  }
}
