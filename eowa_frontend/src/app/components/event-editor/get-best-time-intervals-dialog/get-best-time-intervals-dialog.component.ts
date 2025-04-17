import { Component, inject } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
  MAT_DIALOG_DATA,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { EowaEvent } from '../../../Model/EowaEvent';
import { UserOpinion } from '../../../Model/Opinion';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-get-best-time-intervals-dialog',
  standalone: true,
  imports: [
    MatDialogActions,
    MatDialogContent,
    MatDialogTitle,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    FormsModule,
    ReactiveFormsModule,
    MatSelectModule,
  ],
  templateUrl: './get-best-time-intervals-dialog.component.html',
  styleUrl: './get-best-time-intervals-dialog.component.css',
})
export class GetBestTimeIntervalsDialogComponent {
  event: EowaEvent = inject<EowaEvent>(MAT_DIALOG_DATA);

  intervalsForm: FormGroup = new FormGroup({
    participants: new FormControl(1, [
      Validators.required,
      Validators.min(1),
      Validators.max(this.event.participants.length),
    ]),
    length: new FormControl(1, [
      Validators.required,
      Validators.min(1),
      Validators.max(24 * 14),
    ]),
    allowedOpinions: new FormControl([], [Validators.required]),
    popularityMode: new FormControl(true, [Validators.required]),
  });

  UserOpinion = UserOpinion;

  readonly dialogRef = inject(
    MatDialogRef<GetBestTimeIntervalsDialogComponent>
  );

  onNoClick() {
    this.dialogRef.close(null);
  }
  onOkClick() {
    this.dialogRef.close({
      participants: this.intervalsForm.get('participants')?.value,
      length: this.intervalsForm.get('length')?.value,
      allowedOpinions: this.intervalsForm.get('allowedOpinions')?.value,
      popularityMode: this.intervalsForm.get('popularityMode')?.value,
    });
  }
}
