import {Component, EventEmitter, Input, Output} from '@angular/core';
import { Task } from '../task';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TaskService} from "../task.service";

@Component({
  selector: 'tt-task-detail',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './task-detail.component.html',
  styleUrl: './task-detail.component.css'
})
export class TaskDetailComponent {
  @Input() task?: Task;
  @Output() refresh: EventEmitter<void> = new EventEmitter<void>();

  constructor(private _taskService: TaskService) {
  }

  onStatusChange(eventTarget: EventTarget | null) {
    if(eventTarget instanceof HTMLSelectElement) {
      let newStatus = (eventTarget as HTMLSelectElement).value;
      if(this.task) {
        let id = this.task.id;
        if(id) {
          if(newStatus && newStatus.length > 0) {
            console.log("Changing status of task " + id +" to " + newStatus)
            this._taskService.changeStatus(id, newStatus).subscribe(() => {
              this.refresh.emit();
              console.log("Changed status of task " + id + " to " + newStatus);
            });
          } else {
            console.error("Tried to change status to empty or undefined status", newStatus);
          }
        } else {
          console.error("Tried to change status for task with undefined ID")
        }
      } else {
        console.error("Tried to change status for undefined task")
      }
    } else {
      console.log(eventTarget, "is not instance of HTMLInputElement.");
    }
  }
}
