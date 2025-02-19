import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { from } from 'rxjs';
import { environment } from '../environment';
import { WebToken } from '../Model/WebToken';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor() {
  }

  private getBaseRequestInit(): RequestInit{
    let init = {
      headers: {
        'Content-Type' : 'application/json',
        'Accept' : 'application/json',
        'credentials' : 'include',
        'Authorization' : ''
      }
    }

    if(localStorage.getItem("jwt")){
      console.log("jwt: "+localStorage.getItem("jwt"));
      init.headers.Authorization = localStorage.getItem("jwt")!;
    } else {
      console.log("no jwt");
    }

    return init;
  }


  get(controller: Controller, path: string){
    let getRequestInit = this.getBaseRequestInit()
    getRequestInit.method = 'GET'
    return this.sendRequest(controller.toString()+path,getRequestInit)
  }

  post(controller: Controller, path: string, content: Object,){
    let postRequestInit = this.getBaseRequestInit()
    postRequestInit.body = JSON.stringify(content)
    postRequestInit.method = 'POST'
    return this.sendRequest(controller.toString()+path,postRequestInit)
  }

  put(controller: Controller, path: string, content: Object,){
    let postRequestInit = this.getBaseRequestInit()
    postRequestInit.body = JSON.stringify(content)
    postRequestInit.method = 'PUT'
    return this.sendRequest(controller.toString()+path,postRequestInit)
  }

  delete(controller: Controller, path: string, content?: Object){
    let postRequestInit = this.getBaseRequestInit()
    if(content){
      postRequestInit.body = JSON.stringify(content)
    }
    postRequestInit.method = 'Delete'
    return this.sendRequest(controller.toString()+path,postRequestInit)
  }

  sendRequest(url: string, request: RequestInit): Observable<Response>{
    return from(fetch(environment.eowaUrl+url,request));
  }

  
}

export enum Controller{
  AUTH = "auth",
  USER = "user",
  EVENT = "events"
}

