import { Component, inject } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  FormsModule,
} from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { HttpStatusCode } from '@angular/common/http';
import { from } from 'rxjs';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { PopUpInfoComponent } from '../popup-info/popup-info.component';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css',
})
export class SignupComponent {

  signupform: FormGroup = new FormGroup({
    username: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
    ]),

    email: new FormControl('', [Validators.required, Validators.email]),

    password: new FormControl('', [Validators.required, Validators.minLength(8)]),

    confirmedPassword: new FormControl('', [Validators.required, this.passwordValidator()]),
  });

  dialog = inject(MatDialog);

  constructor(private authservice: AuthService, private router: Router) {}

  signUp() {
    if (!this.signupform.valid) {
      return;
    }
    this.authservice
      .signUp({
        username: this.signupform.value['username']!,
        email: this.signupform.value['email']!,
        password: this.signupform.value['password']!,
      })
      .subscribe((response) => {
        let status = response.status;
        if (status == HttpStatusCode.Ok) {
          const dialogRef = this.dialog.open(PopUpInfoComponent, {
            data: { title: 'Success', content: 'User created successfully' },
          });
          this.router.navigate(['/login']);
        } else {
          from(response.text()).subscribe((message) => {
            const dialogRef = this.dialog.open(PopUpInfoComponent, {
              data: { title: ' Error', content: message },
            });
          });
        }
      });
  }

  passwordValidator() {
    return (control: AbstractControl) => {
      if (this.signupform != undefined) {
        const passwordfield = this.signupform.get('password');
        const confirmfield = this.signupform.get('confirmedPassword');
        if (passwordfield == null || confirmfield == null) {
          return { passwordValidator: false };
        }
        if (passwordfield.value == confirmfield.value) {
          confirmfield.setErrors(null);
          return null;
        } else {
          control.setErrors({ passwordValidator: true });
          return { passwordValidator: true };
        }
      }
      return null;
    };
  }
}
