import {Component, QueryList, ViewChildren} from '@angular/core';
import {TaskListComponent} from "../task-list/task-list.component";
import {Task} from "../task";
import {TaskDetailComponent} from "../task-detail/task-detail.component";
import {TaskCreationFormComponent} from "../task-creation-form/task-creation-form.component";

@Component({
  selector: 'tt-task-view',
  standalone: true,
  imports: [
    TaskListComponent,
    TaskDetailComponent,
    TaskCreationFormComponent
  ],
  templateUrl: './task-view.component.html',
  styleUrl: './task-view.component.css'
})
export class TaskViewComponent {
  @ViewChildren(TaskListComponent) taskLists!: QueryList<TaskListComponent>;

  refreshTaskLists(task?: Task) {
    this.taskLists.forEach((taskList: TaskListComponent) => {
      taskList.refresh();
    })
  }
}
