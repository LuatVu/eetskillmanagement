export interface RequestDetail {
  id: string;
  comment: string;
  experience: string | any;
  old_experience: number | any;
  current_experience: number | any;
  status: string;
  statusMessage: string;
  skill_id: string;
  skill: string;
  current_level: string | any;
  expected_level: string;
  old_level: string;
  skill_group: string;
  skillHighlight?: string;
  isDisabled: boolean;
  competency_lead: { personal_id: string, display_name: string }
}

export interface infoListModel {
  requester: string,
  approver: string,
  approver_id: string,
  manager_comment: string;
}

export interface CompetencyModel {
  display_name: string;
  personal_id: string;
  skill_cluster: string[];
  skill_names: string[];
  skill_ids: string[];
  isChecked: boolean;
}

export interface CompetencyGroupModel {
  display_name: string;
  personal_id: string;
  skill_clusters: SkillClusterModel[];
  isChecked: boolean;
}

export interface SkillClusterModel{
  skill_cluster: string;
  skill_group_id: string;
}

export interface RequestInf {
  id: string;
  requester: string;
  request_date: Date | string;
  mod_date: Date | string;
  status: string;
  isShowForwardBtn?: boolean;
  statusClass: string;
}

export interface SkillDetailLevelModel {
  level: string;
  description: string;
}

export interface ManageRequestTabModel{
  name: string;
  routeName: string;
}