export interface ReportForChartModel {
    title: string; // 'Associates by Team',
    titleColor: string; // '#D4AF37',
    backgroundColor: string; //'#D4AF37',
    chartLabels: string[]; //['EET11', 'EET12', 'EET22', 'EET31'],
    chartData: number[]; //[30, 1, 3, 5, 10],
    chartItemColor: string[]; //['#7ebdff', '#79c5c0', '#b2b9c0', '#deb300', '#deb300']
}

export interface ReportTabModel {
    name: string;
    routeName: string;
}