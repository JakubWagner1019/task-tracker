import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TaskRowComponent} from "../task-row/task-row.component";
import { Task } from '../task';
import {TaskService} from "../task.service";

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
  @Output() taskSelected: EventEmitter<Task> = new EventEmitter<Task>();
  tasks: Task[] = [];

  constructor(private taskService: TaskService) {
  }

  ngOnInit(): void {
    this.refresh();
  }

  refresh() {
    if(this.status) {
      this.taskService.getAllByStatus(this.status).subscribe(tasks => this.tasks = tasks);
    } else {
      this.taskService.getAll().subscribe(tasks => this.tasks = tasks);
    }
  }
}
