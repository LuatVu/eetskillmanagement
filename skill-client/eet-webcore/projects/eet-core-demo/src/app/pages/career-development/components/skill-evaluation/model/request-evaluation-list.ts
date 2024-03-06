import { STATUS } from "./skill-evaluation.model";

export interface MyRequestEvaluationData {
    position: string;
    approver: string;
    status: STATUS
    created_date: string;
    updated_date: string;
    approved_date: string;
}
