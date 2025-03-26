import { User } from './User';

export interface Option {
  id?: number;
  value: string;
  selected: boolean;
  voters: User[];
}
