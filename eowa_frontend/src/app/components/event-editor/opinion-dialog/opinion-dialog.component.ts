import {
  AfterViewInit,
  Component,
  ElementRef,
  inject,
  ViewChild,
} from '@angular/core';
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
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Hour } from '../../../Model/Hour';
import { UserOpinion } from '../../../Model/Opinion';

@Component({
  selector: 'app-opinion-dialog',
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
  ],
  templateUrl: './opinion-dialog.component.html',
  styleUrl: './opinion-dialog.component.css',
})
export class OpinionDialogComponent implements AfterViewInit {
  @ViewChild('barCanvas')
  canvas?: ElementRef<HTMLCanvasElement>;

  readonly dialogRef = inject(MatDialogRef<OpinionDialogComponent>);

  readonly data = inject<OpinionData>(MAT_DIALOG_DATA);

  context?: CanvasRenderingContext2D;

  opinionForm: FormGroup = new FormGroup({
    opinion: new FormControl('', [Validators.required]),
  });

  UserOpinion = UserOpinion;

  ngAfterViewInit(): void {
    this.drawStats();
  }

  private drawStats() {
    this.context = this.canvas?.nativeElement.getContext('2d') || undefined;
    let redX =
      this.getPercentage(
        this.data.participantNumber,
        this.getParticipantsFor(UserOpinion.BAD).length
      ) * 3;
    let yellowX =
      this.getPercentage(
        this.data.participantNumber,
        this.getParticipantsFor(UserOpinion.TOLERABLE).length
      ) * 3;
    let greenX =
      this.getPercentage(
        this.data.participantNumber,
        this.getParticipantsFor(UserOpinion.GOOD).length
      ) * 3;

    if (this.context) {
      this.context.fillStyle = 'grey';
      this.context.fillRect(0, 0, 300, 150);
      this.context.fillStyle = 'red';
      this.context.fillRect(0, 0, redX, 150);
      this.context.fillStyle = 'yellow';
      this.context.fillRect(redX, 0, yellowX, 150);
      this.context.fillStyle = 'green';
      this.context.fillRect(redX + yellowX, 0, greenX, 150);
    }
  }

  onOkClick(): void {
    if (this.opinionForm.valid) {
      this.dialogRef.close(this.opinionForm.controls['opinion'].value);
    }
  }

  onNoClick(): void {
    this.dialogRef.close(null);
  }

  getAnswerPercentage() {
    return this.getPercentage(
      this.data.participantNumber,
      this.data.hour.opinions.length
    );
  }

  getParticipantsPercentage(userOpinion: UserOpinion) {
    return this.getPercentage(
      this.data.hour.opinions.length,
      this.getParticipantsFor(userOpinion).length
    );
  }

  getPercentage(all: number, part: number) {
    if (all == 0 || part == 0) {
      return 0;
    }
    return (part / all) * 100;
  }

  getParticipantsFor(opinionType: UserOpinion) {
    let opinionArray = [];
    for (let opinion of this.data.hour.opinions) {
      if (opinion.userOpinion == opinionType) {
        opinionArray.push(opinion.user.username);
      }
    }
    return opinionArray;
  }
}

export interface OpinionData {
  hour: Hour;
  participantNumber: number;
  title: string;
}
