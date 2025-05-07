import { Component, Input, Output } from '@angular/core';
import { Day } from '../../../Model/Day';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { HourComponent } from '../hour/hour.component';
import { Hour } from '../../../Model/Hour';
import { Opinion } from '../../../Model/Opinion';
import {
  EditMode,
  OpinionMode,
} from '../view-calendar/view-calendar.component';
import { Subject } from 'rxjs';
import { TimeIntervalDetails } from '../../../Model/TimeIntervalDetails';

@Component({
  selector: 'app-day',
  standalone: true,
  imports: [MatCardModule, MatButtonModule, MatChipsModule, HourComponent],
  templateUrl: './day.component.html',
  styleUrl: './day.component.css',
})
export class DayComponent {
  @Input() day?: Day;

  @Input() editMode: EditMode = EditMode.DEFAULT;

  @Input() participantNumber = 0;
  
  @Input() $showTimeInterval?: Subject<TimeIntervalDetails>;

  @Input() opinionMode = OpinionMode.DEFAULT;

  @Output() dayChanged = new Subject<Day>();

  @Output() dayHoursChanged = new Subject<Hour>();

  @Output() opinionsSet = new Subject<Opinion[]>();

  @Output() hourSelected = new Subject<Hour>();

  wholeDayChanged: Subject<void> = new Subject();

  OpinionMode = OpinionMode;

  EditMode = EditMode;

  disableDay() {
    this.day!.enabled = false;
    this.dayChanged.next(this.day!);
  }

  enableDay() {
    this.day!.enabled = true;
    this.dayChanged.next(this.day!);
  }

  hourChanged(hour: Hour) {
    this.dayHoursChanged.next(hour);
  }

  opinionSet(opinion: Opinion) {
    this.opinionsSet.next([opinion]);
  }

  addOpinion() {
    this.wholeDayChanged.next();
  }

  selectHour(hour: Hour) {
    this.hourSelected.next(hour);
  }
}
