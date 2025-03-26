import {
  Component,
  EventEmitter,
  inject,
  Input,
  Output,
  OnInit,
} from '@angular/core';
import { Hour } from '../../../Model/Hour';
import { NumberToHourPipe } from '../../../pipes/number-to-hour.pipe';
import { CommonModule } from '@angular/common';
import {
  Opinion,
  UserOpinion,
  UserOpinionLookup,
} from '../../../Model/Opinion';
import { MatDialog } from '@angular/material/dialog';
import { OpinionDialogComponent } from '../opinion-dialog/opinion-dialog.component';
import { UserService } from '../../../services/user.service';
import {
  EditMode,
  OpinionMode,
} from '../view-calendar/view-calendar.component';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-hour',
  standalone: true,
  imports: [NumberToHourPipe, CommonModule],
  templateUrl: './hour.component.html',
  styleUrl: './hour.component.css',
})
export class HourComponent implements OnInit {
  @Input() hour?: Hour;

  @Input() editMode: EditMode = EditMode.DEFAULT;

  @Input() participantNumber = 0;

  @Input() date?: Date;

  @Input() wholeDayChanged?: Subject<void>;

  @Input() opinionMode = OpinionMode.DEFAULT;

  @Output() hourChanged = new EventEmitter<Hour>();

  @Output() opinionSet = new EventEmitter<Opinion>();

  @Output() hourSelected = new EventEmitter<Hour>();

  selected = false;

  dialog = inject(MatDialog);

  Editmode = EditMode;

  backgroundColor = '';

  constructor(private userservice: UserService) {}

  ngOnInit(): void {
    this.wholeDayChanged?.subscribe(() => {
      this.addOpinion(this.opinionMode.valueOf());
      this.calculateBackgroundColor();
    });
    this.calculateBackgroundColor();
  }

  mouseEnter() {
    if (this.hour?.enabled && this.editMode == EditMode.START_AND_END)
      this.backgroundColor = 'blueviolet';
  }

  mouseLeave() {
    this.calculateBackgroundColor();
  }

  calculateBackgroundColor() {
    if (!this.hour?.enabled) {
      this.backgroundColor = 'grey';
      return;
    }
    let sat = this.calculateSatisfaction();
    if (sat == -1) {
      this.backgroundColor = 'transparent';
      return;
    }
    let red = 3 * (1 - sat) * 255;
    let green = 3 * sat * 255;

    this.backgroundColor = 'rgb(' + red + ', ' + green + ', 0)';
  }

  hourClicked() {
    if (!this.hour?.enabled) {
      return;
    }
    switch (this.editMode) {
      case EditMode.EDIT:
        this.hour!.enabled = !this.hour!.enabled;
        this.hourChanged.emit(this.hour!);
        break;
      case EditMode.OPINION:
        this.addOpinion(this.opinionMode.valueOf());
        break;
      case EditMode.START_AND_END:
        this.hourSelected.emit(this.hour!);
        this.selected = true;
        break;
      case EditMode.DEFAULT:
        this.openOpinionDialog();
        break;
    }
    this.calculateBackgroundColor();
  }

  calculateSatisfaction(): number {
    if (this.hour != null && this.hour.enabled) {
      if (this.hour.opinions.length != 0) {
        let satisfactionLevel = 0;
        for (let opinion of this.hour.opinions) {
          if (opinion.userOpinion == UserOpinion.GOOD) {
            satisfactionLevel += 1;
          }
          if (opinion.userOpinion == UserOpinion.TOLERABLE) {
            satisfactionLevel += 0.5;
          }
        }
        return (satisfactionLevel = satisfactionLevel / this.participantNumber);
      }
    }
    return -1;
  }

  openOpinionDialog() {
    const numberToHourPipe = new NumberToHourPipe();
    let dialogRef = this.dialog.open(OpinionDialogComponent, {
      data: {
        title:
          this.date!.toDateString() +
          ' ' +
          numberToHourPipe.transform(this.hour!.number),
        hour: this.hour,
        participantNumber: this.participantNumber,
      },
    });

    dialogRef.afterClosed().subscribe((result: string) => {
      if (result == undefined || result == null) {
        return;
      }
      this.addOpinion(result);
      this.calculateBackgroundColor();
    });
  }

  addOpinion(opinionType: string) {
    let opinion: Opinion = {
      userOpinion: UserOpinionLookup[opinionType as keyof typeof UserOpinion],
      number: this.hour?.numberInTotal,
      user: this.userservice.getCurrentUser(),
    };
    this.opinionSet.emit(opinion);
    this.setUserOpinion(opinion);
  }

  setUserOpinion(opinion: Opinion) {
    for (let i = 0; i < this.hour!.opinions.length; i++) {
      let existingOpinion = this.hour!.opinions[i];
      if (existingOpinion.user.username == opinion.user.username) {
        this.hour!.opinions[i] = opinion;
        return;
      }
    }
    this.hour?.opinions.push(opinion);
    this.calculateBackgroundColor();
  }

  isSelected() {
    if (
      this.editMode != EditMode.START_AND_END &&
      this.editMode != EditMode.SHOW_BEST
    ) {
      this.selected = false;
    }
    return this.selected;
  }
}
