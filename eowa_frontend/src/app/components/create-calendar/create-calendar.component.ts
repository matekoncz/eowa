import { Component, inject } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { EventService } from '../../services/event.service';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { from } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Calendar, ZoneId } from '../../Model/Calendar';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {
  MatOptionModule,
  provideNativeDateAdapter,
} from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { PopUpInfoComponent } from '../popup-info/popup-info.component';

@Component({
  selector: 'app-create-calendar',
  standalone: true,
  providers: [provideNativeDateAdapter()],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatDatepickerModule,
    MatOptionModule,
  ],
  templateUrl: './create-calendar.component.html',
  styleUrl: './create-calendar.component.css',
})
export class CreateCalendarComponent {

  createCalendarForm: FormGroup = new FormGroup({

    timeZone: new FormControl('', [Validators.required]),

    startTime: new FormControl(new Date(), [Validators.required]),

    endTime: new FormControl(new Date(), [Validators.required]),
  });

  private eventId: number;

  readonly dialog = inject(MatDialog);

  ZoneId = ZoneId;
  Object = Object;

  constructor(
    private eventservice: EventService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {
    this.eventId = this.activatedRoute.snapshot.queryParams['id'];
  }

  getZoneIdKeys() {
    let list = [];
    for (let key in ZoneId) {
      list.push(key);
    }
    return list;
  }

  addCalendar() {
    if (!this.createCalendarForm.valid) {
      return;
    }

    let calendar: Calendar = {
      timeZone: this.createCalendarForm.value['timeZone']!,
      startTime: this.createCalendarForm.value['startTime']!,
      endTime: this.createCalendarForm.value['endTime']!,
      starthour: -1,
      endhour: -1,
    };

    this.eventservice
      .addCalendar(this.eventId, calendar)
      .subscribe((response) => {
        let status = response.status;
        if (status == 200) {
          const dialogRef = this.dialog.open(PopUpInfoComponent, {
            data: { title: 'Success', content: 'Calendar added successfully' },
          });
          this.router.navigate(['my-events']);
        } else {
          from(response.text()).subscribe((message) => {
            const dialogRef = this.dialog.open(PopUpInfoComponent, {
              data: { title: 'Error', content: message },
            });
            this.router.navigate(['my-events']);
          });
        }
      });
  }

  cancel() {
    this.router.navigate(['my-events']);
  }
}
