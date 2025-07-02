import { Calendar } from './Calendar';
import { SelectionField } from './SelectionField';
import { User } from './User';

export interface EowaEvent {
  id?: number;
  calendar?: Calendar;
  owner: User;
  eventName: string;
  description: string;
  participants: User[];
  invitationCode?: string;
  selectionFields: SelectionField[];
  finalized: boolean;
}
