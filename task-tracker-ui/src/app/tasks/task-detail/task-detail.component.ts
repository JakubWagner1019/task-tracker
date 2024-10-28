import {Component, EventEmitter, Output} from '@angular/core';
import {Task} from '../task';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TaskService} from "../task.service";
import {Observable} from "rxjs";
import {deepCopy} from "../../util/deep-copy";

interface StatusChange {
  type: 'status';
  status: string;
}

interface TitleChange {
  type: 'title',
  title: string;
}

interface DescriptionChange {
  type: 'description',
  description: string;
}

type Change = StatusChange | TitleChange | DescriptionChange;

const isStatusChange = (change: Change): change is StatusChange => change.type === 'status';
const isTitleChange = (change: Change): change is TitleChange => change.type === 'title';
const isDescriptionChange = (change: Change): change is DescriptionChange => change.type === 'description';

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
  @Output() taskUpdated: EventEmitter<Task> = new EventEmitter<Task>();

  task?: Task;
  originalTask?: Task;

  changes: Change[] = []

  constructor(private _taskService: TaskService) {
  }

  statusChanged(eventTarget: EventTarget | null) {
    let newStatus = TaskDetailComponent.getValueFromHtmlSelectEvent(eventTarget);
    if (newStatus && newStatus.length > 0) {
      this.changes.push({type: 'status', status: newStatus});
    } else {
      console.warn("Tried to change status to empty or undefined", newStatus);
    }
  }

  static aggregateChanges(changes: Change[]): Task {
    let task: Task = {};
    console.log("Changes: ", changes);
    for (let change of changes) {
      if (isStatusChange(change)) {
        task.status = change.status;
      } else if (isTitleChange(change)) {
        task.title = change.title;
      } else if (isDescriptionChange(change)) {
        task.description = change.description;
      }
    }
    console.log("Aggregated changes", task);
    return task;
  }

  submit() {
    if (this.changes.length === 0) {
      console.error("No changes were found");
      return;
    }

    let taskUpdate = TaskDetailComponent.aggregateChanges(this.changes);
    this.changes = [];

    if (this.task) {
      let id = this.task.id;
      if (id) {
        console.log("Updating task " + id + " with " + taskUpdate);
        this._taskService.patchTask(id, taskUpdate).subscribe({
          next: (task) => {
            this.taskUpdated.emit(task);
          }
        });
      } else {
        console.warn("Tried to update a task with undefined ID")
      }
    }
  }

  titleChanged(eventTarget: EventTarget | null) {
    let newTitle = TaskDetailComponent.getInnerHtmlFromContentEditableEvent(eventTarget);
    if (newTitle && newTitle.length > 0) {
      if(this.task && newTitle !== this.task.title) {
        this.task.title = newTitle;
        this.changes.push({type: 'title', title: newTitle});
      }
    } else {
      console.warn("Tried to change title to empty or undefined", newTitle);
    }
  }

  descriptionChanged(target: EventTarget | null) {
    let newDescription = TaskDetailComponent.getInnerHtmlFromContentEditableEvent(target);
    if (newDescription && newDescription.length > 0) {
      if(this.task && newDescription !== this.task.description) {
        this.task.description = newDescription;
        this.changes.push({type: 'description', description: newDescription});
      }
    } else {
      console.warn("Tried to change description to empty or undefined", newDescription);
    }
  }

  static getInnerHtmlFromContentEditableEvent(target: EventTarget | null): string | null {
    if (target) {
      if (target instanceof HTMLElement) {
        if (target.innerHTML) {
          return target.innerHTML;
        }
      } else {
        console.log(target, "is not instance of HTMLInputElement.");
      }
    } else {
      console.warn("Tried to change status for undefined task")
    }
    return null;
  }

  static getValueFromHtmlSelectEvent(target: EventTarget | null): string | null {
    if (target) {
      if (target instanceof HTMLSelectElement) {
        if (target.value) {
          return target.value;
        }
      } else {
        console.log(target, "is not instance of HTMLInputElement.");
      }
    } else {
      console.warn("event target null or undefined:", target);
    }
    return null;
  }

  dismiss() {
    this.changes = []
    this.task = deepCopy(this.originalTask);
  }

  selectTask(task: Task) {
    this.originalTask = task;
    this.task = deepCopy(task);
  }
}
