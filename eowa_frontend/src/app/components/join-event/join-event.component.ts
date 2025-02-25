import { Component, inject } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { EventService } from '../../services/event.service';
import { MatDialog } from '@angular/material/dialog';
import { PopUpInfoComponent } from '../popup-info/popup-info.component';
import { from } from 'rxjs';

@Component({
  selector: 'app-join-event',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './join-event.component.html',
  styleUrl: './join-event.component.css',
})
export class JoinEventComponent {
  
  joinform: FormGroup = new FormGroup({
    invitationCode: new FormControl('', [Validators.required]),
  });

  dialog = inject(MatDialog);

  constructor(private eventservice: EventService, private router: Router) {}

  join() {
    if (!this.joinform.valid) {
      return;
    }
    this.eventservice
      .joinEvent(this.joinform.value['invitationCode'])
      .subscribe((response) => {
        if (response.status == 200) {
          const dialogRef = this.dialog.open(PopUpInfoComponent, {
            data: {
              title: 'Success',
              content: 'You are now a participant of the event',
            },
          });
          this.router.navigate(['/my-events']);
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
