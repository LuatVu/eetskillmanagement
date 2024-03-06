export const DATA_CONFIG = {
  DISPLAY_COLUMNS: ['displayName', 'personalCode', 'team', 'level', 'skillGroups', 'skills', 'experience', 'action'],
  ARRAY_FILTER: [
    { key: 'teamList', name: 'Team', originalData: [], selectedData: [], isMultiple: true },
    { key: 'levelList', name: 'Level', originalData: [], selectedData: [], isMultiple: true },
    { key: 'skillGroupList', name: 'Skill Cluster', originalData: [], selectedData: [], isMultiple: true },
    { key: 'skillList', name: 'Skill', originalData: [], selectedData: [], isMultiple: true },
    { key: 'expList', name: 'Year Exp', originalData: [], selectedData: '', isMultiple: false }
  ],
  EXP_LIST: ['< 5', '5 - 10', '> 10'],
  TECHNICAL_SKILL_TYPE: "Technical",
  BEHAVIORAL_SKILL_TYPE: "Behavioral"
}