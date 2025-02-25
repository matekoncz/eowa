import { Calendar } from './Calendar';
import { User } from './User';

export interface EowaEvent {
  id?: number;
  calendar?: Calendar;
  owner: User;
  eventName: String;
  description: String;
  participants: User[];
  invitationCode?: String;
}
