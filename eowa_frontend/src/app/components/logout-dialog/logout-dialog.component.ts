import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialogActions,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-logout-dialog',
  standalone: true,
  imports: [FormsModule, MatButtonModule, MatDialogTitle, MatDialogActions],
  templateUrl: './logout-dialog.component.html',
  styleUrl: './logout-dialog.component.css',
})
export class LogoutDialogComponent {
  
  readonly dialogRef = inject(MatDialogRef<LogoutDialogComponent>);

  constructor(private authservice: AuthService, private router: Router) {}

  onNoClick(): void {
    this.dialogRef.close();
  }
  onYesClick(): void {
    this.authservice.logOut();
    this.dialogRef.close();
    this.router.navigate(['/']);
  }
}
