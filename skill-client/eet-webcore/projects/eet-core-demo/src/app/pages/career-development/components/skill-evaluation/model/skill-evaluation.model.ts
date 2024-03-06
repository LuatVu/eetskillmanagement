export type STATUS = 'APPROVAL_PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED'

export interface RequestEvaluationDataModel {
  id?: string;
  requester: string;
  approver: string;
  approver_id?: string;
  status: STATUS,
  statusMsg?: string,
  comment: string,
  request_evaluation_details: Skill[],
  created_date: string,
  updated_date?: string,
  isEnabled?: boolean
}


export interface RequestEvaluationDataComparison {
  skill_id: string,
  isChanged: boolean
}

export interface Skill {
  skill_id: string;
  skill: string;
  old_level: string;
  evaluate_level: string;
  evaluated_level: number;
  evaluated_exp?: number;
  skill_group: string;
  current_level: string;
  expected_level: string;
  current_experience: number;
  old_experience: number;
  isUpdated?: boolean;
  comment: string;
  skill_description: LevelModel[],
  isDisabled?: boolean,
  status?: string,
  experience?: number;
  selected?: boolean;
  skillName?: string; // only for API get skill higlight
  level?: number;
  level_before_update?: string;
  skill_type: string;
}


export interface SkillModel {
  current_experience: number;
  previous_experience: number;
  evaluated_exp: number;

  current_level: string;
  previous_level: string;
  expected_level: string;
  evaluated_level: number;

  comment: string;
  previous_comment?: string;
  evaluated_comment?: string;

  skill: string;
  skill_description: SkillDescriptionModel[];
  skill_group: string;
  skill_id: string;
}

export interface SkillDescriptionModel {
  level: string;
  description: string;
}

export interface EvaluateSkillDTOModel {
  id?: string;
  requester: string;
  approver: string;
  approver_id?: string;
  status: STATUS,
  statusMsg?: string,
  comment: string,
  request_evaluation_details: SkillModel[],
  created_date: string,
  updated_date?: string,
  isEnabled?: boolean
}


export interface LevelModel {
  level: string;
  description: string;
}

export interface CompetencyLeaderModel {
  id: string;
  name: string
}
