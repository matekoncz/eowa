package com.example.eowa.service;

import com.example.eowa.exceptions.CalendarExceptions.CalendarException;
import com.example.eowa.exceptions.userExceptions.UserException;
import com.example.eowa.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ActiveProfiles("test")
@SpringBootTest
public class EventServiceTests {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private CalendarService calendarService;

    @BeforeEach
    public void beforeTests(){
        sessionService.deleteAllSession();
        eventService.deleteAllEvent();
        userService.deleteAllUsers();
    }

    @Test
    public void shouldSaveEvent() throws UserException {
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);
        Event event = new Event(savedUser,"kertiparti", new HashSet<>(),"");
        Event savedEvent = eventService.saveEvent(event);
        Assertions.assertNotNull(savedEvent);
    }

    @Test
    public void shouldGetEventById() throws UserException {
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);
        Event event = new Event(savedUser,"kertiparti", new HashSet<>(),"");
        Long storedId = eventService.saveEvent(event).getId();

        Assertions.assertEquals("kertiparti",eventService.getEventById(storedId).getEventName());
    }

    @Test
    public void shouldSaveParticipantsOnEvent() throws UserException {
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);

        User p1 = new User("feri","asznalo1","feri@gmail.com");
        User savedp1 = userService.saveUser(p1);

        User p2 = new User("dani","asznalo1","dani@gmail.com");
        User savedp2 = userService.saveUser(p2);

        Set<User> participants = new HashSet<>();
        participants.add(savedp1);
        participants.add(savedp2);

        Event event = new Event(savedUser,"kertiparti",participants,"");
        Event savedEvent = eventService.saveEvent(event);

        Assertions.assertEquals(savedEvent.getParticipants().size(),3);
    }

    @Test
    public void shouldSetEventCalendar() throws UserException, CalendarException {
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);
        Event event = new Event(savedUser,"kertiparti", new HashSet<>(),"");
        Event savedEvent = eventService.saveEvent(event);
        eventService.setEventCalendar(savedEvent.getId(),"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(3));
        Calendar savedCalendar = eventService.getEventById(savedEvent.getId()).getCalendar();
        Assertions.assertNotNull(savedCalendar);
    }

    @Test
    public void shouldSetEventCalendarDays() throws UserException, CalendarException {
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);
        Event event = new Event(savedUser,"kertiparti", new HashSet<>(),"");
        Event savedEvent = eventService.saveEvent(event);
        eventService.setEventCalendar(savedEvent.getId(),"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(3));
        Calendar savedCalendar = eventService.getEventById(savedEvent.getId()).getCalendar();
        Assertions.assertNotNull(savedCalendar);
        Assertions.assertEquals(savedCalendar.getDays().size(),3);
    }

    @Test
    public void shouldSetUnavailableDays() throws CalendarException, UserException {
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);
        Event event = new Event(savedUser,"kertiparti", new HashSet<>(),"");
        Event savedEvent = eventService.saveEvent(event);

        eventService.setEventCalendar(savedEvent.getId(),"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(3));

        eventService.setUnavailableDays(savedEvent.getId(),Set.of(0,1));

        Calendar savedCalendar = eventService.getEventById(savedEvent.getId()).getCalendar();
        Assertions.assertFalse(savedCalendar.getDays().get(0).isEnabled());
        Assertions.assertFalse(savedCalendar.getDays().get(1).isEnabled());
        Assertions.assertTrue(savedCalendar.getDays().get(2).isEnabled());
    }

    @Test
    public void shouldSetUnavailableHours() throws CalendarException, UserException {
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);
        Event event = new Event(savedUser,"kertiparti", new HashSet<>(),"");
        Event savedEvent = eventService.saveEvent(event);

        eventService.setEventCalendar(savedEvent.getId(),"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(2));

        eventService.setUnavailableHours(savedEvent.getId(),Set.of(0,1,24));

        Calendar savedCalendar = eventService.getEventById(savedEvent.getId()).getCalendar();
        Day firstDay = savedCalendar.getDays().getFirst();
        Day secondDay = savedCalendar.getDays().get(1);
        Assertions.assertFalse(firstDay.getHours().get(0).isEnabled());
        Assertions.assertFalse(firstDay.getHours().get(1).isEnabled());
        Assertions.assertTrue(firstDay.getHours().get(2).isEnabled());
        Assertions.assertFalse(secondDay.getHours().get(0).isEnabled());
        Assertions.assertTrue(secondDay.getHours().get(2).isEnabled());
    }

    @Test
    public void shouldSetUnavailableHoursDaily() throws UserException, CalendarException {
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);
        Event event = new Event(savedUser,"kertiparti", new HashSet<>(),"");
        Event savedEvent = eventService.saveEvent(event);

        eventService.setEventCalendar(savedEvent.getId(),"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(2));

        eventService.setUnavailableHoursDaily(savedEvent.getId(),Set.of(0,1,2,3,21,22,23));

        Calendar savedCalendar = eventService.getEventById(savedEvent.getId()).getCalendar();
        Day firstDay = savedCalendar.getDays().getFirst();
        Day secondDay = savedCalendar.getDays().get(1);
        Assertions.assertFalse(firstDay.getHours().get(0).isEnabled());
        Assertions.assertFalse(firstDay.getHours().get(1).isEnabled());
        Assertions.assertTrue(firstDay.getHours().get(4).isEnabled());
        Assertions.assertFalse(firstDay.getHours().get(21).isEnabled());

        Assertions.assertFalse(secondDay.getHours().get(0).isEnabled());
        Assertions.assertFalse(secondDay.getHours().get(1).isEnabled());
        Assertions.assertTrue(secondDay.getHours().get(4).isEnabled());
        Assertions.assertFalse(secondDay.getHours().get(21).isEnabled());
    }

    @Test
    public void shouldSetUnavailableHoursWeekly() throws UserException, CalendarException {
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);
        Event event = new Event(savedUser,"kertiparti", new HashSet<>(),"");
        Event savedEvent = eventService.saveEvent(event);

        eventService.setEventCalendar(savedEvent.getId(),"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(8));

        eventService.setUnavailableHoursWeekly(savedEvent.getId(),Set.of(0,1,2,3,21,22,23));

        Calendar savedCalendar = eventService.getEventById(savedEvent.getId()).getCalendar();
        Day firstDay = savedCalendar.getDays().getFirst();
        Day secondDay = savedCalendar.getDays().get(1);
        Day eighthDay = savedCalendar.getDays().get(7);

        Assertions.assertFalse(firstDay.getHours().get(0).isEnabled());
        Assertions.assertFalse(firstDay.getHours().get(1).isEnabled());
        Assertions.assertTrue(firstDay.getHours().get(4).isEnabled());
        Assertions.assertFalse(firstDay.getHours().get(21).isEnabled());

        Assertions.assertTrue(secondDay.getHours().get(4).isEnabled());

        Assertions.assertFalse(eighthDay.getHours().get(0).isEnabled());
        Assertions.assertFalse(eighthDay.getHours().get(1).isEnabled());
        Assertions.assertTrue(eighthDay.getHours().get(4).isEnabled());
        Assertions.assertFalse(eighthDay.getHours().get(21).isEnabled());
    }

    @Test
    void shouldSetUserOpinion() throws UserException, CalendarException {
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);
        Event event = new Event(savedUser,"kertiparti", new HashSet<>(),"");
        Event savedEvent = eventService.saveEvent(event);

        eventService.setEventCalendar(savedEvent.getId(),"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(2));

       List<Day> days = eventService.getEventById(event.getId()).getCalendar().getDays();
       Hour firstHour = days.getFirst().getHours().getFirst();
       Hour lastHour = days.getLast().getHours().getLast();

       eventService.setUserOpinion(event.getId(), Set.of(firstHour.getNumberInTotal(), lastHour.getNumberInTotal()),user, Opinion.UserOpinion.TOLERABLE);
       eventService.setUserOpinion(event.getId(), Set.of(firstHour.getNumberInTotal()),user, Opinion.UserOpinion.BAD);

        days = eventService.getEventById(event.getId()).getCalendar().getDays();
        firstHour = days.getFirst().getHours().getFirst();
        lastHour = days.getLast().getHours().getLast();

        Hour unchangedHour = days.getFirst().getHours().get(5);

        Assertions.assertEquals(firstHour.getOpinions().size(),1);
        Assertions.assertEquals(calendarService.getUserOpinion(firstHour,user).getUserOpinion(), Opinion.UserOpinion.BAD);
        Assertions.assertEquals(lastHour.getOpinions().size(),1);
        Assertions.assertEquals(calendarService.getUserOpinion(lastHour,user).getUserOpinion(), Opinion.UserOpinion.TOLERABLE);
        Assertions.assertEquals(unchangedHour.getOpinions().size(),0);
    }

    @Test
    void shouldRemoveUserOpinion() throws CalendarException, UserException {
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);
        Event event = new Event(savedUser,"kertiparti", new HashSet<>(),"");
        Event savedEvent = eventService.saveEvent(event);

        eventService.setEventCalendar(savedEvent.getId(),"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(2));

        List<Day> days = eventService.getEventById(event.getId()).getCalendar().getDays();
        Hour firstHour = days.getFirst().getHours().getFirst();
        Hour lastHour = days.getLast().getHours().getLast();

        eventService.setUserOpinion(event.getId(), Set.of(firstHour.getNumberInTotal(), lastHour.getNumberInTotal()),user, Opinion.UserOpinion.TOLERABLE);

        eventService.removeUserOpinion(event.getId(), Set.of(firstHour.getNumberInTotal()),user);

        days = eventService.getEventById(event.getId()).getCalendar().getDays();
        firstHour = days.getFirst().getHours().getFirst();
        lastHour = days.getLast().getHours().getLast();

        Hour unchangedHour = days.getFirst().getHours().get(5);

        Assertions.assertEquals(firstHour.getOpinions().size(),0);
        Assertions.assertEquals(lastHour.getOpinions().size(),1);
        Assertions.assertEquals(unchangedHour.getOpinions().size(),0);
    }
}
