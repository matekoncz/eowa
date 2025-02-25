import { Injectable } from '@angular/core';
import { ApiService, Controller } from './api.service';
import { EowaEvent } from '../Model/EowaEvent';
import { User } from '../Model/User';
import { Calendar } from '../Model/Calendar';
import { UserOpinion } from '../Model/Opinion';

@Injectable({
  providedIn: 'root',
})
export class EventService {
  constructor(private apiservice: ApiService) {}

  createEvent(event: EowaEvent) {
    return this.apiservice.post(Controller.EVENT, '/create', event);
  }

  getEvent(id: number) {
    return this.apiservice.get(Controller.EVENT, '/' + id);
  }

  listMyEvents() {
    return this.apiservice.get(Controller.EVENT, '/currentuser-events');
  }

  deleteEventById(id: number) {
    return this.apiservice.delete(Controller.EVENT, '/' + id);
  }

  addParticipants(id: number, users: User[]) {
    return this.apiservice.put(
      Controller.EVENT,
      '/' + id + '/add-users',
      users
    );
  }

  addCalendar(id: number, calendar: Calendar) {
    let path =
      '/add-calendar?start=' +
      calendar.startTime.toISOString().replace('Z', '') +
      '&end=' +
      calendar.endTime.toISOString().replace('Z', '') +
      '&zone=' +
      calendar.timeZone;
    return this.apiservice.put(Controller.EVENT, '/' + id + path, calendar);
  }

  setUnavailableDays(id: number, serialNumbers: number[]) {
    return this.apiservice.put(
      Controller.EVENT,
      '/' + id + '/set-unavailable-days',
      serialNumbers
    );
  }

  setUnavailableHours(id: number, serialNumbers: number[]) {
    return this.apiservice.put(
      Controller.EVENT,
      '/' + id + '/set-unavailable-hours',
      serialNumbers
    );
  }

  setUnavailableHoursPeriodically(
    id: number,
    period: number,
    serialNumbers: number[]
  ) {
    return this.apiservice.put(
      Controller.EVENT,
      '/' + id + '/set-unavailable-hours-periodically?period=' + period,
      serialNumbers
    );
  }

  setUserOpinion(id: number, hourSerials: number[], opinion: UserOpinion) {
    return this.apiservice.put(
      Controller.EVENT,
      '/' + id + '/set-user-opinion?opinion=' + UserOpinion[opinion],
      hourSerials
    );
  }

  removeUserOpinion(id: number, hourSerials: number[]) {
    return this.apiservice.put(
      Controller.EVENT,
      '/' + id + '/remove-user-opinion',
      hourSerials
    );
  }

  joinEvent(invitationCode: String) {
    return this.apiservice.put(
      Controller.EVENT,
      '/join-event?invitation=' + invitationCode,
      ''
    );
  }
}
