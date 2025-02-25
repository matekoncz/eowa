import { Opinion } from './Opinion';

export interface Hour {
  id: number;
  number: number;
  numberInTotal: number;
  enabled: Boolean;
  opinions: Opinion[];
}
