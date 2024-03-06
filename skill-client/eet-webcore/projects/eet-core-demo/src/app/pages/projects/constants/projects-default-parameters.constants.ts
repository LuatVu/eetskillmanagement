export const GB_UNIT = 'GB Unit';
export const CUSTOMER_GB = 'Customer GB';
export const PROJECT_TYPE = 'Project Type';
export const PROJECT_STATUS = 'Project Status';
export const PROJECT_SCOPE='Project Scope'

export const DEFAULT_PAGE = 0;
export const DEFAULT_ITEMS_PER_PAGE = 5

export const DEFAULT_ELASTICSEARCH_PARAMETERS = {
  size: 2,
  from: 0
}
export const PROJECT_DATA = {
  ARRAY_FILTER: [
    {
      key: 'projectType', isCheckboxFilter: true, name: 'Project Type', originalData: [
        { name: 'Bosch', id: 'Bosch-filter', checked: false },
        { name: 'Non-Bosch', id: 'Non-Bosch-filter', checked: false },
      ], selectedData: []
    },
    {
      key: 'status', isCheckboxFilter: true, name: 'Project Status', originalData: [
        { name: 'On-going', checked: false, id: 'On-Going-filter' },
        { name: 'Closed', checked: false, id: 'Closed-filter' },        
      ], selectedData: []
    },

    { isCheckboxFilter: false, originalData: [], selectedData: [], key: 'startDate', name: 'Year' },
    { isCheckboxFilter: false, originalData: [], selectedData: [], key: 'customerGB', name: 'Customer GB' },
    { isCheckboxFilter: false, originalData: [], selectedData: [], key: 'team', name: 'Team' },
  ],
  TOP_PROJECT_ARRAY_FILTER: [
    {
      key: 'topProject', isCheckboxFilter: true, name: 'Top Project', originalData: [
        { name: 'Top Project', checked: false, id: 'Top-Project-filter' }
      ], selectedData: []
    }
  ]
}

export const PROJECT_CONFIG = {
  PROJECT_TAB_LIST: [
    { name: 'projects.tab_label.customer_gb', routeName: 'customer-gb' },
    { name: 'projects.tab_label.project_list', routeName: 'project-list' },
    { name: 'projects.tab_label.v_model', routeName: 'v-model' },
  ]
}

export const PROJECT_PORTFOLIO = {
  HIGHLIGHT:'project_highlight',
  PROBLEM_STATEMENT:"project_problem",
  SOLUTION:"project_solution",
  BENEFITS:"project_benefit"
}
