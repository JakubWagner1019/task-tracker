import {Component, EventEmitter, Input, Output} from '@angular/core';
import { Task } from '../task';
import {RouterLink} from "@angular/router";

@Component({
  selector: '[tt-task-row]',
  standalone: true,
  imports: [
    RouterLink
  ],
  templateUrl: './task-row.component.html',
  styleUrl: './task-row.component.css'
})
export class TaskRowComponent {
  @Input() task!: Task;
  @Output() taskSelected: EventEmitter<Task> = new EventEmitter<Task>();

  select() {
    this.taskSelected.emit(this.task);
  }
}
