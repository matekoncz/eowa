import { NumberToHourPipe } from './number-to-hour.pipe';

describe('NumberToHourPipe', () => {
  it('create an instance', () => {
    const pipe = new NumberToHourPipe();
    expect(pipe).toBeTruthy();
  });
});
