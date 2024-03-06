export interface Team {
    id: string,
    name: string
    group:string,
    line_manager: Manager
}

export interface Manager {
    id:string,
    displayName:string
}