import { Routes } from '@angular/router';
import {TaskViewComponent} from "./tasks/task-view/task-view.component";
import {TaskDetailViewComponent} from "./tasks/task-detail-view/task-detail-view.component";
import {TaskCreationFormComponent} from "./tasks/task-creation-form/task-creation-form.component";
import {CompletedTaskViewComponent} from "./tasks/completed-task-view/completed-task-view.component";

export const routes: Routes = [
  {path: 'tasks/:id', component: TaskDetailViewComponent},
  {path: 'tasks', component: TaskViewComponent},
  {path: 'createTask', component: TaskCreationFormComponent},
  {path: 'completedTasks', component: CompletedTaskViewComponent},
  {path: '', redirectTo: 'tasks', pathMatch: 'full'},
];
