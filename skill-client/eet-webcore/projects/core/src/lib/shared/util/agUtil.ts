import {
  Column,
  DateFilterModel,
  ICellRendererParams,
  NumberFilterModel,
  TextFilterModel,
} from '@ag-grid-community/core';
import { DateTime } from 'luxon';
import { HighlightTextPipe } from '../pipes/highlight-text.pipe';

const highlightPipe: HighlightTextPipe = new HighlightTextPipe();
export class AgUtil {
  public static addOverlayCustomComponent() {
    const overlay = document.querySelector('.cdk-overlay-container');
    if (overlay) {
      overlay.classList.add('ag-custom-component-popup');
    } else {
      console.warn('Not found overlay: cdk-overlay-container');
    }
  }

  public static getFilterTooltip(colFilModel: any) {
    if (colFilModel) {
      switch (colFilModel.filterType) {
        case 'date':
          return this._resolveDateFilterTooltip(colFilModel);
        case 'number':
          return this._resolveNumberFilterTooltip(colFilModel);
        default:
          if (colFilModel.filter) {
            return this._resolveTextFilterTooltip(colFilModel);
          }
      }
    }
    return 'Click to filter';
  }

  public static defaultCellRenderer(
    params: ICellRendererParams,
    asteriskFilter: boolean = false
  ) {
    const eGui = document.createElement('div');
    const col = params.column as Column;
    const data = params.data;
    eGui.style.overflow = 'hidden';
    eGui.style.textOverflow = 'ellipsis';
    // @ts-ignore Cannot invoke an object which is possibly 'undefined'.ts(2722)
    let value = params.getValue();
    const highlight = data && data.highlight;
    if (
      typeof highlight === 'object' &&
      highlight.hasOwnProperty(col.getColId())
    ) {
      value = highlight[col.getColId()];
    } else if (typeof value === 'number') {
      value = value + '';
      // @ts-ignore Cannot invoke an object which is possibly 'undefined'.ts(2722)
    } else if (params.column.isFilterActive()) {
      // @ts-ignore Cannot invoke an object which is possibly 'undefined'.ts(2722)
      const filterModel = params.api.getFilterModel()[params.colDef.field];
      if (
        filterModel.filterType !== 'autocomplete' &&
        filterModel.filterType !== 'number' &&
        filterModel.filterType !== 'date'
      ) {
        let ftVal = filterModel.filter;
        if (asteriskFilter && typeof ftVal === 'string') {
          ftVal = ftVal.replace(/\*/g, '');
        }
        value = highlightPipe.transform(value, ftVal);
      }
    }

    if (value) {
      eGui.innerHTML = value;
      // @ts-ignore Cannot invoke an object which is possibly 'undefined'.ts(2722)
      eGui.title = params.getValue();
      eGui.style.height = '100%';
      eGui.style.width = '100%';
    }
    return eGui;
  }

  public static comparatorDate(
    filterLocalDateAtMidnight: Date,
    cellValue: any
  ) {
    let dateAsString = cellValue;
    if (dateAsString == null) {
      return 0;
    }
    if (DateTime.isDateTime(dateAsString)) {
      dateAsString = dateAsString.toISODate();
    }
    // In the example application, dates are stored as yyyy/mm/dd
    // We create a Date object for comparison against the filter date
    const dateParts = dateAsString.split('-');
    const day = Number(dateParts[2]);
    const month = Number(dateParts[1]) - 1;
    const year = Number(dateParts[0]);
    const cellDate = new Date(year, month, day);

    // Now that both parameters are Date objects, we can compare
    if (cellDate < filterLocalDateAtMidnight) {
      return -1;
    } else if (cellDate > filterLocalDateAtMidnight) {
      return 1;
    } else {
      return 0;
    }
  }

  private static _resolveNumberFilterTooltip(filterModel: NumberFilterModel) {
    switch (filterModel.type) {
      case 'inRange':
        return ` Number in range: \n \u{25C6} ${filterModel.filter} ~ ${filterModel.filterTo}`;
      case 'equals':
        return ' Number equals: \n \u{25C6} ' + filterModel.filter;
      case 'lessThan':
        return ' Number less than: \n \u{25C6} ' + filterModel.filter;
      case 'greaterThan':
        return ' Number greater than: \n \u{25C6} ' + filterModel.filter;
      case 'notEqual':
        return ' Number not equal: \n \u{25C6} ' + filterModel.filter;
      case 'empty':
        return ' Number empty';
      default:
        return ` Number: \n \u{25C6} ` + filterModel.filter;
    }
  }

  private static _resolveTextFilterTooltip(filterModel: TextFilterModel) {
    switch (filterModel.type) {
      case 'equals':
        return ' Text equals: \n \u{25C6} ' + filterModel.filter;
      case 'notEqual':
        return ' Text not equal: \n \u{25C6} ' + filterModel.filter;
      case 'contains':
        return ' Text contains: \n \u{25C6} ' + filterModel.filter;
      case 'notContains':
        return ' Text not contains: \n \u{25C6} ' + filterModel.filter;
      case 'startsWith':
        return ' Text starts with: \n \u{25C6} ' + filterModel.filter;
      case 'endsWith':
        return ' Text end with: \n \u{25C6} ' + filterModel.filter;
      case 'fullTextSearch' as any:
        return ' Full text search: \n \u{25C6} ' + filterModel.filter;
      case 'empty':
        return ' Text empty';
      default:
        return ` Text: \n \u{25C6} ` + filterModel.filter;
    }
  }
  private static _resolveDateFilterTooltip(filterModel: DateFilterModel) {
    switch (filterModel.type) {
      case 'inRange':
        return ` Date in range: \n \u{25C6} ${filterModel.dateFrom} ~ ${filterModel.dateTo}`;
      case 'equals':
        return ' Date equals: \n \u{25C6} ' + filterModel.dateFrom;
      case 'lessThan':
        return ' Date less than: \n \u{25C6} ' + filterModel.dateFrom;
      case 'greaterThan':
        return ' Date greater than: \n \u{25C6} ' + filterModel.dateFrom;
      case 'notEqual':
        return ' Date not equal: \n \u{25C6} ' + filterModel.dateFrom;
      case 'empty':
        return ' Date empty';
      default:
        return ` Date: \n \u{25C6} ` + filterModel.dateFrom;
    }
  }
}
