export interface OrderBy {
  value: string;
  viewValue: string;
}
export interface SelectedBy {
  value: any;
  viewValue: string;
}

export interface CommonIdentifier {
  id: string,
  name: string
}
export interface CourseDataSource {
  id: string,
  name: string;
  course_id: string;
  course_type: string;
  trainer: string;
  start: string;
  duration: number;
  type: string;
  target_audience: string;
  status: string;
  description: string
}


export interface CourseInformation {
  id: string;
  name: string;
  trainer: string;
  course_type: string;
  duration: number;
  date: Date;
  status: string;
  desc?: string;
}

export interface CourseDetail {
  id: string,
  name: string,
  categoryName: string,
  trainer: string,
  duration: number,
  date: Date,
  status: string,
  description: string,
  course_id: string,
  course_type: string,
  target_audience: string
}

export interface CourseMembers {
  id: string,
  personal_id: string,
  display_name: string,
  start_date: Date,
  end_date: Date,
  isNotFromAPI?: boolean
}

export const LEARNING_STATUS = {
  ON_GOING: 'ON-GOING',
  NEW: 'NEW',
  CLOSED: 'CLOSED'
}

export const LEARNING_PERMISSIONS = {
  PERMISSIONS: {
    ADD: "ADD_NEW_LEARNING_COURSE",
    EDIT: "EDIT_LEARNING_COURSE",
    IMPORT_FROM_EXCEL: "IMPORT_LEARNING_COURSES",
    VIEW_DETAIL: "VIEW_ALL_LEARNING_COURSES",
    DELETE: "DELETE_LEARNING_COURSE",
    ASSIGN: "ASSIGN_LEARNING_COURSE"
  }
}