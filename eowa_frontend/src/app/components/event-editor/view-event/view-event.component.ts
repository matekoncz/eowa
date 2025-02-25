import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EventService } from '../../../services/event.service';
import { EowaEvent } from '../../../Model/EowaEvent';
import { from } from 'rxjs';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';

@Component({
  selector: 'app-view-event',
  standalone: true,
  imports: [MatButtonModule, MatChipsModule],
  templateUrl: './view-event.component.html',
  styleUrl: './view-event.component.css',
})
export class ViewEventComponent implements OnInit {
  
  eventId?: number;

  event?: EowaEvent;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private eventservice: EventService
  ) {}

  ngOnInit(): void {
    this.eventId = this.activatedRoute.snapshot.queryParams['id'];
    this.eventservice.getEvent(this.eventId!).subscribe((response) => {
      from(response.json()).subscribe((event: EowaEvent) => {
        this.event = event;
      });
    });
  }

  viewCalendar() {
    this.router.navigate(['view-calendar'], {
      queryParams: { id: this.event?.id },
    });
  }
  addCalendar() {
    this.router.navigate(['create-calendar'], {
      queryParams: { id: this.event?.id },
    });
  }
}
