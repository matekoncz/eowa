import { Option } from './Option';

export interface SelectionField {
  id?: number;
  title: string;
  options: Option[];
  selectedByPoll: boolean;
  openForModification: boolean;
}
