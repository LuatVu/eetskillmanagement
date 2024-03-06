import { RouterModule, Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { VModelComponent } from "./v-model.component";

const routes: Routes = [
  {
    path: '',
    component: VModelComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class VModelRoutingModule { }
