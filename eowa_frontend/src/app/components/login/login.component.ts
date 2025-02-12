import { Component } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, FormsModule, ValidationErrors, ValidatorFn } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import {MatFormFieldModule} from '@angular/material/form-field';
import {ReactiveFormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {Validators} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { HttpStatusCode } from '@angular/common/http';
import { from } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [FormsModule,ReactiveFormsModule,MatFormFieldModule,MatInputModule,MatButtonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginform: FormGroup = new FormGroup({

    username: new FormControl('',[Validators.required, Validators.minLength(8)]),
  
    password: new FormControl('',[Validators.required, Validators.minLength(8)]),

  })

  

  constructor(private authservice: AuthService, private router: Router){
  }

  login(){
    if(!this.loginform.valid){
      return;
    }
    this.authservice.logIn({
      username: this.loginform.value["username"]!,
      password: this.loginform.value["password"]!
    }).subscribe((response) => {
      let status = response.status;
      if(status == HttpStatusCode.Ok){
        window.alert("login successful")
        this.router.navigate(["/"]);
      } else {
        from(response.text()).subscribe((message)=>{
          window.alert("error: "+message);
        })
      }
    });
  }
}
