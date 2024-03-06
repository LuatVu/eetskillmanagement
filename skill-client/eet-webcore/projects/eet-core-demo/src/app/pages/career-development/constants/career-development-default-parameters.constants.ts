import { CONFIG } from "../../../shared/constants/config.constants";

export const CAREER_DEVELOPMENT_CONFIG = {
  TAB_LIST: [
    { name: 'career_development.tab_label.career_road_map', routeName: 'career-road-map' },
    // { name: 'career_development.tab_label.jd', routeName: 'jd' },
    // { name: 'career_development.tab_label.learning_courses', routeName: 'learn-courses',permission:CONFIG.PERMISSIONS.VIEW_DEPARTMENT_LEARNING }, // permission only apply for view
    { name: 'career_development.tab_label.self_skill_evaluation', routeName: 'self-skill-evaluation',permission:CONFIG.PERMISSIONS.VIEW_SKILL_EVALUATE },// permission only apply for view
    { name: 'career_development.tab_label.my_request', routeName: 'my-request',permission:CONFIG.PERMISSIONS.VIEW_SKILL_EVALUATE }// permission only apply for view
  ]
}
