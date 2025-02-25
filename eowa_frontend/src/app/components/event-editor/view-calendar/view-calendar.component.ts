import { Component, inject, OnInit } from '@angular/core';
import { Calendar } from '../../../Model/Calendar';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom, from } from 'rxjs';
import { EowaEvent } from '../../../Model/EowaEvent';
import { EventService } from '../../../services/event.service';
import { Day } from '../../../Model/Day';
import { DayComponent } from '../day/day.component';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatButtonModule } from '@angular/material/button';
import { Hour } from '../../../Model/Hour';
import { User } from '../../../Model/User';
import { UserService } from '../../../services/user.service';
import {
  PeriodicalHourDisablerComponentComponent,
  PeriodicalHourDisablingDO,
} from '../periodical-hour-disabler-component/periodical-hour-disabler-component.component';
import { MatDialog } from '@angular/material/dialog';
import {
  Opinion,
  UserOpinion,
  UserOpinionLookup,
} from '../../../Model/Opinion';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-view-calendar',
  standalone: true,
  imports: [
    DayComponent,
    MatCardModule,
    MatChipsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatSelectModule,
    CommonModule,
  ],
  templateUrl: './view-calendar.component.html',
  styleUrl: './view-calendar.component.css',
})
export class ViewCalendarComponent implements OnInit {

  eventId?: number;

  eventowner?: User;

  calendar?: Calendar;

  participantNumber?: number;

  weekDays = [
    'Monday',
    'Tuesday',
    'Wednesday',
    'Thursday',
    'Friday',
    'Saturday',
    'Sunday',
  ];

  weeks: (Day | null)[][] = [];

  editMode = false;

  opinionMode = OpinionMode.DEFAULT;

  changedDays: Set<Day> = new Set();

  changedHours: Set<Hour> = new Set();

  changedOpinions: Set<Opinion> = new Set();

  dialog = inject(MatDialog);

  opinonForm = new FormGroup({
    opinion: new FormControl('GOOD'),
  });

  OpinionMode = OpinionMode;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private eventservice: EventService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.eventId = this.activatedRoute.snapshot.queryParams['id'];
    this.eventservice.getEvent(this.eventId!).subscribe((response) => {
      from(response.json()).subscribe((event: EowaEvent) => {
        this.eventowner = event.owner;
        this.calendar = event.calendar;
        this.participantNumber = event.participants.length;
        let firstDay = this.calendar!.days![0];
        let firstDate = this.convertTimeStampToDate(
          firstDay.dayStartTime as unknown as number
        );
        let firstDayOfWeek = firstDate.getDay();
        console.log('First day is the', firstDayOfWeek, 'th/nd/rd');
        this.fillWeeks(firstDayOfWeek - 1, this.calendar!.days!);
      });
    });
  }

  fillWeeks(firstDayOfWeek: number, days: Day[]) {
    let dayOfWeek = firstDayOfWeek;
    let week: (Day | null)[] = [];
    this.weeks = [];
    for (let i = 0; i < firstDayOfWeek; i++) {
      week.push(null);
    }
    for (let day of days) {
      day.dayStartTime = this.convertTimeStampToDate(
        day.dayStartTime as unknown as number
      );
      if (dayOfWeek == 7) {
        this.weeks.push(week);
        week = [];
        dayOfWeek = 0;
      }
      week.push(day);
      dayOfWeek += 1;
    }
    if (week.length > 0) {
      this.weeks.push(week);
    }
    for (let i = dayOfWeek; i < 7; i++) {
      week.push(null);
    }
    console.log(this.weeks);
  }

  convertTimeStampToDate(timeStamp: number): Date {
    let date = new Date(0);
    date.setUTCSeconds(timeStamp);
    return date;
  }

  editCalendar() {
    this.editMode = true;
  }
  exitEditMode(save: boolean) {
    console.log('this.changedDays', this.changedDays);
    console.log('this.changedHours', this.changedHours);
    this.editMode = false;
    if (!save) {
      this.ngOnInit();
    } else {
      this.updateDays();
      this.updateHours();
    }
  }

  async updateOpinions() {
    console.log('updatig opinions');
    if (this.changedOpinions.size != 0) {
      for (let opinionKey of Object.keys(UserOpinion)) {
        let opinionType =
          UserOpinionLookup[opinionKey as keyof typeof UserOpinion];
        let numbers: number[] = [];
        for (let opinion of this.changedOpinions) {
          if (opinion.userOpinion == opinionType) {
            numbers.push(opinion.number!);
          }
        }
        console.log('sending', opinionType);
        await firstValueFrom(
          this.eventservice.setUserOpinion(this.eventId!, numbers, opinionType)
        );
      }
      this.changedOpinions.clear();
      this.opinionMode = OpinionMode.DEFAULT;
      console.log('reloading');
      this.ngOnInit();
    }
  }

  private updateDays() {
    if (this.changedDays.size != 0) {
      let serialNumbers: number[] = [];
      this.getDisabledDayNumbers(serialNumbers);

      this.eventservice
        .setUnavailableDays(this.eventId!, serialNumbers)
        .subscribe((response) => {
          this.ngOnInit();
          if (response.ok) {
            this.changedDays.clear();
          }
        });
    }
  }

  private updateHours() {
    if (this.changedHours.size != 0) {
      let serialNumbers: number[] = [];
      this.getDisabledHourNumbers(serialNumbers);

      this.eventservice
        .setUnavailableHours(this.eventId!, serialNumbers)
        .subscribe((response) => {
          this.ngOnInit();
          if (response.ok) {
            this.changedHours.clear();
          }
        });
    }
  }

  getDisabledDayNumbers(serialNUmbers: number[]) {
    for (let week of this.weeks) {
      for (let day of week) {
        if (day?.enabled == false) {
          serialNUmbers.push(day.serialNumber);
        }
      }
    }
  }

  getDisabledHourNumbers(serialNUmbers: number[]) {
    for (let week of this.weeks) {
      for (let day of week) {
        if (day == null) {
          continue;
        }
        for (let hour of day!.hours) {
          if (hour.enabled == false) {
            serialNUmbers.push(hour.numberInTotal);
          }
        }
      }
    }
  }

  dayChanged(day: Day) {
    if (this.changedDays.has(day)) {
      this.changedDays.delete(day);
    } else {
      this.changedDays.add(day);
    }
  }

  hourChanged(hour: Hour) {
    if (this.changedHours.has(hour)) {
      this.changedHours.delete(hour);
    } else {
      this.changedHours.add(hour);
    }
  }

  opinionsChanged(opinions: Opinion[]) {
    for (let opinion of opinions) {
      if (this.changedOpinions.has(opinion)) {
        this.changedOpinions.delete(opinion);
      } else {
        this.changedOpinions.add(opinion);
      }
    }
  }

  isUserEventOwner() {
    if (this.eventowner == null) {
      return false;
    }
    return (
      this.userService.getCurrentUser().username === this.eventowner.username
    );
  }

  disableHoursPeriodically() {
    let dialogRef = this.dialog.open(PeriodicalHourDisablerComponentComponent);

    dialogRef.afterClosed().subscribe((result: PeriodicalHourDisablingDO) => {
      this.eventservice
        .setUnavailableHoursPeriodically(
          this.eventId!,
          result.period,
          result.hourNumbers
        )
        .subscribe((response) => {
          if (response.ok) {
            this.ngOnInit();
          }
        });
    });
  }

  opinionModeChanged() {
    console.log(this.opinionMode);
    if (this.opinionMode == OpinionMode.DEFAULT) {
      this.opinionMode =
        OpinionModeLookup[
          this.opinonForm.controls['opinion']
            .value! as keyof typeof OpinionModeLookup
        ];
    } else {
      this.opinionMode = OpinionMode.DEFAULT;
    }
  }
}

export enum OpinionMode {
  DEFAULT = 'DEFAULT',
  GOOD = 'GOOD',
  BAD = 'BAD',
  TOLERABLE = 'TOLERABLE',
}

const OpinionModeLookup = {
  DEFAULT: OpinionMode.DEFAULT,
  GOOD: OpinionMode.GOOD,
  BAD: OpinionMode.BAD,
  TOLERABLE: OpinionMode.TOLERABLE,
};
