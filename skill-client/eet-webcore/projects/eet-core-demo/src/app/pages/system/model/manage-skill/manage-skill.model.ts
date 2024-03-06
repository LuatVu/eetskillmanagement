export class SkillModel {
  constructor() {
  }
  skillType: string;
  skillId: string;
  skill_name: string;
  skill_group: string;
  level_0: string;
  level_1: string;
  level_2: string;
  level_3: string;
  level_4: string;
  description: string;
  required?: boolean;
  mandatory?: boolean;
  filterType?: string;
}

export interface SkillLevelModel {
  id: string;
  name: string;
  description: string;
  key: string;
}

export interface SkillDialogDataModel {
  skillDetail: SkillModel;
}


export interface SkillDescriptionModel {
  id: string;
  name: string;
  description: string;
}

export interface CompetencyModel {
  id: string;
  name: string;
}

export const SKM_SKILL_ALREADY_EXISTS = "SKM_SKILL_ALREADY_EXISTS"
export const ADD_SKILL_SUCCESS = "ADD_SKILL_SUCCESS"
export const UPDATE_SKILL_SUCCESS = "UPDATE_SKILL_SUCCESS"
export const ERROR_WHEN_GET_COMPETENCY_LEAD = "ERROR WHEN GET COMPETENCY LEAD"