import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TaskRowComponent} from "../task-row/task-row.component";
import { Task } from '../task';
import {TaskService} from "../task.service";
import {Observable} from "rxjs";

@Component({
  selector: 'tt-task-list',
  standalone: true,
  imports: [TaskRowComponent],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.css'
})
export class TaskListComponent {
  @Input() status?: string;
  @Input() title?: string;
  tasks: Task[] = [];
  error: boolean = false;

  constructor(private taskService: TaskService) {
  }

  ngOnInit(): void {
    this.refresh();
  }

  refresh() {
    let observable: Observable<Task[]>
    if(this.status) {
      observable = this.taskService.getAllByStatus(this.status)
    } else {
      observable = this.taskService.getAll();
    }
    observable.subscribe({
      next : (tasks: Task[]) => {
        this.error = false;
        tasks.sort((a: Task, b: Task) => {return (a.id ? a.id : 0) - (b.id ? b.id : 0)});
        this.tasks = tasks
      },
      error: (error) => {
        this.error = true;
      }
    });
  }
}
