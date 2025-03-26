import { Calendar } from './Calendar';
import { SelectionField } from './SelectionField';
import { User } from './User';

export interface EowaEvent {
  id?: number;
  calendar?: Calendar;
  owner: User;
  eventName: string;
  description: String;
  participants: User[];
  invitationCode?: String;
  selectionFields: SelectionField[];
  finalized: boolean;
}
