import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { HttpStatusCode } from '@angular/common/http';
import { from } from 'rxjs';
import { Router } from '@angular/router';
import { EventService } from '../../services/event.service';
import { UserService } from '../../services/user.service';
import { EowaEvent } from '../../Model/EowaEvent';
import { MatDialog } from '@angular/material/dialog';
import { PopUpInfoComponent } from '../popup-info/popup-info.component';

@Component({
  selector: 'app-create-event',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './create-event.component.html',
  styleUrl: './create-event.component.css',
})
export class CreateEventComponent {
  createEventForm: FormGroup = new FormGroup({
    name: new FormControl('', [Validators.required, Validators.minLength(8)]),

    description: new FormControl('', [
      Validators.required,
      Validators.minLength(8),
    ]),
  });

  dialog = inject(MatDialog);

  constructor(
    private eventservice: EventService,
    private userservice: UserService,
    private router: Router
  ) {}

  createEvent() {
    if (!this.createEventForm.valid) {
      return;
    }
    
    this.eventservice
      .createEvent({
        eventName: this.createEventForm.value['name']!,
        description: this.createEventForm.value['description']!,
        owner: this.userservice.getCurrentUser(),
        participants: [],
        selectionFields: [],
        finalized: false
      })
      .subscribe((response) => {
        let status = response.status;
        if (status == HttpStatusCode.Ok) {
          const dialogRef = this.dialog.open(PopUpInfoComponent, {
            data: { title: 'Success', content: 'Event created successfully' },
          });
          from(response.json()).subscribe((event: EowaEvent) => {
            this.router.navigate(['create-calendar'], {
              queryParams: { id: event.id },
            });
          });
        } else {
          from(response.text()).subscribe((message) => {
            const dialogRef = this.dialog.open(PopUpInfoComponent, {
              data: { title: 'Error', content: message },
            });
          });
        }
      });
  }
}
