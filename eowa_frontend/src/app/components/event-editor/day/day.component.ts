import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Day } from '../../../Model/Day';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { HourComponent } from '../hour/hour.component';
import { Hour } from '../../../Model/Hour';
import { Opinion } from '../../../Model/Opinion';
import { OpinionMode } from '../view-calendar/view-calendar.component';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-day',
  standalone: true,
  imports: [MatCardModule, MatButtonModule, MatChipsModule, HourComponent],
  templateUrl: './day.component.html',
  styleUrl: './day.component.css',
})
export class DayComponent {
  @Input() day?: Day;

  @Input() editMode: Boolean = false;

  @Input() participantNumber = 0;

  @Input() opinionMode = OpinionMode.DEFAULT;

  @Output() dayChanged = new EventEmitter<Day>();

  @Output() dayHoursChanged = new EventEmitter<Hour>();

  @Output() opinionsSet = new EventEmitter<Opinion[]>();

  wholeDayChanged: Subject<void> = new Subject();

  OpinionMode = OpinionMode;

  disableDay() {
    this.day!.enabled = false;
    this.dayChanged.emit(this.day);
  }

  enableDay() {
    this.day!.enabled = true;
    this.dayChanged.emit(this.day);
  }

  hourChanged(hour: Hour) {
    this.dayHoursChanged.emit(hour);
  }

  opinionSet(opinion: Opinion) {
    this.opinionsSet.emit([opinion]);
  }

  addOpinion() {
    this.wholeDayChanged.next();
  }
}
