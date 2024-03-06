export interface ConfirmDialogModel {
  content: string;
  title: string;
  btnConfirm?: string;
  btnCancel?: string;
  icon?: string;
  isShowOKButton?: boolean;
}

export interface CommonDialogModel {
  title: string;
  icon?: string;
  component: any;
  width: string;
  maxWdith?: string;
  height: string;
  passingData: any;
  type: any;
}
