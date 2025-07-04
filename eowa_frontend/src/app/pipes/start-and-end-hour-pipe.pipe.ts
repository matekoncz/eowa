import { Pipe, PipeTransform } from '@angular/core';
import { Calendar } from '../Model/Calendar';
import { NumberToHourPipe } from './number-to-hour.pipe';

@Pipe({
  name: 'startAndEndHourPipe',
  standalone: true,
})
export class StartAndEndHourPipePipe implements PipeTransform {
  transform(number: number, calendar: Calendar, ...args: unknown[]): string {
    if (number == -1) {
      return 'not yet set';
    }
    let day = calendar.days?.find(
      (day) => day.hours.find((hour) => hour.numberInCalendar == number) != null
    );
    let hour = day?.hours.find((hour) => hour.numberInCalendar == number);

    if (!hour?.number) {
      return 'start of the event';
    }

    return (
      day?.dayStartTime.toDateString() +
      ' ' +
      new NumberToHourPipe().transform(hour!.number) +
      ':00'
    );
  }
}
