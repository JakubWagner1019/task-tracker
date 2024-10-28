import {Component, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {TaskListComponent} from "../task-list/task-list.component";
import {Task} from "../task";
import {TaskDetailComponent} from "../task-detail/task-detail.component";
import {TaskCreationFormComponent} from "../task-creation-form/task-creation-form.component";
import {TaskService} from "../task.service";

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
  @ViewChild(TaskDetailComponent) taskDetail!: TaskDetailComponent;
  @ViewChildren(TaskListComponent) taskLists!: QueryList<TaskListComponent>;
  selectedTask?: Task;

  constructor(private _taskService: TaskService) {
  }

  selectTask(task: Task): void {
    this.taskDetail.selectTask(task);
  }

  refreshTaskLists(task?: Task) {
    if(task) {
      this.taskDetail.selectTask(task);
    }
    this.taskLists.forEach((taskList: TaskListComponent) => {
      taskList.refresh();
    })
  }
}
