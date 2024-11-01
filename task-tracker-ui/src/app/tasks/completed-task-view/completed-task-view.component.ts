import { Component } from '@angular/core';
import {TaskListComponent} from "../task-list/task-list.component";

@Component({
  selector: 'tt-completed-task-view',
  standalone: true,
  imports: [
    TaskListComponent
  ],
  templateUrl: './completed-task-view.component.html',
  styleUrl: './completed-task-view.component.css'
})
export class CompletedTaskViewComponent {

}
