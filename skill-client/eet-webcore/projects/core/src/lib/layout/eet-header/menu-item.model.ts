import { Type } from '@angular/core';
import { ClosableOverlayComponentModel, LogoutComponent } from '@bci-web-core/core';

export class MenuItem {
  id?: string;
  title: string = '';
  hide?:boolean
  icon?: string;
  svgIcon?: string;
  iconFont?: string;
  children?:MenuItem[];
  badge?: number;
  cb?: (event: MouseEvent, self:MenuItem) => void;
  overlay?: {
    component: Type<ClosableOverlayComponentModel<any>>;
    config: any;
  };
}