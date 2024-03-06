export interface BaseResponseModel {
  message: string;
  code: 'SUCCESS' | 'ERROR' | string;
  timestamp: Date;
  data: any;
}

export class BasePaginationModel {
  page: number = 0;
  size: number = 0;
  sort: string[] = [];
}
