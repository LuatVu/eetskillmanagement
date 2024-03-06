import { Competency, CompetencyDetail } from "./competency-lead.model";

export const CompetencyList: Competency[] = [
  {
    id: '0',
    name: "Java"
  },
  {
    id: '1',
    name: ".NET"
  },
  {
    id: '2',
    name: "DevOps"
  },
  {
    id: '3',
    name: "Frontend"
  }
]


export const CompetencyDetailData: CompetencyDetail[] = [
  {
  id: '0',
  displayName: "Java",
  competencyLead: [
    {
      id: '0',
      displayName: "Vo Ly Luan (MS-EET11)",
      skills: [
        {
          id: '0',
          displayName: 'Lorem Ipsum',
          isLeader: true
        },
        {
          id: '1',
          displayName: 'ASP.NET',
          isLeader: false
        }
        
      ]
    }
  ],
  description: "Java?"
  }
]
