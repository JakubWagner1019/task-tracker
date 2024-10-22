import {Component, Input} from '@angular/core';
import { Task } from '../task';

@Component({
  selector: 'tt-task-detail',
  standalone: true,
  imports: [],
  templateUrl: './task-detail.component.html',
  styleUrl: './task-detail.component.css'
})
export class TaskDetailComponent {
  @Input() task?: Task;
}
