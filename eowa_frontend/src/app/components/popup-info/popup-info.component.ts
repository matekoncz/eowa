import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';

@Component({
  standalone: true,
  selector: 'popup-info',
  templateUrl: 'popup-info.component.html',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatButtonModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
  ],
})
export class PopUpInfoComponent {
  readonly dialogRef = inject(MatDialogRef<PopUpInfoComponent>);

  readonly data = inject<DialogData>(MAT_DIALOG_DATA);

  constructor(private router: Router) {}

  onOkClick(): void {
    this.dialogRef.close();
  }
}

export interface DialogData {
  title: string;
  content: string;
  html?: string;
}
