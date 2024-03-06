import { RouterModule, Routes } from "@angular/router";
import { ManageRequestComponent } from "./manage-request.component";
import { NgModule } from "@angular/core";
import { PendingRequestComponent } from "./pending-request/pending-request.component";
import { PreviousRequestComponent } from "./previous-request/previous-request.component";

const routes: Routes = [
  {
    path: '',
    component: ManageRequestComponent,
    children: [
      { path: '', redirectTo: 'pending-request', pathMatch: 'full' },
      { path: 'pending-request', component:  PendingRequestComponent},
      { path: 'approved-rejected-request', component:  PreviousRequestComponent},      
    ]
  }
]


@NgModule({
  imports: [RouterModule.forChild(routes)]
})

export class ManageRequestRoutingModule { }
