export interface ProjectRole {
    id: string;
    displayName: string;
}

export interface ProjectCommonTask {
    id: string;
    name: string;
    project_role_id: string;
}

export interface DataSender {
    id: string;
    project_role_id?: string;
}

export const ERROR_USED = 'PROJECT_ROLE_IS_IN_USE'
export const ERROR_DUPLICATE_ROLE = 'PROJECT_NAME_ALREADY_EXIST'
export const ERROR_DUPLICATE_TASK = 'COMMON_TASK_ALREADY_EXIST'

