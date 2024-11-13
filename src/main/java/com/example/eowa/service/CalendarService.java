package com.example.eowa.service;

import com.example.eowa.exceptions.CalendarExceptions.CalendarException;
import com.example.eowa.exceptions.CalendarExceptions.TimeTravelException;
import com.example.eowa.exceptions.CalendarExceptions.WrongIntervalException;
import com.example.eowa.model.Calendar;
import com.example.eowa.model.Day;
import com.example.eowa.model.Hour;
import com.example.eowa.repository.CalendarRepository;
import com.example.eowa.repository.DayRepository;
import com.example.eowa.repository.HourRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final DayRepository dayRepository;
    private final HourRepository hourRepository;

    public CalendarService(CalendarRepository calendarRepository, DayRepository dayRepository, HourRepository hourRepository) {
        this.calendarRepository = calendarRepository;
        this.dayRepository = dayRepository;
        this.hourRepository = hourRepository;
    }

    public void deleteAllCalendarData(){
        this.hourRepository.deleteAll();
        this.dayRepository.deleteAll();
        this.calendarRepository.deleteAll();
    }

    public Calendar createCalendar(String zoneIdString, LocalDateTime startTime, LocalDateTime endTime) throws CalendarException {
        ZoneId zoneId = ZoneId.of(zoneIdString);
        ZonedDateTime zonedStart = ZonedDateTime.of(startTime.withHour(0),zoneId);
        ZonedDateTime zonedEnd = ZonedDateTime.of(endTime.plusDays(1).withHour(0),zoneId);

        if(zonedStart.isBefore(ZonedDateTime.now(ZoneId.of(zoneIdString)).withHour(0).withMinute(0).withSecond(0).withNano(0))
                || startTime.plusDays(90).isBefore(endTime)){
            throw new WrongIntervalException();
        }
        if(endTime.withHour(0).isBefore(startTime.withHour(0))){
            throw new TimeTravelException();
        }

        Calendar calendar = new Calendar(zoneId,zonedStart,zonedEnd);

        calendar.setDays(generateDays(calendar));
        calendarRepository.save(calendar);

        return calendar;
    }

    private List<Day> generateDays(Calendar calendar) {
        List<Day> days = new ArrayList<>();
        List<Hour> dayHours = new ArrayList<>();
        ZonedDateTime currentDay = calendar.getStartTime();
        int serial = 0;
        for(ZonedDateTime currentHour = calendar.getStartTime().withHour(0);currentHour.isBefore(calendar.getEndTime()); currentHour= currentHour.plusHours(1)){
            if(currentHour.getDayOfYear()!=currentDay.getDayOfYear()){
                Day day = new Day(currentDay, serial, true, false, dayHours);
                days.add(day);
                dayRepository.save(day);
                currentDay=currentHour;
                dayHours = new ArrayList<>();
                serial++;
            }
            Hour currentHourInHourFormat = new Hour(currentHour.getHour(), true);
            dayHours.add(currentHourInHourFormat);
            hourRepository.save(currentHourInHourFormat);
        }
        return days;
    }
}
