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

  getById(id: number): Observable<Task> {
    return this.http.get<Task>(this.taskUrl + '/' + id);
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

  patchTask(id: number, task: Task): Observable<Task> {
    return this.http.patch<Task>(this.taskUrl + '/' + id, task);
  }
}
