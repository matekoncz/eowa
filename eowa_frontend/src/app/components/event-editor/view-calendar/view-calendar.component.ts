import { Component, inject, OnInit } from '@angular/core';
import { Calendar } from '../../../Model/Calendar';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom, from, Subject } from 'rxjs';
import { EowaEvent } from '../../../Model/EowaEvent';
import { EventService } from '../../../services/event.service';
import { Day } from '../../../Model/Day';
import { DayComponent } from '../day/day.component';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatButtonModule } from '@angular/material/button';
import { Hour } from '../../../Model/Hour';
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
import { StartAndEndHourPipePipe } from '../../../pipes/start-and-end-hour-pipe.pipe';
import { MatSnackBar } from '@angular/material/snack-bar';
import { GetBestTimeIntervalsDialogComponent } from '../get-best-time-intervals-dialog/get-best-time-intervals-dialog.component';
import { TimeIntervalDetails } from '../../../Model/TimeIntervalDetails';
import { ProcessIndicatorComponent } from '../../process-indicator/process-indicator.component';
import { MatIconModule } from '@angular/material/icon';

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
    StartAndEndHourPipePipe,
    ProcessIndicatorComponent,
    MatIconModule
],
  templateUrl: './view-calendar.component.html',
  styleUrl: './view-calendar.component.css',
})
export class ViewCalendarComponent implements OnInit {

  eventId?: number;

  event?: EowaEvent;

  calendar?: Calendar;

  participantNumber?: number;

  weeks: (Day | null)[][] = [];

  editMode = EditMode.DEFAULT;

  opinionMode = OpinionMode.DEFAULT;

  changedDays: Set<Day> = new Set();

  changedHours: Set<Hour> = new Set();

  newStartAndEnd: number[] = [];

  bestIntervals: TimeIntervalDetails[] = [];

  changedOpinions: Set<Opinion> = new Set();

  dialog = inject(MatDialog);

  snackbar = inject(MatSnackBar);

  opinonForm = new FormGroup({
    opinion: new FormControl('GOOD'),
  });

  firstDayOfWeek = 0;

  loading = true;

  OpinionMode = OpinionMode;

  EditMode = EditMode;

  $showTimeIntervals = new Subject<TimeIntervalDetails>();

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
        this.loading = false;
        this.event = event;
        this.calendar = event.calendar;
        let firstDay = this.calendar!.days![0];
        let firstDate = this.convertTimeStampToDate(
          firstDay.dayStartTime as unknown as number
        );
        this.firstDayOfWeek = firstDate.getDay();
        console.log('First day is the', this.firstDayOfWeek, 'th/nd/rd');
        this.fillWeeks(this.firstDayOfWeek - 1, this.calendar!.days!);
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
    this.editMode = EditMode.EDIT;
  }
  exitEditMode(save: boolean) {
    console.log('this.changedDays', this.changedDays);
    console.log('this.changedHours', this.changedHours);
    this.editMode = EditMode.DEFAULT;
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
            serialNUmbers.push(hour.numberInCalendar);
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

  showTimeInterval(timeInterval: TimeIntervalDetails) {
    this.$showTimeIntervals.next(timeInterval);
  }

  isUserEventOwner() {
    if (this.event?.owner == null) {
      return false;
    }
    return (
      this.userService.getCurrentUser()!.username === this.event.owner.username
    );
  }

  disableHoursPeriodically() {
    let dialogRef = this.dialog.open(PeriodicalHourDisablerComponentComponent);

    dialogRef.afterClosed().subscribe((result: PeriodicalHourDisablingDO) => {
      console.log("result", result);
      console.log("this.firstDayOfWeek", this.firstDayOfWeek);
      this.eventservice
        .setUnavailableHoursPeriodically(
          this.eventId!,
          result.period,
          (result.day-this.firstDayOfWeek+1) % 7,
          result.hourNumbers,
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
    if (this.editMode == EditMode.DEFAULT) {
      this.editMode = EditMode.OPINION;
      this.opinionMode =
        OpinionModeLookup[
          this.opinonForm.controls['opinion']
            .value! as keyof typeof OpinionModeLookup
        ];
    } else {
      this.opinionMode = OpinionMode.DEFAULT;
      this.editMode = EditMode.DEFAULT;
    }
  }

  startAndEnd() {
    if (this.editMode == EditMode.DEFAULT) {
      this.editMode = EditMode.START_AND_END;
      this.newStartAndEnd = [];
      this.snackbar.open('select the start hour', 'close');
    } else {
      this.editMode = EditMode.DEFAULT;
    }
  }

  selectHour(hour: Hour) {
    console.log('hour selected', hour);
    console.log('newStartAndEnd', this.newStartAndEnd);
    this.newStartAndEnd.push(hour.numberInCalendar);
    this.newStartAndEnd.sort();

    switch (this.newStartAndEnd.length) {
      case 1:
        this.snackbar.open('select the end hour', 'close');
        break;
      case 2:
        this.eventservice
          .setStartAndEndTime(
            this.eventId!,
            this.newStartAndEnd[0],
            this.newStartAndEnd[1]
          )
          .subscribe((response) => {
            if (response.ok) {
              this.ngOnInit();
              this.snackbar.open(
                'successfully updated start and end time',
                'close'
              );
            }
          });
        this.editMode = EditMode.DEFAULT;
        break;
    }
  }

  openGetBestTimeIntervalsDialog() {
    let dialogref = this.dialog.open(GetBestTimeIntervalsDialogComponent, {
      data: this.event,
    });

    dialogref.afterClosed().subscribe((result) => {
      if (result != null) {
        this.getBestTimeIntervals(
          result.participants,
          result.length,
          result.allowedOpinions,
          result.popularityMode
        );
      }
    });
  }

  getBestTimeIntervals(
    participants: number,
    length: number,
    allowedOpinions: UserOpinion[],
    popularityMode: boolean
  ) {
    this.eventservice
      .getBestTimeIntervals(
        this.eventId!,
        participants,
        length,
        allowedOpinions,
        popularityMode
      )
      .subscribe((response) => {
        let status = response.status;
        if (status == 200) {
          from(response.json()).subscribe(
            (intervals: TimeIntervalDetails[]) => {
              console.log(intervals);
              this.bestIntervals = intervals;
              this.editMode = EditMode.SHOW_BEST;
            }
          );
        }
      });
  }

  exitSHowBestMode() {
    this.editMode = EditMode.DEFAULT;
    let noInterval : TimeIntervalDetails = {
      hourSerial: 0,
      length: 0,
      participantNumber: 0
    }
    this.$showTimeIntervals.next(noInterval);
  }

  showBestIntervals() {}
}

export enum EditMode {
  DEFAULT = 'DEFAULT',
  EDIT = 'EDIT',
  OPINION = 'OPINION',
  START_AND_END = 'START_AND_END',
  SHOW_BEST = 'SHOW_BEST',
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
