import { Component, inject } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import {
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { NumberToHourPipe } from '../../../pipes/number-to-hour.pipe';

@Component({
  selector: 'app-periodical-hour-disabler-component',
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatSelectModule,
    MatOptionModule,
    NumberToHourPipe,
  ],
  templateUrl: './periodical-hour-disabler-component.component.html',
  styleUrl: './periodical-hour-disabler-component.component.css',
})
export class PeriodicalHourDisablerComponentComponent {
  
  readonly dialogRef = inject(
    MatDialogRef<PeriodicalHourDisablerComponentComponent>
  );

  periodForm: FormGroup = new FormGroup({

    hours: new FormControl([], [Validators.required]),

    period: new FormControl(1, [Validators.required,Validators.pattern('^1|7$'),]),
  });

  hours: number[] = this.getNumbers();

  getNumbers() {
    let array = [];
    for (let i = 0; i < 24; i++) {
      array.push(i);
    }
    return array;
  }

  onOkClick(): void {
    if (this.periodForm.valid) {
      this.dialogRef.close({
        hourNumbers: this.periodForm.controls['hours'].value,
        period: this.periodForm.controls['period'].value,
      });
    }
  }

  onNoClick(): void {
    this.dialogRef.close(null);
  }
}

export interface PeriodicalHourDisablingDO {
  period: number;
  hourNumbers: number[];
}
