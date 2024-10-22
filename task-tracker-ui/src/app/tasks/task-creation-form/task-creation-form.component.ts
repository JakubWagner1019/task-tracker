import {Component, EventEmitter, Output} from '@angular/core';
import {TaskService} from "../task.service";
import { Task } from '../task';
import {JsonPipe} from "@angular/common";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'tt-task-creation-form',
  standalone: true,
  imports: [
    JsonPipe,
    FormsModule
  ],
  templateUrl: './task-creation-form.component.html',
  styleUrl: './task-creation-form.component.css'
})
export class TaskCreationFormComponent {
  @Output() refresh: EventEmitter<void> = new EventEmitter<void>();

  model: Task = this.newTask();

  private newTask() {
    return {status: "Open"};
  }

  constructor(private _taskService: TaskService) {
  }

  submit() {
    if(this.model.title && this.model.title.length > 0) {
      if(!this.model.status || this.model.status.length === 0) {
        this.model.status = "Open";
      }
      this._taskService.createTask(this.model).subscribe(() => {
        this.refresh.emit();
      });
      this.model = this.newTask();
    }
  }
}
