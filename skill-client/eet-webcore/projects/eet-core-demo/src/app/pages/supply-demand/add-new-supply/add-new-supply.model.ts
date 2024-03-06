import { Project } from "../supply.model";

export interface AddDemandSharedModel {
  isStoredState: boolean;
  projectList: Array<Project>;
  skillClusterList: Array<any>;
  levelList: Array<any>;
  locationList: Array<any>;
  assigneeList: Array<any>;
}
