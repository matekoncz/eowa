package com.example.eowa.service;

import com.example.eowa.exceptions.CalendarExceptions.CalendarException;
import com.example.eowa.model.Calendar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.Month;

@ActiveProfiles("test")
@SpringBootTest
public class CalendarServiceTest {

    @Autowired
    CalendarService calendarService;

    @Test
    public void shouldCreateCalendar() throws CalendarException {
        Calendar calendar = calendarService.createCalendar("CET", LocalDateTime.now(),LocalDateTime.now().plusWeeks(2));

        Assertions.assertNotNull(calendar.getStartTime());
        Assertions.assertNotNull(calendar.getEndTime());

        Assertions.assertEquals(14,calendar.getDays().size());
    }

    @Test
    public void shouldHandleClockChanges() throws CalendarException {
        LocalDateTime startTime = LocalDateTime.of(2025, Month.OCTOBER, 25, 0, 0);
        Calendar calendar = calendarService.createCalendar("CET", startTime,startTime.plusDays(4));

        Assertions.assertNotNull(calendar.getStartTime());
        Assertions.assertNotNull(calendar.getEndTime());

        Assertions.assertEquals(4,calendar.getDays().size());
        Assertions.assertEquals(25,calendar.getDays().get(1).getHours().size());
    }
}
