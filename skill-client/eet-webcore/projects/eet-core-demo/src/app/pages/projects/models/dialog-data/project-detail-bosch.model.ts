import { MembersInfo } from "./members-info/members-info.model";

export interface ProjectDetailBosch {
    name: string,
    start_date: string,
    end_date: string,
    pm_name: string,
    challenge: string,
    status: string,
    project_type: 'Bosch',
    project_type_id: 'bd377e0d-688e-4b91-b328-334fa911a7d7',
    objective: string,
    gb_unit: string,
    team_size: string,
    reference_link: string,
    department: string // -> Team, currently getting "department"
    description: string,
    members: MembersInfo[]
}
