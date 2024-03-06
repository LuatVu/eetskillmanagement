import { orderBy } from "./projects.model";

export const OrderBy: orderBy[] = [
    {
        apiValue: 'startDate',
        viewValue: 'Date (Earliest)'
    },
    {
        apiValue: 'projectName',
        viewValue: 'Project Name [A-Z]'
    },
    {
        apiValue: 'name,desc',
        viewValue: 'Project Name [Z-A]'
    },
    {
        apiValue: 'startDateReverse',
        viewValue: 'Date (Latest)'
    }
]
