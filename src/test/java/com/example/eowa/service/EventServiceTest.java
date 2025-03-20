package com.example.eowa.service;

import com.example.eowa.EowaIntegrationTest;
import com.example.eowa.exceptions.CalendarExceptions.CalendarException;
import com.example.eowa.exceptions.authenticationExceptions.InvalidInvitationCodeException;
import com.example.eowa.exceptions.eventExceptions.EventCannotBeFinalizedException;
import com.example.eowa.exceptions.eventExceptions.EventIsFinalizedException;
import com.example.eowa.exceptions.userExceptions.UserException;
import com.example.eowa.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class EventServiceTest extends EowaIntegrationTest {

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
        Assertions.assertEquals(savedCalendar.getDays().size(),4);
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

        eventService.setUnavailableHoursPeriodically(savedEvent.getId(),Set.of(0,1,2,3,21,22,23), CalendarService.Period.DAILY);

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

        eventService.setUnavailableHoursPeriodically(savedEvent.getId(),Set.of(0,1,2,3,21,22,23), CalendarService.Period.WEEKLY);

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

    @Test
    public void shouldAddParticipantWithInvitationCode() throws UserException, CalendarException, InvalidInvitationCodeException {
        User owner = new User("felh","asznalo1","email@gmail.com");
        User savedOwner = userService.saveUser(owner);

        User participant = new User("felh2","asznalo1","email2@gmail.com");
        User savedParticipant = userService.saveUser(participant);

        Event event = new Event(savedOwner,"kertiparti", new HashSet<>(),"");
        Event savedEvent = eventService.saveEvent(event);

        eventService.setEventCalendar(savedEvent.getId(),"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(2));

        Event updatedEvent = eventService.joinEventWithInvitationCode(savedParticipant, event.getInvitationCode());

        Assertions.assertTrue(updatedEvent.getParticipants().contains(savedParticipant));
    }

    @Test
    public void shouldAddSelectionFields() throws UserException {
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);
        Event event = new Event(savedUser,"kertiparti", new HashSet<>(),"");

        Event savedEvent = eventService.saveEvent(event);

        SelectionField selectionField = new SelectionField("mezo",false,false);
        Option option = new Option("ertek");
        selectionField.addOptions(Set.of(option));

        eventService.addFieldsToEvent(savedEvent.getId(),Set.of(selectionField));

        Event updatedEvent = eventService.getEventById(savedEvent.getId());

        Assertions.assertEquals(updatedEvent.getSelectionFields().size(),1);
        Assertions.assertEquals(updatedEvent.getSelectionFields().stream().findFirst().get().getOptions().size(), 1);
    }

    @Test
    public void shouldRemoveSelectionFields() throws UserException {
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);
        Event event = new Event(savedUser,"kertiparti", new HashSet<>(),"");

        Event savedEvent = eventService.saveEvent(event);

        SelectionField selectionField = new SelectionField("mezo",false,false);
        Option option = new Option("ertek");
        selectionField.addOptions(Set.of(option));

        eventService.addFieldsToEvent(savedEvent.getId(),Set.of(selectionField));
        eventService.removeFieldsFromEvent(savedEvent.getId(),Set.of(eventService.getEventById(savedEvent.getId()).getSelectionFields().stream().findFirst().get().getId()));

        Event updatedEvent = eventService.getEventById(savedEvent.getId());

        Assertions.assertEquals(updatedEvent.getSelectionFields().size(),0);
    }

    @Test
    public void shouldAddOptionsToField() throws UserException {
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);
        Event event = new Event(savedUser,"kertiparti", new HashSet<>(),"");

        Event savedEvent = eventService.saveEvent(event);

        SelectionField selectionField = new SelectionField("mezo",false,false);

        eventService.addFieldsToEvent(savedEvent.getId(),Set.of(selectionField));

        Option option = new Option("ertek");
        long fieldid = eventService.getEventById(savedEvent.getId()).getSelectionFields().stream().findFirst().get().getId();
        eventService.addFieldOptions(fieldid,Set.of(option),true);

        Event updatedEvent = eventService.getEventById(savedEvent.getId());

        Assertions.assertEquals(updatedEvent.getSelectionFields().size(),1);
        Assertions.assertEquals(updatedEvent.getSelectionFields().stream().findFirst().get().getOptions().size(), 1);
    }

    @Test
    public void shouldRemoveOptionsFromField() throws UserException {
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);
        Event event = new Event(savedUser,"kertiparti", new HashSet<>(),"");

        Event savedEvent = eventService.saveEvent(event);

        SelectionField selectionField = new SelectionField("mezo",false,false);

        eventService.addFieldsToEvent(savedEvent.getId(),Set.of(selectionField));

        Option option = new Option("ertek");
        long fieldid = eventService.getEventById(savedEvent.getId()).getSelectionFields().stream().findFirst().get().getId();
        eventService.addFieldOptions(fieldid,Set.of(option),true);

        SelectionField savedField = eventService.getEventById(savedEvent.getId()).getSelectionFields().stream().findFirst().get();
        eventService.removeFieldOptions(savedField.getId(),Set.of(savedField.getOptions().stream().findFirst().get().getId()),true);

        SelectionField updatedField = eventService.getEventById(savedEvent.getId()).getSelectionFields().stream().findFirst().get();

        Assertions.assertEquals(updatedField.getOptions().size(),0);
    }

    @Test
    public void shouldAddVoteToOption() throws Exception{
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);
        Event event = new Event(savedUser,"kertiparti", new HashSet<>(),"");

        SelectionField selectionField = new SelectionField("mezo",false,true, Set.of(new Option("ertek")));
        event.setSelectionFields(Set.of(selectionField));

        Event savedEvent = eventService.saveEvent(event);

        long fieldid = eventService.getEventById(savedEvent.getId()).getSelectionFields().stream().findFirst().get().getId();
        long optionid = eventService.getSelectionFieldById(fieldid).getOptions().stream().findFirst().get().getId();

        eventService.addVote(optionid,fieldid,user);

        Assertions.assertEquals(eventService.getOptionById(optionid).getVoters().size(),1);
    }

    @Test
    public void shouldRemoveVoteFromOption() throws Exception{
        User owner = new User("felh","asznalo1","email@gmail.com");
        User savedOwner = userService.saveUser(owner);
        Event event = new Event(savedOwner,"kertiparti", new HashSet<>(),"");

        SelectionField selectionField = new SelectionField("mezo",false,true, Set.of(new Option("ertek")));
        event.setSelectionFields(Set.of(selectionField));

        Event savedEvent = eventService.saveEvent(event);

        long fieldid = eventService.getEventById(savedEvent.getId()).getSelectionFields().stream().findFirst().get().getId();
        long optionid = eventService.getSelectionFieldById(fieldid).getOptions().stream().findFirst().get().getId();

        eventService.addVote(optionid,fieldid,owner);

        eventService.removeVote(optionid,fieldid,owner);

        Assertions.assertEquals(eventService.getOptionById(optionid).getVoters().size(),0);
    }

    @Test
    public void shouldSelectOptionByMostVotes() throws Exception{
        User owner = new User("felh","asznalo1","email@gmail.com");
        User savedOwner = userService.saveUser(owner);
        User user = new User("felh2","asznalo1","email2@gmail.com");
        User savedUser = userService.saveUser(user);

        Event event = new Event(savedOwner,"kertiparti", new HashSet<>(),"");

        SelectionField selectionField = new SelectionField("mezo",false,true, Set.of(new Option("ertek"),new Option("ertek2")));
        event.setSelectionFields(Set.of(selectionField));

        Event savedEvent = eventService.saveEvent(event);

        long fieldid = eventService.getEventById(savedEvent.getId()).getSelectionFields().stream().findFirst().get().getId();
        long firstOptionId = eventService.getSelectionFieldById(fieldid).getOptions().stream().filter((o)->o.getValue().equals("ertek")).findFirst().get().getId();
        long secondOptionId = eventService.getSelectionFieldById(fieldid).getOptions().stream().filter((o)->o.getValue().equals("ertek2")).findFirst().get().getId();

        eventService.addVote(firstOptionId,fieldid,user);

        Assertions.assertTrue(eventService.getOptionById(firstOptionId).isSelected());
        Assertions.assertFalse(eventService.getOptionById(secondOptionId).isSelected());

        eventService.addVote(secondOptionId,fieldid,user);
        eventService.addVote(secondOptionId,fieldid,owner);

        Assertions.assertFalse(eventService.getOptionById(firstOptionId).isSelected());
        Assertions.assertTrue(eventService.getOptionById(secondOptionId).isSelected());
    }

    @Test
    public void shouldSelectOptionManually() throws Exception{
        User owner = new User("felh","asznalo1","email@gmail.com");
        User savedOwner = userService.saveUser(owner);
        User user = new User("felh2","asznalo1","email2@gmail.com");
        User savedUser = userService.saveUser(user);

        Event event = new Event(savedOwner,"kertiparti", new HashSet<>(),"");

        SelectionField selectionField = new SelectionField("mezo",false,true, Set.of(new Option("ertek"),new Option("ertek2")));
        event.setSelectionFields(Set.of(selectionField));

        Event savedEvent = eventService.saveEvent(event);

        long fieldid = eventService.getEventById(savedEvent.getId()).getSelectionFields().stream().findFirst().get().getId();
        long firstOptionId = eventService.getSelectionFieldById(fieldid).getOptions().stream().filter((o)->o.getValue().equals("ertek")).findFirst().get().getId();
        long secondOptionId = eventService.getSelectionFieldById(fieldid).getOptions().stream().filter((o)->o.getValue().equals("ertek2")).findFirst().get().getId();

        eventService.addVote(firstOptionId,fieldid,user);
        eventService.addVote(secondOptionId,fieldid,user);
        eventService.addVote(secondOptionId,fieldid,owner);

        eventService.selectOption(firstOptionId,fieldid);

        Assertions.assertTrue(eventService.getOptionById(firstOptionId).isSelected());
        Assertions.assertFalse(eventService.getOptionById(secondOptionId).isSelected());
    }

    @Test
    public void shouldFinalizeEvent() throws UserException, EventCannotBeFinalizedException, CalendarException {
        User owner = new User("felh","asznalo1","email@gmail.com");
        User savedOwner = userService.saveUser(owner);
        User user = new User("felh2","asznalo1","email2@gmail.com");
        User savedUser = userService.saveUser(user);

        Event event = new Event(savedOwner,"kertiparti", new HashSet<>(Set.of(savedUser)),"");

        SelectionField selectionField = new SelectionField("mezo",false,true, Set.of(new Option("ertek"),new Option("ertek2")));
        event.setSelectionFields(Set.of(selectionField));

        Event savedEvent = eventService.saveEvent(event);

        eventService.setEventCalendar(savedEvent.getId(),"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(2));

        long fieldid = eventService.getEventById(savedEvent.getId()).getSelectionFields().stream().findFirst().get().getId();
        long firstOptionId = eventService.getSelectionFieldById(fieldid).getOptions().stream().filter((o)->o.getValue().equals("ertek")).findFirst().get().getId();
        long secondOptionId = eventService.getSelectionFieldById(fieldid).getOptions().stream().filter((o)->o.getValue().equals("ertek2")).findFirst().get().getId();

        eventService.addVote(firstOptionId,fieldid,user);
        eventService.addVote(secondOptionId,fieldid,user);
        eventService.addVote(secondOptionId,fieldid,owner);

        eventService.selectOption(firstOptionId,fieldid);

        eventService.setStartTimeAndEndTime(event.getId(), 5,10);

        eventService.finalizeEvent(event.getId());

        Assertions.assertEquals(mailService.getUnreadMails(user).size(),1);
        Assertions.assertEquals(mailService.getUnreadMails(owner).size(),1);

        Assertions.assertTrue(eventService.getEventById(event.getId()).isFinalized());
    }

    @Test
    public void shouldNotFinalizeEventIfFieldsAreNotSet() throws Exception {
        User owner = new User("felh","asznalo1","email@gmail.com");
        User savedOwner = userService.saveUser(owner);
        User user = new User("felh2","asznalo1","email2@gmail.com");
        User savedUser = userService.saveUser(user);

        Event event = new Event(savedOwner,"kertiparti", new HashSet<>(Set.of(savedUser)),"");

        SelectionField selectionField = new SelectionField("mezo",false,true, Set.of(new Option("ertek"),new Option("ertek2")));
        event.setSelectionFields(Set.of(selectionField));

        Event savedEvent = eventService.saveEvent(event);

        eventService.setEventCalendar(savedEvent.getId(),"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(2));

        long fieldid = eventService.getEventById(savedEvent.getId()).getSelectionFields().stream().findFirst().get().getId();
        long firstOptionId = eventService.getSelectionFieldById(fieldid).getOptions().stream().filter((o)->o.getValue().equals("ertek")).findFirst().get().getId();
        long secondOptionId = eventService.getSelectionFieldById(fieldid).getOptions().stream().filter((o)->o.getValue().equals("ertek2")).findFirst().get().getId();

        eventService.addVote(firstOptionId,fieldid,user);
        eventService.addVote(secondOptionId,fieldid,user);
        eventService.addVote(secondOptionId,fieldid,owner);

        eventService.selectOption(firstOptionId,fieldid);

        Assertions.assertThrows(EventCannotBeFinalizedException.class,()->{
            eventService.finalizeEvent(event.getId());
        });
    }

    @Test
    public void shouldThrowExceptionIfFinalizedEventIsModified() throws Exception{
        User owner = new User("felh","asznalo1","email@gmail.com");
        User savedOwner = userService.saveUser(owner);
        User user = new User("felh2","asznalo1","email2@gmail.com");
        User savedUser = userService.saveUser(user);

        Event event = new Event(savedOwner,"kertiparti", new HashSet<>(),"");

        SelectionField selectionField = new SelectionField("mezo",false,true, Set.of(new Option("ertek"),new Option("ertek2")));
        event.setSelectionFields(Set.of(selectionField));

        Event savedEvent = eventService.saveEvent(event);

        long fieldid = eventService.getEventById(savedEvent.getId()).getSelectionFields().stream().findFirst().get().getId();
        long firstOptionId = eventService.getSelectionFieldById(fieldid).getOptions().stream().filter((o)->o.getValue().equals("ertek")).findFirst().get().getId();

        eventService.selectOption(firstOptionId,fieldid);

        eventService.finalizeEvent(event.getId());

        Assertions.assertThrows(EventIsFinalizedException.class, ()->{
            eventService.checkIfEventIsFinalized(event.getId());
        });
    }

    @Test
    public void shouldCreateEventBlueprint() throws Exception{
        User owner = new User("felh","asznalo1","email@gmail.com");
        User savedOwner = userService.saveUser(owner);

        Event event = new Event(savedOwner,"kertiparti", new HashSet<>(),"");

        Event savedEvent = eventService.saveEvent(event);

        SelectionField selectionField = new SelectionField("mezo",false,true, Set.of(new Option("ertek"),new Option("ertek2")));
        event.setSelectionFields(Set.of(selectionField));

        eventService.addFieldsToEvent(savedEvent.getId(), Set.of(selectionField));

        EventBlueprint blueprint = eventService.createEventBlueprint(savedEvent.getId(),"schema",owner);

        Assertions.assertNotNull(blueprint);

        Event newEvent = new Event(savedOwner,"ujparti", new HashSet<>(),"");

        Event savedNewEvent = eventService.saveEvent(newEvent);

        eventService.addFieldsFromBluePrint(savedNewEvent.getId(),blueprint.getId());

        long fieldid = eventService.getEventById(savedNewEvent.getId()).getSelectionFields().stream().findFirst().get().getId();
        Assertions.assertNotNull(eventService.getSelectionFieldById(fieldid).getOptions().stream().filter((o)->o.getValue().equals("ertek")).findFirst().orElse(null));
    }

    @Test
    public void shouldUnFinalizeEvent() throws Exception{
        User owner = new User("felh","asznalo1","email@gmail.com");
        User savedOwner = userService.saveUser(owner);
        User user = new User("felh2","asznalo1","email2@gmail.com");
        User savedUser = userService.saveUser(user);

        Event event = new Event(savedOwner,"kertiparti", new HashSet<>(Set.of(savedUser)),"");

        Event savedEvent = eventService.saveEvent(event);

        eventService.setEventCalendar(savedEvent.getId(),"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(2));

        eventService.setStartTimeAndEndTime(savedEvent.getId(),1,3);

        eventService.finalizeEvent(event.getId());

        eventService.unFinalizeEvent(event.getId());

        Assertions.assertFalse(eventService.getEventById(event.getId()).isFinalized());
    }
}
