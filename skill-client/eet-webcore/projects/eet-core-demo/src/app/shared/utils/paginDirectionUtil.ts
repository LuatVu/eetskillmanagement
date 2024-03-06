import { CONFIG } from "../constants/config.constants";

export class PaginDirectionUtil {
    public static addClassForOptionsPagination(pagin:any): void {
      const selectOptionElement = document.querySelector('mat-paginator mat-select')?.getBoundingClientRect()
      const boundTop = selectOptionElement?.top || 0
      const boundBottom = selectOptionElement?.bottom || 0
      if(pagin.getBoundingClientRect().bottom > window.innerHeight-200) {
        const overlayPane = document.querySelector('.cdk-overlay-pane') as HTMLElement
        if(overlayPane) {
          overlayPane.style.top = (boundTop-31).toString() + 'px'
          overlayPane.style.bottom = boundBottom.toString() + 'px'
        }
        document.querySelector('.mat-select-panel')?.classList.add(CONFIG.DIRECTION_TOP_EXPAND_DROPDOWN_PAGINATION)
      }else{
        document.querySelector('.mat-select-panel')?.classList.add(CONFIG.DIRECTION_BOTTOM_EXPAND_DROPDOWN_PAGINATION)
      }
    }
    public static expandTopForDropDownPagination(): void {
      const dropdowns = document.querySelectorAll('mat-paginator')
        dropdowns?.forEach((e) => {
          e.addEventListener('click', () => this.addClassForOptionsPagination(e));
        })
    }
    public static removeEventListenerForDirectionExpandPagination(): void {
      const dropdowns = document.querySelectorAll('mat-paginator')
        dropdowns?.forEach((e) => {
          e.removeEventListener('click', this.addClassForOptionsPagination);
        })
    }
    
  }
  