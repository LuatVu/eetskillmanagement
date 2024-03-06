import { OrderBy } from "./my-learning.model";



export const TYPE_LIST: OrderBy[] = [
  { value: 'self', viewValue: 'Self Study' },
  { value: 'rnd', viewValue: 'R&D' },
  { value: 'online', viewValue: 'Online' },
]

export const CATEGORY_LIST: OrderBy[] = [
  { value: 'tech', viewValue: 'Technical Skills' },
  { value: 'soft', viewValue: 'Soft Skills' },
  { value: 'misc', viewValue: 'Miscellaneous' },
]
export const CATEGORY_ORDER: OrderBy[]= [
  {
    value: 'name',
    viewValue: 'Course Name [A-Z]',
  },
  { value: 'nameDescend', viewValue: 'Course Name [Z-A]' },
  {
    value: 'duration',
    viewValue: 'Duration',
  },
  {
    value: 'course_type',
    viewValue: 'Course Type',
  },
  {
    value: 'target_audience',
    viewValue: 'Target Audience',
  },
];

// API_CALLS
export const DEFAULT_ELASTICSEARCH_PARAMETERS = {
  size: 2,
  from: 0
}