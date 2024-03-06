export class RoleModel {
  public id: string;
  public name: string;
}

export class CommonTaskModel {
  public id: string;
  public name: string;
  public project_role_id: string;

}

export class ProjectPhaseModel {
  public id: string;
  public name: string;
  public project: any[];
}

export class GBModel {

}
