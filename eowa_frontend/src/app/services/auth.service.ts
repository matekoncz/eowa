import { Injectable } from '@angular/core';
import { ApiService, Controller } from './api.service';
import { User } from '../Model/User';
import { HttpStatusCode } from '@angular/common/http';
import { from, mergeMap, of, switchMap } from 'rxjs';
import { Credentials } from '../Model/Credentials';
import { WebToken } from '../Model/WebToken';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  constructor(private apiservice: ApiService) {}

  signUp(user: User) {
    return this.apiservice.post(Controller.AUTH, '/signup',user);
  }

  logIn(credentals: Credentials) {
    console.log("logging in");
    return this.apiservice.post(Controller.AUTH, '/login', credentals).pipe(switchMap((response: Response) => {
      if(response.status == HttpStatusCode.Ok){
        from(response.text()).subscribe((jwt)=>{
          localStorage.setItem("jwt",jwt);
        });
      }
      return of(response);
    }));
  }

  logOut() {
    let returnvalue = this.apiservice.delete(Controller.AUTH, '/logout');
    localStorage.removeItem("jwt");
    return returnvalue;
  }
}
