import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Task} from './task';

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  constructor(private http: HttpClient) {}

  getAll(): Observable<Task[]> {
    return this.http.get<Task[]>("http://localhost:8080/api/tasks");
  }

  getAllByStatus(status: string) : Observable<Task[]> {
    return this.http.get<Task[]>("http://localhost:8080/api/tasks?status=" + status);
  }

  createTask(task: Task): Observable<void> {
    return this.http.post<void>("http://localhost:8080/api/tasks", task);
  }
}
