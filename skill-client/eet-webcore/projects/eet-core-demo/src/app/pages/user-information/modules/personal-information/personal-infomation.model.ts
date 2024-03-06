export interface PersonalInfomationModel {
  code: string;
  department_name: string;
  group: string;
  gender: string;
  id: string;
  level: string;
  name: string;
  skills: SkillModel[]
  supervisor_name: string;
  manager: string;
  title: string;
  top_skills: SkillModel[]
  total_experienced: string;
  ntid: string;
  team: string;
  team_id: string;
  total_exp: string | number;
  experienced_at_bosch: string;
  experienced_non_bosch: string;
  avatarcolor?: string;
  avatarbgcolor?: string;
  picture?: string;
  shortName?: string;
  projects?: ProjectModel[];
  courses?: CourseModel[];
  skill_cluster?: string[];
  brief_info?: string;
  personal_number?: string;
}

export interface SkillModel {
  competency: string;
  experience_number: number; 
  level: string | number;
  name: string;
  skill_group: string;
  skill_id: string;
  current_level: string;
  expected_level: string;
  skill_type:string
}

export interface ProjectModel {
  id: string;
  name: string;
  role: string;
  role_name: string;
  role_id: string;
  status: string;
  tasks: TaskModel[];
  challenge: string;
  objective: string;
  description: string;
  department: string;
  createdBy: string;
  team_size: string;
  project_type: string;
  start_date: string | Date;
  end_date: string | Date;
  additional_tasks: string;
  pm_name: string;
  gb_unit: string;
  reference_link: string;
  project_type_id: string;
  referencelink: string;
  project_id: string;
  technology_used: string;
  skill_tags:SkillTagModel[],
  member_start_date:string,
  member_end_date:string
}

export interface TaskModel {
  id: string,
  name: string,
  project_role_id: string
}

export interface SkillTagModel {
  id:string,
  name:string
}

export interface CourseModel {
  id: string;
  name: string,
  status: string,
  category: string,
  certificate: string,
  description: string,
  course_id: string,
  course_type: string,
  start_date: string | Date,
  end_date: string | Date
  duration: string | number;
}

export interface LevelHistoryModel {
  id: string
  skillCluster: string
  skillName: string
  date: string | Date
  levelChange: string | number
  expChange: string | number
  note: string
}