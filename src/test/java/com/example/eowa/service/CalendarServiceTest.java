package com.example.eowa.service;

import com.example.eowa.EowaIntegrationTest;
import com.example.eowa.exceptions.CalendarExceptions.CalendarException;
import com.example.eowa.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarServiceTest extends EowaIntegrationTest {

    @Test
    public void shouldCreateCalendar() throws CalendarException {
        Calendar calendar = calendarService.createCalendar("CET", LocalDateTime.now(),LocalDateTime.now().plusWeeks(2));

        Assertions.assertNotNull(calendar.getStartTime());
        Assertions.assertNotNull(calendar.getEndTime());

        Assertions.assertEquals(15,calendar.getDays().size());
    }

    @Test
    public void shouldHandleClockChanges() throws CalendarException {
        LocalDateTime startTime = LocalDateTime.of(2025, Month.OCTOBER, 25, 0, 0);
        Calendar calendar = calendarService.createCalendar("CET", startTime,startTime.plusDays(4));

        Assertions.assertNotNull(calendar.getStartTime());
        Assertions.assertNotNull(calendar.getEndTime());

        Assertions.assertEquals(5,calendar.getDays().size());
        Assertions.assertEquals(25,calendar.getDays().get(1).getHours().size());
    }

    @Test
    public void shouldSelectStartAndEndTime() throws CalendarException {
        Calendar calendar = calendarService.createCalendar("CET", LocalDateTime.now(),LocalDateTime.now().plusWeeks(2));

        calendarService.setTimeInterval(calendar,2,4);

        Calendar updatedCalendar = calendarService.getCalendarById(calendar.getId());

        Assertions.assertEquals(updatedCalendar.getStarthour(),2);
        Assertions.assertEquals(updatedCalendar.getEndhour(),4);

    }

    @Test
    public void shouldNotSelectInvalidStartAndEndTime() throws CalendarException {
        Calendar calendar = calendarService.createCalendar("CET", LocalDateTime.now(),LocalDateTime.now().plusWeeks(2));

        calendarService.setTimeInterval(calendar,4,2);

        Calendar updatedCalendar = calendarService.getCalendarById(calendar.getId());

        Assertions.assertEquals(updatedCalendar.getStarthour(),-1);
        Assertions.assertEquals(updatedCalendar.getStarthour(),-1);
    }

    @Test
    public void shouldGetBestIntervals() throws Exception {
        User user1 = userService.saveUser(new User("felhasznalo1","jelszo123","email1@gmail.com"));
        User user2 = userService.saveUser(new User("felhasznalo2","jelszo123","email2@gmail.com"));

        Event event = eventService.saveEvent(new Event(user1,"esemeny", new HashSet<User>(Set.of(user2)),"leiras"));

        LocalDateTime startTime = LocalDateTime.of(2025, Month.OCTOBER, 25, 0, 0);
        Calendar calendar = calendarService.createCalendar("CET", startTime,startTime.plusDays(4));

        event.setCalendar(calendar);

        calendar = calendarService.getCalendarById(calendar.getId());

        calendarService.setUserOpinion(calendar,user1,Set.of(0,1,2,3,4,5,6,7,8,9,10,11), Opinion.UserOpinion.GOOD);
        calendarService.setUserOpinion(calendar,user2,Set.of(6,7,8,9,10,11,12,13,14,15,16,17), Opinion.UserOpinion.GOOD);

        //calendar = calendarService.getCalendarById(calendar.getId());
        List<MomentDetails> bestTimeIntervals = calendarService.getBestTimeIntervals(calendar,2,3, Set.of(Opinion.UserOpinion.GOOD));

        bestTimeIntervals.sort(Comparator.comparingInt(MomentDetails::getLength).reversed());

        Assertions.assertEquals(bestTimeIntervals.size(),4);
        Assertions.assertEquals(bestTimeIntervals.getFirst().getLength(),6);
    }
}
