import { Injectable } from '@angular/core';
import { ApiService, Controller } from './api.service';
import { User } from '../Model/User';
import { HttpStatusCode } from '@angular/common/http';
import { from, of, switchMap } from 'rxjs';
import { Credentials } from '../Model/Credentials';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private authStatus: AuthStatus = AuthStatus.PENDING;

  constructor(private apiservice: ApiService) {
    if (localStorage.getItem('jwt')) {
      this.authStatus = AuthStatus.LOGGED_IN;
    } else {
      this.authStatus = AuthStatus.LOGGED_OUT;
    }
  }

  signUp(user: User) {
    return this.apiservice.post(Controller.AUTH, '/signup', user);
  }

  logIn(credentals: Credentials) {
    return this.apiservice.post(Controller.AUTH, '/login', credentals).pipe(
      switchMap((response: Response) => {
        if (response.status == HttpStatusCode.Ok) {
          from(response.text()).subscribe((jwt) => {
            localStorage.setItem('jwt', jwt);
          });
          this.authStatus = AuthStatus.LOGGED_IN;
        } else {
          this.authStatus = AuthStatus.LOGGED_OUT;
        }
        return of(response);
      })
    );
  }

  logOut() {
    let returnvalue = this.apiservice.delete(Controller.AUTH, '/logout');
    localStorage.removeItem('jwt');
    this.authStatus = AuthStatus.LOGGED_OUT;
    return returnvalue;
  }

  getAuthStatus() {
    return this.authStatus;
  }
}

export enum AuthStatus {
  PENDING,
  LOGGED_IN,
  LOGGED_OUT,
}
