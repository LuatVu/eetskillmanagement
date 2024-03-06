export interface ProjectForVModel {
    name: string;
    project_id: string;
    gb_unit:string
    customer_gb:string
}

export interface PhaseModel {
    name: string;
    listProjects: ProjectForVModel[];
}

export class ChildVModel {
    public id: string;
    public x: number;
    public y: number;
    public class: string;
    public width: number;
    public height: number;
    public label: string;
    public project_id: string;
    public bgColor:string;
    constructor(id: string, x: number, y: number, className: string, width: number, height: number, label: string, project_id: string,bgColor:string) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.class = className;
        this.width = width;
        this.height = height;
        this.label = label;
        this.project_id = project_id;
        this.bgColor=bgColor
    }
}

export interface PointLike {
    x: number;
    y: number;
}
