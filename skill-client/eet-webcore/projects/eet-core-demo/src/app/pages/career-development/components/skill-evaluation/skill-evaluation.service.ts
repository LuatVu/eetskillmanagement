import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { of } from 'rxjs';
import { EvaluateSkillDTOModel, RequestEvaluationDataModel, Skill } from './model/skill-evaluation.model';
import { CoreUrl } from 'nestjs/core-backend/src/constants/apiLocation.constant';
import { TOKEN_KEY } from '@core/src/lib/shared/util/system.constant';
@Injectable({
  providedIn: 'root',
})
export class SkillEvaluationService {
  constructor(public http: HttpClient) { }

  getPersonalEvaluate(userId: string) {
    const requester: boolean = true;
    return this.http.get<any>(`/${CoreUrl.API_PATH}/associate/${userId}/skill`)
  }
  getRequestEvaluation(page: number, size: number) {
    return this.http.get<any>(`${CoreUrl.API_PATH}/evaluation`, {
      params: {
        requester: true,
        page: page,
        size: size
      }
    })
  }

  getDetailedRequestEvaluation(skill_id: string) {
    return this.http.get<any>(`${CoreUrl.API_PATH}/evaluation/${skill_id}`)
  }

  getCompetencyLeader() {
    return this.http.get<any>(`${CoreUrl.API_PATH}/manager`)
  }
  postRequestEvaluation(data: EvaluateSkillDTOModel | RequestEvaluationDataModel) {
    return this.http.post<any>(`${CoreUrl.API_PATH}/evaluation`, data)
  }
  getLineManager(personalId: string) {
    return this.http.get<any>(`${CoreUrl.API_PATH}/line-manager/${personalId}`)
  }
  getPendingRequest(personalId: string) {
    return this.http.get<any>(`${CoreUrl.API_PATH}/evaluation/request-pending/${personalId}`)
  }
}
