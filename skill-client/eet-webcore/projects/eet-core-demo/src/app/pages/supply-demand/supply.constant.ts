export const DATA_CONFIG = {
  DISPLAY_COLUMNS: ['subId','projectName', 'skillClusterName', 'skill', 'level', 'location', 'allowExternal','createdByName', 'createdDate', 
                    'assignUserName', 'expectedDate', 'forecastDate', 'filledDate','supplyType', 'candidateName', 'status', 'action', 'note'],
  ARRAY_FILTER: [
    { key: 'projectName', name: 'Project Name', originalData: [], selectedData: [], isMultiple: true },
    { key: 'skillClusterName', name: 'Skill Cluster', originalData: [], selectedData: [], isMultiple: true },
    { key: 'createdByName', name: 'Created By', originalData: [], selectedData: [], isMultiple: true },
    { key: 'assignUserName', name: 'PIC', originalData: [], selectedData: [], isMultiple: true },
    { key: 'status', name: 'Status', originalData: [], selectedData: '', isMultiple: true }
  ]
}