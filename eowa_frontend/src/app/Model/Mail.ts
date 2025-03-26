import { User } from './User';

export interface Mail {
  id?: number;
  sender: User;
  reciever: User;
  title: string;
  content: string;
}
