export class Helpers {
  public static parseDateTimeToString(date: Date): string {
    const year = date.getFullYear()
    const month = (date.getMonth() + 1).toString().padStart(2,'0')
    const day = date.getDate().toString().padStart(2,'0')
    return `${year}-${month}-${day}`;
  }

  public static cloneDeep(originalData: any): any {
    return JSON.parse(JSON.stringify(originalData));
  }

  public static compareObject(obj1: any, obj2: any): boolean {
    return JSON.stringify(obj1) == JSON.stringify(obj2);
  }
  public static uuidv4(): string {
    return "10000000-1000-4000-8000-100000000000".replace(/[018]/g, (c: any) => {
      const randomValue = crypto.getRandomValues(new Uint8Array(1))[0];
      const shifted = (c ^ randomValue & 15) >> (parseInt(c, 10) / 4);
      return shifted.toString(16);
    });
  }

  public static buildColor_CurrentLevel(current_level: any, expected_level: any){
    if(expected_level == undefined || expected_level.trim() == ""){
      return 'rgba(0, 0, 0, 0.87);';
    }
    if(current_level < expected_level){
      return '#f24822';
    }
    if(current_level == expected_level){
      return '#198f51';
    }
    if(current_level > expected_level){
      return '#0088d4';
    }
    return 'rgba(0, 0, 0, 0.87);';
  }

}
