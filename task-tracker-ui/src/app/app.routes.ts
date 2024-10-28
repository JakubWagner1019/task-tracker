import { Routes } from '@angular/router';
import {TaskViewComponent} from "./tasks/task-view/task-view.component";
import {TaskDetailViewComponent} from "./tasks/task-detail-view/task-detail-view.component";

export const routes: Routes = [
  {path: 'tasks/:id', component: TaskDetailViewComponent},
  {path: 'tasks', component: TaskViewComponent},
  {path: '', redirectTo: 'tasks', pathMatch: 'full'},
];
