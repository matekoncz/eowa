import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'numberToHour',
  standalone: true,
})
export class NumberToHourPipe implements PipeTransform {
  transform(value: number): string {
    let numberstring = String(value);
    if (numberstring.length == 1) {
      numberstring = '0' + numberstring;
    }
    return numberstring;
  }
}
