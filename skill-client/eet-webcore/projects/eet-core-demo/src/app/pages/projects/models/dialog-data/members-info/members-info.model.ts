export interface MembersInfo {
    id: string,
    name: string,
    role: string,
    common_task: CommonTask[],
    additional_task: string,
    isNotFromAPI?: boolean,
    uuid:string
}

export interface CommonTask {
    id: string,
    name: string,
    project_role_id: string
}