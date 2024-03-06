export const TAB_CODE_LIST = {
  SKILL: 'SKILL',
  PROJECT: 'PROJECT',
  LEARNING: 'LEARNING',
  HISTORY: 'HISTORY'
} as const;

export const PERSONAL_INFO_CONFIG = {
  PERSONAL_INFO_TAB_LIST: [
    {
      name: 'personal_information.project_tab',
      personal_name:'personal_information.personal_project_tab',
      code: TAB_CODE_LIST.PROJECT
    },
    {
      name: 'personal_information.skill_tab',
      personal_name:'personal_information.personal_skill_tab',
      code: TAB_CODE_LIST.SKILL
    },
    // {
    //   name: 'personal_information.learning_tab',
    //   code: TAB_CODE_LIST.LEARNING
    // },
    {
      name: 'personal_information.history_change_tab',
      personal_name:'personal_information.history_change_tab',
      code: TAB_CODE_LIST.HISTORY
    }
  ],
  PERMISISON: {
    EDIT_ASSOCIATE_INFO_PERMISSION: 'EDIT_ASSOCIATE_INFO_PERMISSION',
    EDIT_PERSONAL_PERMISSION: 'EDIT_PERSONAL_PERMISSION'
  }
} as const;

