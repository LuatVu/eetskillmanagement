export interface Competency {
    id: string,
    name: string,
    skill_type: string
}

export interface SkillsDTO {
    competency: string,
    name: string,
    skill_id: string
}
export interface CompetencyDetail {
    id: string,
    displayName: string,
    competencyLead: CompetencyLead[],
    description: string
}
export interface CompetencyLead {
    id: string,
    displayName: string,
    skills: SkillLead[]
}

export interface SkillLead {
    id: string,
    displayName: string,
    isLeader: boolean
}

export interface SkillTypeModel {
    id: string,
    name: string,
}

export interface SkillGroupModel {
    name: string,
    skill_type_id: string | undefined,
    skill_type: string
}