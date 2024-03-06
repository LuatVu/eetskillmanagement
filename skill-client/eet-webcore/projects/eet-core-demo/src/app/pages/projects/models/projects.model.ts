export interface projectBasicInfo {
    id: string
    projectName: string,
    projectId: string,
    technologyUsed: string,
    description: string,
    teamSize: string
}
export interface Projects {
    id: string,
    projectId: string,
    description: string,
    teamSize: string,
    projectName: string,
    technologyUsed: string,
    technologyUsedArray: string[],
    projectManager: string, 
    startDate: string,
    customerGB: string,
    endDate: string,
    status: string,
    team: string,
    projectType: string,
    topProject: boolean;
    scopeId:string,
    gbUnit:string
    
}

export interface orderBy {
    apiValue: string,
    viewValue: string
}

export interface managerDTO {
    id: string,
    name: string
}
export interface gb {
    id: number;
    val: string
  }
  interface members {
    name: string;
    role: string;
    task: string;
    additional_task: string
  }
  
  export interface FilterModel {
    key: string;
    name: string;
    isCheckboxFilter: boolean;
    originalData: FilterCheckBox[] | any;
    selectedData: FilterCheckBox[] | any;
  }
  
  export interface FilterCheckBox {
    name: string,
    checked: boolean;
    id: string;
  }
  export interface ProjectPhaseModel {
    id: string;
    name: string;
  }

  export interface FilterProjectModel {
    id?:string,
    name: string;
    order: number;
    isSelected: boolean;
    projectCount:number
  }

  export interface CustomerGbModel {
    id: string,
    numOfHC: number,
    numOfProject: number,
    projectSkillTagSimpleDocuments: SkillTagUsedInProject [],
    projects : CustomerGbProjectDto[],
    name: string,
    toolAccross: number,
    detail: any
  }
  export interface CustomerGbProjectDto{
    projectName: string,
    projectId: string,
    [dynamic: string]: string
  }
  export interface SkillTagUsedInProject {
    tag: string,
    projectUsed: number,
  }

  export interface managerModel {
    name: string
}

export interface ProjectType {
  name: string;
  id: string;
}

export interface ProjectTabModel {
  name: string;
  routeName: string;
}


export interface CustomerModel {
  id: string,
  name: string,
  head_counts: number,
  hightlight: string,
  v_model_count: number,
  corporation: string,
  gb_info: [{[dynamic: string]: number}]
}

export interface GBFilterModel {
  type: string,
  data: string
}

export interface VModelProjectModel {
  name: string,
  project_id: string
  [dynamic: string]: any
}

export interface ProjectScope {
  id:string,
  name:string
  colour:string,
  hover_colour:string
}

export interface ProjectPorfolio {
  projectName:string | null,
  highlight:string | null,
  skillTags:string[],
  teamSize:string | null,
  customerGB:string | null,
  projectScopeName:string | null,
  projectScopeId:string | null,

}

export interface CustomerUpdateEventModel{
  type: string,
  message: string | undefined
}