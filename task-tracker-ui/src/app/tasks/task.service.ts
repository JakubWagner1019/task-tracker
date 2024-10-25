import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Task} from './task';
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private taskUrl: string;

  constructor(private http: HttpClient) {
    this.taskUrl = environment.baseUrl + '/api/tasks';
  }

  getAll(): Observable<Task[]> {
    return this.http.get<Task[]>(this.taskUrl);
  }

  getAllByStatus(status: string) : Observable<Task[]> {
    return this.http.get<Task[]>(this.taskUrl + "?status=" + status);
  }

  createTask(task: Task): Observable<void> {
    return this.http.post<void>(this.taskUrl, task);
  }

  changeStatus(id: number, status: string): Observable<void> {
    return this.http.patch<void>(this.taskUrl + '/' + id, {status: status});
  }
}
