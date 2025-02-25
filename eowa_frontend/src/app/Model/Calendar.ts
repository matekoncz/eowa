import { Day } from './Day';

export interface Calendar {
  id?: number;
  timeZone: ZoneId;
  startTime: Date;
  endTime: Date;
  days?: Day[];
}

export enum ZoneId {
  UTC = 'UTC',
  CET = 'CET',
}
