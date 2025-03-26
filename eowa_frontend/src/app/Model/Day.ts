import { Hour } from './Hour';

export interface Day {
  id: number;
  dayStartTime: Date;
  serialNumber: number;
  enabled: Boolean;
  hours: Hour[];
}
