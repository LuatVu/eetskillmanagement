import { SupplyModel } from "../supply.model";

export interface HistoryModel {
  updatedByName: string;
  updatedDate: Date;
  oldStatus: string;
  newStatus: string;
  candidateName: string;
  assignee: string;
  supplyType: string;
  note: string;
}

