import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { from } from 'rxjs';
import { environment } from '../environment';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  constructor() {}

  private getBaseRequestInit(): RequestInit {
    let init = {
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
        credentials: 'include',
        Authorization: '',
      },
    };

    if (localStorage.getItem('jwt')) {
      init.headers.Authorization = localStorage.getItem('jwt')!;
    } else {
    }

    return init;
  }

  get(controller: Controller, path: string) {
    let getRequestInit = this.getBaseRequestInit();
    getRequestInit.method = 'GET';
    return this.sendRequest(controller.toString() + path, getRequestInit);
  }

  post(controller: Controller, path: string, content?: Object) {
    let postRequestInit = this.getBaseRequestInit();
    postRequestInit.body = JSON.stringify(content);
    postRequestInit.method = 'POST';
    return this.sendRequest(controller.toString() + path, postRequestInit);
  }

  put(controller: Controller, path: string, content?: Object) {
    let putRequestInit = this.getBaseRequestInit();
    putRequestInit.body = JSON.stringify(content);
    putRequestInit.method = 'PUT';
    return this.sendRequest(controller.toString() + path, putRequestInit);
  }

  delete(controller: Controller, path: string, content?: Object) {
    let deleteRequestInit = this.getBaseRequestInit();
    if (content) {
      deleteRequestInit.body = JSON.stringify(content);
    }
    deleteRequestInit.method = 'Delete';
    return this.sendRequest(controller.toString() + path, deleteRequestInit);
  }

  sendRequest(url: string, request: RequestInit): Observable<Response> {
    return from(fetch(environment.eowaUrl + url, request));
  }
}

export enum Controller {
  AUTH = 'auth',
  USER = 'user',
  EVENT = 'events',
  MAIL = 'mails',
}
