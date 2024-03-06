export interface AppSettings {
  navPos: 'side' | 'top';
  dir: 'ltr' | 'rtl';
  theme: 'light' | 'dark';
  showHeader: boolean;
  headerPos: 'fixed' | 'static' | 'above';
  sidenavOpened: boolean;
  sidenavCollapsed: boolean;
  language: string;
  appName: string;
}

export const defaults: AppSettings = {
  navPos: 'side',
  dir: 'ltr',
  theme: 'light',
  showHeader: true,
  headerPos: 'fixed',
  sidenavOpened: true,
  sidenavCollapsed: false,
  language: 'de-DE',
  appName: 'EET'
};
