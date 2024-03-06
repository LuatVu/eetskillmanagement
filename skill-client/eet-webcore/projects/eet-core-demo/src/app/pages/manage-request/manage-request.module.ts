import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { SharedModule } from "@core/src/lib/shared/shared.module";
import { SharedCommonModule } from "../../shared/shared.module";
import { CommentDialogComponent } from "./components/dialogs/comment-dialog/comment-dialog.component";
import { ForwardRequestComponent } from "./components/forward-request/forward-request.component";
import { ViewRequestDetailComponent } from "./components/view-request-detail/view-request-detail.component";
import { ViewPreviousRequestDetailComponent } from "./components/view-previous-request-detail/view-previous-request-detail.component";
import { ManageRequestRoutingModule } from "./manage-request-routing.module";
import { ManageRequestComponent } from "./manage-request.component";
import { PendingRequestComponent } from "./pending-request/pending-request.component";
import { PreviousRequestComponent } from "./previous-request/previous-request.component";

@NgModule({
  declarations: [
    ManageRequestComponent,
    ForwardRequestComponent,
    ViewRequestDetailComponent,
    ViewPreviousRequestDetailComponent,
    CommentDialogComponent,
    PendingRequestComponent,
    PreviousRequestComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    SharedCommonModule,
    ManageRequestRoutingModule
  ],
  exports: []
})

export class ManageRequestModule { }
