import { Injectable } from '@angular/core';
import { ApiService, Controller } from './api.service';
import { EowaEvent } from '../Model/EowaEvent';
import { User } from '../Model/User';
import { Calendar } from '../Model/Calendar';
import { UserOpinion } from '../Model/Opinion';
import { SelectionField } from '../Model/SelectionField';
import { Option } from '../Model/Option';

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
    offset: number,
    serialNumbers: number[]
  ) {
    return this.apiservice.put(
      Controller.EVENT,
      '/' + id + '/set-unavailable-hours-periodically?period=' + period + '&offset=' + offset,
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

  addFields(id: number, fields: SelectionField[]) {
    return this.apiservice.put(
      Controller.EVENT,
      '/' + id + '/add-fields',
      fields
    );
  }

  removeFields(id: number, ids: number[]) {
    return this.apiservice.delete(
      Controller.EVENT,
      '/' + id + '/remove-fields',
      ids
    );
  }

  addOptions(id: number, fieldid: number, options: Option[]) {
    return this.apiservice.put(
      Controller.EVENT,
      '/' + id + '/fields/' + fieldid,
      options
    );
  }

  removeOptions(id: number, fieldid: number, ids: number[]) {
    return this.apiservice.delete(
      Controller.EVENT,
      '/' + id + '/fields/' + fieldid,
      ids
    );
  }

  voteForOption(id: number, fieldid: number, optionid: number) {
    return this.apiservice.put(
      Controller.EVENT,
      '/' + id + '/fields/' + fieldid + '/vote/' + optionid
    );
  }

  removeOptionVote(id: number, fieldid: number, optionid: number) {
    return this.apiservice.delete(
      Controller.EVENT,
      '/' + id + '/fields/' + fieldid + '/remove-vote/' + optionid
    );
  }

  selectOption(id: number, fieldid: number, optionid: number) {
    return this.apiservice.put(
      Controller.EVENT,
      '/' + id + '/fields/' + fieldid + '/select/' + optionid
    );
  }

  setStartAndEndTime(id: number, start: number, end: number) {
    return this.apiservice.put(
      Controller.EVENT,
      '/' + id + '/set-start-and-end?start=' + start + '&end=' + end
    );
  }

  resetStartAndEndTime(id: number) {
    return this.apiservice.delete(
      Controller.EVENT,
      '/' + id + '/set-start-and-end'
    );
  }

  finalizeEvent(id: number) {
    return this.apiservice.put(Controller.EVENT, '/' + id + '/finalize');
  }

  unFinalizeEvent(id: number) {
    return this.apiservice.delete(Controller.EVENT, '/' + id + '/finalize');
  }

  getBestTimeIntervals(
    id: number,
    participants: number,
    length: number,
    allowedOptionions: UserOpinion[],
    popularityMode: boolean
  ) {
    return this.apiservice.put(
      Controller.EVENT,
      '/' + id + '/get-best-time-intervals?participants=' + participants + '&length=' + length + '&popularity=' + popularityMode,
      allowedOptionions
    );
  }

  createBlueprint(id: number, name: string) {
    return this.apiservice.post(
      Controller.EVENT,
      '/' + id + '/create-blueprint?name=' + name
    );
  }

  addFieldsFromBlueprint(id: number, blueprintid: number) {
    return this.apiservice.put(
      Controller.EVENT,
      '/' + id + '/add-from-blueprint/' + blueprintid
    );
  }

  getBluePrintsForCurrentUser() {
    return this.apiservice.get(Controller.EVENT, '/my-blueprints');
  }
}
