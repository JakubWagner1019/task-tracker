import {Component, ViewChild} from '@angular/core';
import {TaskService} from "../task.service";
import {TaskDetailComponent} from "../task-detail/task-detail.component";
import {Task} from "../task";
import {ActivatedRoute, RouterLink} from "@angular/router";

@Component({
  selector: 'tt-task-detail-view',
  standalone: true,
  imports: [
    TaskDetailComponent,
    RouterLink
  ],
  templateUrl: './task-detail-view.component.html',
  styleUrl: './task-detail-view.component.css'
})
export class TaskDetailViewComponent {
  @ViewChild(TaskDetailComponent) taskDetail!: TaskDetailComponent;
  task?: Task;

  constructor(private route: ActivatedRoute, private _taskService: TaskService) {
  }

  ngOnInit(): void {
    let id = this.route.snapshot.paramMap.get("id");
    if(id && id.length > 0) {
      let idAsNumber = +id;
      if(!isNaN(idAsNumber)) {
        this._taskService.getById(idAsNumber).subscribe({
          next: task => {
            this.task = task;
          }
        })
      }
    }

  }

}
