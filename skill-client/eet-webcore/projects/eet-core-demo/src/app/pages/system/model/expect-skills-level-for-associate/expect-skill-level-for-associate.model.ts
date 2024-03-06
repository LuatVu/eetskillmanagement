export interface SkillGroupModel {
    id: string,
    name : string
}

export interface FilterModel {
    key: string;
    name: string;
    originalData: string[];
    selectedData: string[] | string;
    isMultiple: boolean;
    function?: any
}
export interface LevelModel {
    id: string;
    name: string;
}
export interface ExpectedSkillUpdateModel {
    idSkill: string;
    levelExpecteds: ExpectedLevelUpdateModel[];
}
export interface ExpectedLevelUpdateModel{
    idLevel : string;
    value: number;
}
export interface ExpectedSkillLevelModel {
    idSkill: string;
    nameSkill: string;
    levelExpecteds: ExpectedLevelModel[];
    [levelName: string]: number | string | ExpectedLevelModel[];
}
export interface ExpectedLevelModel{
    idLevel : string;
    nameLevel: string;
    value: number;
    originalValue: number;
}