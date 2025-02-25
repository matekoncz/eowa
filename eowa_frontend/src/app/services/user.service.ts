import { Injectable } from '@angular/core';
import { User } from '../Model/User';
import { WebToken } from '../Model/WebToken';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  getCurrentUser(): User {
    return (JSON.parse(localStorage.getItem("jwt")!) as WebToken).user;
  }

  constructor() { }
}
