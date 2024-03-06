export interface HistoricalLevel {
  id: string;
  skill_cluster: string;
  skill_name: string;
  date: string | Date;
  old_level: string | number;
  new_level: string | number;
  old_exp: string | number;
  new_exp: string | number;
  note: string;
}
