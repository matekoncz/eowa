import { Opinion } from './Opinion';

export interface Hour {
  id: number;
  number: number;
  numberInCalendar: number;
  enabled: Boolean;
  opinions: Opinion[];
}
