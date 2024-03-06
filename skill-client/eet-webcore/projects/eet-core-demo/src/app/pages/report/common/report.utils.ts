export class ReportUtils {
    static buildFilterString(teams: string[], projectGb: string[]): string {
        let filterString: string = '';
        if (teams?.length !== 0) {
            filterString += 'team=' + teams.join(',');
        }
        if (projectGb?.length !== 0) {
            if (filterString === '') {
                filterString += 'gb_unit=' + projectGb.join(',');
            } else {
                filterString += '&gb_unit=' + projectGb.join(',');
            }
        }

        return filterString;
    }

    static generateColorsArray(initColors: string[], length: number): string[] {
        let resColors: string[] = initColors.map((r: string) => r);
        if (resColors.length != length) {
            while (resColors.length > length) {
                resColors.pop();
            }

            while (resColors.length < length) {
                let newColor = ReportUtils.generateRandomColor();
                while (resColors.includes(newColor)) {
                    newColor = ReportUtils.generateRandomColor();
                }
                resColors.push(newColor);
            }
        }

        return resColors;
    }

    static generateRandomColor(): string {
        const letters = "0123456789ABCDEF";
        let color: string = "#";
        for (let i = 0; i < 6; i++) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
    }
}