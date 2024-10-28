import { Component } from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {TaskListComponent} from "./tasks/task-list/task-list.component";
import {TaskViewComponent} from "./tasks/task-view/task-view.component";

@Component({
  selector: 'tt-root',
  standalone: true,
    imports: [RouterOutlet, TaskListComponent, TaskViewComponent, RouterLink],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'task-tracker-ui';
}
