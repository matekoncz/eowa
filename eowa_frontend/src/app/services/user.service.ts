import { Injectable } from '@angular/core';
import { User } from '../Model/User';
import { WebToken } from '../Model/WebToken';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  getCurrentUser(): User | null {
    if(localStorage.getItem('jwt') === null) {
      console.log('No jwt found');
      return null;
    }
    console.log(localStorage.getItem('jwt'));
      return (JSON.parse(localStorage.getItem('jwt')!) as WebToken).user;
  }

  constructor() {}
}
