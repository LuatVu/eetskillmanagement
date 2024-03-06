import { Pipe, PipeTransform } from '@angular/core';
import { CourseInformation } from './model/my-learning.model';

@Pipe({
  name: 'filter'
})
export class MyLearningComponentPipe implements PipeTransform {

  transform(items: CourseInformation[], filterdata: string): any[] {
    if (!items) return [];
    if (!filterdata) return items;
    filterdata = filterdata.toString().toLowerCase();
    return items.filter((it) => {
      return it.name.toLowerCase().startsWith(filterdata);
    });
  }

}
