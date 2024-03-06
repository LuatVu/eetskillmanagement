export interface filterOption {
  name: string;
  value: string;
  isdefault: boolean;
}

export interface EmpFilter {
  name: string;
  options: string[];
  defaultValue: string;
  paramFilterName:string;
}
