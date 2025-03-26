import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, FormsModule } from '@angular/forms';
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
import { MatSnackBar } from '@angular/material/snack-bar';

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
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  loginform: FormGroup = new FormGroup({
    username: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
    ]),

    password: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
    ]),
  });

  dialog = inject(MatDialog);

  snackbar = inject(MatSnackBar);

  constructor(private authservice: AuthService, private router: Router) {}

  login() {
    if (!this.loginform.valid) {
      return;
    }
    this.authservice
      .logIn({
        username: this.loginform.value['username']!,
        password: this.loginform.value['password']!,
      })
      .subscribe((response) => {
        let status = response.status;
        if (status == HttpStatusCode.Ok) {
          this.snackbar.open('Logged in successfully', 'Close', {
            duration: 5000,
          });
          this.router.navigate(['/']);
        } else {
          from(response.text()).subscribe((message) => {
            const dialogRef = this.dialog.open(PopUpInfoComponent, {
              data: { title: 'Error', content: message },
            });
          });
        }
      });
  }
}
