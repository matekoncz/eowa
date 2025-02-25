import { Component, OnInit } from '@angular/core';
import { EventService } from '../../services/event.service';
import { Router } from '@angular/router';
import { EowaEvent } from '../../Model/EowaEvent';
import { from } from 'rxjs';
import { MatListModule } from '@angular/material/list';
import { EventShortinfoComponent } from '../event-shortinfo/event-shortinfo.component';
import { UserService } from '../../services/user.service';
import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-my-events',
  standalone: true,
  imports: [MatListModule, EventShortinfoComponent, MatDividerModule],
  templateUrl: './my-events.component.html',
  styleUrl: './my-events.component.css',
})
export class MyEventsComponent implements OnInit {
  
  events: EowaEvent[] = [];

  constructor(
    private eventservice: EventService,
    private userservice: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.eventservice.listMyEvents().subscribe((response) => {
      from(response.json()).subscribe((events: EowaEvent[]) => {
        this.events = events;
      });
    });
  }

  getRole(event: EowaEvent): string {
    if (event.owner.username == this.userservice.getCurrentUser().username) {
      return 'organizer';
    } else {
      return 'participant';
    }
  }
}
