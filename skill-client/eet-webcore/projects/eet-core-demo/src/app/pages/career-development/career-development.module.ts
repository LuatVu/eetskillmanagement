import { SharedModule } from '@core/src/lib/shared/shared.module';
import { SharedCommonModule } from '../../shared/shared.module';
import { SupplyRoutingModule } from '../supply-demand/supply-routing.module';
import { CareerDevelopmentComponent } from './career-development.component';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CareerDevelopmentRoutingModule } from './career-development-routing.module';
import { CareerRoadmapComponent } from './components/career-roadmap/career-roadmap.component';
import { TechnicalCompetenciesComponent } from './components/technical-competencies/technical-competencies.component';
import { JdComponent } from './components/jd/jd.component';
import { AddCourseDialogComponent } from './components/learning/add-course/add-course.component';
import { CourseInfoDialogComponent } from './components/learning/course-info/course-info.component';
import { CourseRegisterComponent } from './components/learning/course-info/course-register/course-register.component';
import { DeletePromptComponent } from './components/learning/delete-prompt/delete-prompt.component';
import { LearningComponent } from './components/learning/learning.component';
import { MyLearningComponentPipe } from './components/learning/my-learning/my-learning-component.pipe';
import { MyLearningComponent } from './components/learning/my-learning/my-learning.component';
import { UploadCourseComponent } from './components/learning/my-learning/upload-course/upload-course.component';
import { RequestDetailDialogComponent } from './components/skill-evaluation/requests/request-detail-dialog/request-detail-dialog.component';
import { RequestsComponent } from './components/skill-evaluation/requests/requests.component';
import { SelectApproverDialogComponent } from './components/skill-evaluation/select-approver-dialog/select-approver-dialog.component';
import { SkillDetailDialogComponent } from './components/skill-evaluation/skill-detail-dialog/skill-detail-dialog.component';
import { SkillEvaluationComponent } from './components/skill-evaluation/skill-evaluation.component';
import { SkillTableComponent } from './components/skill-evaluation/skill-table/skill-table.component';
@NgModule({
    declarations: [
        CareerDevelopmentComponent,
        CareerRoadmapComponent,
        JdComponent,
        LearningComponent,
        MyLearningComponent,
        AddCourseDialogComponent,
        CourseInfoDialogComponent,
        CourseRegisterComponent,
        DeletePromptComponent,
        UploadCourseComponent,
        MyLearningComponentPipe,
        SkillEvaluationComponent,
        RequestsComponent,
        SkillTableComponent,
        SelectApproverDialogComponent,
        RequestDetailDialogComponent,
        SkillDetailDialogComponent,
        TechnicalCompetenciesComponent,
    ],
    imports: [
        CommonModule,
        SharedModule,
        SharedCommonModule,
        CareerDevelopmentRoutingModule
    ],
    exports: [
        CareerDevelopmentComponent
    ]
})
export class CareerDevelopmentModule {}
