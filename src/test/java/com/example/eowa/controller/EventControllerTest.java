package com.example.eowa.controller;

import com.example.eowa.EowaIntegrationTest;
import com.example.eowa.exceptions.authenticationExceptions.AuthenticationException;
import com.example.eowa.exceptions.userExceptions.UserException;
import com.example.eowa.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class EventControllerTest extends EowaIntegrationTest {

    @Test
    public void shouldCreateEvent() throws Exception {
        User user = new User("felh", "asznalo1", "email@gmail.com");
        User savedUser = userService.saveUser(user);
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        WebToken jwt = loginUserAndGetWebToken(credentials,user);

        Event event = new Event(savedUser, "buli", new HashSet<>(), "");

        var response = mockMvc.perform(post("/events/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(event))
                        .accept("application/json")
                        .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)))
                .andReturn().getResponse();
        Event savedEvent = objectMapper.readValue(response.getContentAsString(), Event.class);

        Assertions.assertEquals(savedEvent.getOwner().getUsername(), "felh");
    }

    @Test
    public void shouldGetEventById() throws Exception {
        User user = new User("felh", "asznalo1", "email@gmail.com");
        User savedUser = userService.saveUser(user);
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        WebToken jwt = loginUserAndGetWebToken(credentials,user);

        Event event = new Event(savedUser, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();
        eventService.setEventCalendar(id,"CET",LocalDateTime.now(),LocalDateTime.now().plusDays(2));

        var response = mockMvc.perform(get("/events/" + id)
                        .accept("application/json")
                        .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)))
                .andReturn().getResponse();

        Event savedEvent = objectMapper.readValue(response.getContentAsString(), Event.class);

        Assertions.assertEquals(savedEvent.getOwner().getUsername(), "felh");
    }

    @Test
    public void shouldGetCurrentUserEvents() throws Exception {
        User user = new User("felh", "asznalo1", "email@gmail.com");
        User savedUser = userService.saveUser(user);
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        WebToken jwt = loginUserAndGetWebToken(credentials,user);

        Event event = new Event(savedUser, "buli", new HashSet<>(), "");
        eventService.saveEvent(event);

        var response = mockMvc.perform(get("/events/currentuser-events")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(event))
                        .accept("application/json")
                        .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)))
                .andReturn().getResponse();

        Event[] events = objectMapper.readValue(response.getContentAsString(), Event[].class);

        Assertions.assertEquals(events[0].getOwner().getUsername(), "felh");
    }

    @Test
    public void shouldDeleteEventById() throws Exception {
        User user = new User("felh", "asznalo1", "email@gmail.com");
        User savedUser = userService.saveUser(user);
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        WebToken jwt = loginUserAndGetWebToken(credentials,user);

        Event event = new Event(savedUser, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        mockMvc.perform(delete("/events/" + id)
                .accept("application/json")
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        Assertions.assertNull(eventService.getEventById(id));
    }

    @Test
    public void shouldAddParticipants() throws Exception {
        User user = new User("felh", "asznalo1", "email@gmail.com");
        User savedUser = userService.saveUser(user);
        User savedUser2 = userService.saveUser(new User("felh2", "asznalo2", "email2@gmail.com"));
        User savedUser3 = userService.saveUser(new User("felh3", "asznalo3", "email3@gmail.com"));

        Set<User> participants = new HashSet<>();
        participants.add(savedUser2);
        participants.add(savedUser3);

        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        WebToken jwt = loginUserAndGetWebToken(credentials,user);

        Event event = new Event(savedUser, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        mockMvc.perform(put("/events/" + id + "/add-users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(participants))
                .accept("application/json")
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        Event savedEvent = eventService.getEventById(id);

        Assertions.assertEquals(savedEvent.getParticipants().size(), 3);
    }

    @Test
    public void shouldSetCalendar() throws Exception {
        User user = new User("felh", "asznalo1", "email@gmail.com");
        User savedUser = userService.saveUser(user);
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        WebToken jwt = loginUserAndGetWebToken(credentials,user);

        Event event = new Event(savedUser, "buli", new HashSet<>(), "");

        var response = mockMvc.perform(post("/events/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(event))
                        .accept("application/json")
                        .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)))
                .andReturn().getResponse();

        Event savedEvent = objectMapper.readValue(response.getContentAsString(), Event.class);

        String start = LocalDateTime.now().toString();
        String end = LocalDateTime.now().plusDays(3).toString();

        mockMvc.perform(put("/events/" + savedEvent.getId() + "/add-calendar")
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt))
                .param("start", start)
                .param("end", end)
                .param("zone", "CET")
                .accept("application/json"));

        savedEvent = eventService.getEventById(savedEvent.getId());

        Assertions.assertEquals(4, savedEvent.getCalendar().getDays().size());
    }

    @Test
    public void shouldNotCreateTooLongCalendar() throws Exception {
        User user = new User("felh", "asznalo1", "email@gmail.com");
        User savedUser = userService.saveUser(user);
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        WebToken jwt = loginUserAndGetWebToken(credentials,user);

        Event event = new Event(savedUser, "buli", new HashSet<>(), "");

        var response = mockMvc.perform(post("/events/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(event))
                        .accept("application/json")
                        .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)))
                .andReturn().getResponse();

        Event savedEvent = objectMapper.readValue(response.getContentAsString(), Event.class);

        String start = LocalDateTime.now().toString();
        String end = LocalDateTime.now().plusDays(100).toString();

        MockHttpServletResponse mockResponse = mockMvc.perform(put("/events/" + savedEvent.getId() + "/add-calendar")
                        .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt))
                        .param("start", start)
                        .param("end", end)
                        .param("zone", "CET")
                        .accept("application/json"))
                .andReturn().getResponse();

        Assertions.assertEquals(mockResponse.getStatus(), HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    public void shouldSetUnavailableDays() throws Exception {
        User user = new User("felh", "asznalo1", "email@gmail.com");
        User savedUser = userService.saveUser(user);
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        WebToken jwt = loginUserAndGetWebToken(credentials,user);

        Event event = new Event(savedUser, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id, "CET", LocalDateTime.now(), LocalDateTime.now().plusDays(3));

        String daySerials = objectMapper.writeValueAsString(Set.of(0, 1));

        mockMvc.perform(put("/events/" + id + "/set-unavailable-days")
                .contentType("application/json")
                .accept("application/json")
                .content(daySerials)
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        List<Day> updatedDays = eventService.getEventById(id).getCalendar().getDays();

        Assertions.assertFalse(updatedDays.getFirst().isEnabled());
        Assertions.assertFalse(updatedDays.get(1).isEnabled());
        Assertions.assertTrue(updatedDays.get(2).isEnabled());
    }

    @Test
    public void shouldSetUnavailableHours() throws Exception {
        User user = new User("felh", "asznalo1", "email@gmail.com");
        User savedUser = userService.saveUser(user);
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        WebToken jwt = loginUserAndGetWebToken(credentials,user);

        Event event = new Event(savedUser, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id, "CET", LocalDateTime.now(), LocalDateTime.now().plusDays(3));

        String hourSerials = objectMapper.writeValueAsString(Set.of(0, 1));

        mockMvc.perform(put("/events/" + id + "/set-unavailable-hours")
                .contentType("application/json")
                .accept("application/json")
                .content(hourSerials)
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        List<Hour> updatedHoursOfFirstDay = eventService.getEventById(id).getCalendar().getDays().getFirst().getHours();

        Assertions.assertFalse(updatedHoursOfFirstDay.getFirst().isEnabled());
        Assertions.assertFalse(updatedHoursOfFirstDay.get(1).isEnabled());
        Assertions.assertTrue(updatedHoursOfFirstDay.get(2).isEnabled());
    }

    @Test
    public void setUnavailableHoursPeriodically() throws Exception {
        User user = new User("felh", "asznalo1", "email@gmail.com");
        User savedUser = userService.saveUser(user);
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        WebToken jwt = loginUserAndGetWebToken(credentials,user);

        Event event = new Event(savedUser, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id, "CET", LocalDateTime.now(), LocalDateTime.now().plusDays(10));

        String hourSerials = objectMapper.writeValueAsString(Set.of(15, 16));

        mockMvc.perform(put("/events/" + id + "/set-unavailable-hours-periodically")
                .contentType("application/json")
                .accept("application/json")
                .content(hourSerials)
                .queryParam("period", "7")
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        List<Day> updatedDays = eventService.getEventById(id).getCalendar().getDays();

        Assertions.assertTrue(updatedDays.getFirst().getHours().getFirst().isEnabled());
        Assertions.assertFalse(updatedDays.getFirst().getHours().get(15).isEnabled());

        Assertions.assertTrue(updatedDays.get(3).getHours().getFirst().isEnabled());
        Assertions.assertTrue(updatedDays.get(3).getHours().get(15).isEnabled());

        Assertions.assertTrue(updatedDays.get(7).getHours().getFirst().isEnabled());
        Assertions.assertFalse(updatedDays.get(7).getHours().get(15).isEnabled());
    }


    @Test
    public void shouldSetUserOpinion() throws Exception {
        User user = new User("felh", "asznalo1", "email@gmail.com");
        User savedUser = userService.saveUser(user);
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        WebToken jwt = loginUserAndGetWebToken(credentials,user);

        Event event = new Event(savedUser, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id, "CET", LocalDateTime.now(), LocalDateTime.now().plusDays(3));

        String hourSerials = objectMapper.writeValueAsString(Set.of(0, 1));

        mockMvc.perform(put("/events/" + id + "/set-user-opinion")
                .contentType("application/json")
                .accept("application/json")
                .queryParam("opinion", "GOOD")
                .content(hourSerials)
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        List<Hour> updatedHoursOfFirstDay = eventService.getEventById(id).getCalendar().getDays().getFirst().getHours();


        User currentUser = userService.getUserByUsername(savedUser.getUsername());

        Assertions.assertEquals(calendarService.getUserOpinion(updatedHoursOfFirstDay.getFirst(), currentUser).getUserOpinion(), Opinion.UserOpinion.GOOD);
        Assertions.assertEquals(calendarService.getUserOpinion(updatedHoursOfFirstDay.get(1), currentUser).getUserOpinion(), Opinion.UserOpinion.GOOD);
        Assertions.assertEquals(updatedHoursOfFirstDay.get(2).getOpinions().size(), 0);
    }

    @Test
    public void shouldRemoveUserOpinion() throws Exception {
        User user = new User("felh", "asznalo1", "email@gmail.com");
        User savedUser = userService.saveUser(user);
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        WebToken jwt = loginUserAndGetWebToken(credentials,user);

        Event event = new Event(savedUser, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id, "CET", LocalDateTime.now(), LocalDateTime.now().plusDays(3));

        eventService.setUserOpinion(id, Set.of(0, 1, 2), sessionService.getUserBySessionId(jwt.getJsessionid()), Opinion.UserOpinion.GOOD);

        String hourSerials = objectMapper.writeValueAsString(Set.of(2, 1));

        mockMvc.perform(put("/events/" + id + "/remove-user-opinion")
                .contentType("application/json")
                .accept("application/json")
                .content(hourSerials)
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        List<Hour> updatedHoursOfFirstDay = eventService.getEventById(id).getCalendar().getDays().getFirst().getHours();


        User currentUser = userService.getUserByUsername(savedUser.getUsername());

        Assertions.assertEquals(calendarService.getUserOpinion(updatedHoursOfFirstDay.getFirst(), currentUser).getUserOpinion(), Opinion.UserOpinion.GOOD);
        Assertions.assertEquals(updatedHoursOfFirstDay.get(1).getOpinions().size(), 0);
        Assertions.assertEquals(updatedHoursOfFirstDay.get(2).getOpinions().size(), 0);
    }

    @Test
    public void shouldJoinEventWIthInvitationCode() throws Exception {
        User savedUser = userService.saveUser(new User("felh2", "asznalo2", "email2@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo2");
        credentials.setUsername("felh2");

        authService.login(credentials);

        Event event = new Event(savedUser, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id, "CET", LocalDateTime.now(), LocalDateTime.now().plusDays(3));

        User participant = new User("felh", "asznalo1", "email@gmail.com");
        User savedParticipant = userService.saveUser(participant);
        Credentials participantCredentials = new Credentials();
        participantCredentials.setPassword("asznalo1");
        participantCredentials.setUsername("felh");

        WebToken jwt = loginUserAndGetWebToken(participantCredentials,savedParticipant);

        mockMvc.perform(put("/events/join-event")
                .accept("application/json")
                .queryParam("invitation", eventService.getEventById(id).getInvitationCode())
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        authService.validateParticipant(jwt.getJsessionid(), id);
    }

    @Test
    public void shouldNotLetUserJoinWithInvalidInvitationCode() throws Exception {
        User savedUser = userService.saveUser(new User("felh2", "asznalo2", "email2@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo2");
        credentials.setUsername("felh2");

        authService.login(credentials);

        Event event = new Event(savedUser, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id, "CET", LocalDateTime.now(), LocalDateTime.now().plusDays(3));

        User participant = new User("felh", "asznalo1", "email@gmail.com");
        userService.saveUser(participant);

        WebToken jwt = loginUserAndGetWebToken(credentials,participant);

        MockHttpServletResponse response = mockMvc.perform(put("/events/join-event")
                        .accept("application/json")
                        .queryParam("invitation", eventService.getEventById(id).getInvitationCode() + "010110")
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)))
                .andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(), HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    public void shouldNotLetUnauthorizedUserSetAvailableDays() throws Exception {
        User user = new User("felh", "asznalo1", "email@gmail.com");
        User savedUser = userService.saveUser(user);

        User stranger = userService.saveUser(new User("stranger", "asznalo1", "email2@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("stranger");

        WebToken jwt = loginUserAndGetWebToken(credentials,user);

        Event event = new Event(savedUser, "buli", new HashSet<>(), "");
        event.addParticipant(stranger);
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id, "CET", LocalDateTime.now(), LocalDateTime.now().plusDays(3));

        String daySerials = objectMapper.writeValueAsString(Set.of(0, 1));

        MockHttpServletResponse response = mockMvc.perform(put("/events/" + id + "/set-unavailable-days")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(daySerials)
                        .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)))
                .andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(), HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void shouldNotLetUnauthorizedUserSetUserOpinion() throws Exception {
        User owner = userService.saveUser(new User("felh1", "asznalo1", "email1@gmail.com"));
        User user = new User("felh", "asznalo1", "email@gmail.com");
        userService.saveUser(user);
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        WebToken jwt = loginUserAndGetWebToken(credentials,user);

        Event event = new Event(owner, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id, "CET", LocalDateTime.now(), LocalDateTime.now().plusDays(3));

        String hourSerials = objectMapper.writeValueAsString(Set.of(0, 1));

        MockHttpServletResponse response = mockMvc.perform(put("/events/" + id + "/set-user-opinion")
                        .contentType("application/json")
                        .accept("application/json")
                        .queryParam("opinion", "GOOD")
                        .content(hourSerials)
                        .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)))
                .andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(), HttpStatus.FORBIDDEN.value());
    }

    @Test()
    public void shouldAddSelectionFieldToEvent() throws Exception {
        User owner = userService.saveUser(new User("felh1", "asznalo1", "email1@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh1");

        WebToken jwt = loginUserAndGetWebToken(credentials,owner);

        Event event = new Event(owner, "buli", new HashSet<>(), "");
        Event savedEvent = eventService.saveEvent(event);
        long id = savedEvent.getId();

        SelectionField field = new SelectionField("mezo",true,false);
        Set<SelectionField> fieldset = new HashSet<>();
        fieldset.add(field);

        mockMvc.perform(put("/events/"+id+"/add-fields")
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(fieldset))
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        Assertions.assertEquals(eventService.getEventById(id).getSelectionFields().stream().findFirst().get().getTitle(),"mezo");
    }

    @Test
    public void shouldRemoveSelectionFieldFromEvent() throws Exception{
        User owner = userService.saveUser(new User("felh1", "asznalo1", "email1@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh1");

        WebToken jwt = loginUserAndGetWebToken(credentials, owner);

        Event event = new Event(owner, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        Option option = new Option("ertek");
        SelectionField field = new SelectionField("mezo",true,false,Set.of(option));
        Set<SelectionField> fieldset = new HashSet<>();
        fieldset.add(field);

        eventService.addFieldsToEvent(id,fieldset);

        Assertions.assertEquals(eventService.getEventById(id).getSelectionFields().stream().findFirst().get().getTitle(),"mezo");

        mockMvc.perform(delete("/events/"+id+"/remove-fields")
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(Set.of(1)))
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        Assertions.assertEquals(eventService.getEventById(id).getSelectionFields().size(),0);
    }


    @Test
    public void shouldAddVote() throws Exception{
        User owner = userService.saveUser(new User("felh1", "asznalo1", "email1@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh1");

        WebToken jwt = loginUserAndGetWebToken(credentials, owner);

        Event event = new Event(owner, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        Option option = new Option("ertek");
        SelectionField field = new SelectionField("mezo",true,true,Set.of(option));
        Set<SelectionField> fieldset = new HashSet<>();
        fieldset.add(field);

        eventService.addFieldsToEvent(id,fieldset);

        long fieldid = eventService.getEventById(id).getSelectionFields().stream().findFirst().get().getId();
        long optionid = eventService.getSelectionFieldById(fieldid).getOptions().stream().findFirst().get().getId();

        mockMvc.perform(put("/events/"+id+"/fields/"+fieldid+"/vote/"+optionid)
                .contentType("application/json")
                .accept("application/json")
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        Assertions.assertEquals(eventService.getOptionById(optionid).getVoters().size(),1);

    }

    @Test
    public void shouldRemoveVote() throws Exception{
        User owner = userService.saveUser(new User("felh1", "asznalo1", "email1@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh1");

        WebToken jwt = loginUserAndGetWebToken(credentials, owner);

        Event event = new Event(owner, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        Option option = new Option("ertek");
        SelectionField field = new SelectionField("mezo",true,true,Set.of(option));
        Set<SelectionField> fieldset = new HashSet<>();
        fieldset.add(field);

        eventService.addFieldsToEvent(id,fieldset);

        long fieldid = eventService.getEventById(id).getSelectionFields().stream().findFirst().get().getId();
        long optionid = eventService.getSelectionFieldById(fieldid).getOptions().stream().findFirst().get().getId();

        eventService.addVote(optionid,fieldid,owner);

        mockMvc.perform(delete("/events/"+id+"/fields/"+fieldid+"/remove-vote/"+optionid)
                .contentType("application/json")
                .accept("application/json")
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        Assertions.assertEquals(eventService.getOptionById(optionid).getVoters().size(),0);
    }

    @Test
    public void shouldSetOption() throws Exception{
        User owner = userService.saveUser(new User("felh1", "asznalo1", "email1@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh1");

        WebToken jwt = loginUserAndGetWebToken(credentials, owner);

        Event event = new Event(owner, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        Option option = new Option("ertek");
        SelectionField field = new SelectionField("mezo",true,true,Set.of(option));
        Set<SelectionField> fieldset = new HashSet<>();
        fieldset.add(field);

        eventService.addFieldsToEvent(id,fieldset);

        long fieldid = eventService.getEventById(id).getSelectionFields().stream().findFirst().get().getId();
        long firstoptionid = eventService.getSelectionFieldById(fieldid).getOptions().stream().findFirst().get().getId();

        eventService.addVote(firstoptionid,fieldid,owner);

        eventService.addFieldOptions(fieldid,Set.of(new Option("ujertek")),true);

        long secondoptionid = eventService.getSelectionFieldById(fieldid).getOptions().stream()
                .filter(o->o.getId() != firstoptionid)
                .findFirst().get().getId();

        mockMvc.perform(put("/events/"+id+"/fields/"+fieldid+"/select/"+secondoptionid)
                .contentType("application/json")
                .accept("application/json")
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        Assertions.assertTrue(eventService.getOptionById(secondoptionid).isSelected());
    }

    @Test
    public void shouldSetStartAndEnd() throws Exception{
        User owner = userService.saveUser(new User("felh1", "asznalo1", "email1@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh1");

        WebToken jwt = loginUserAndGetWebToken(credentials, owner);

        Event event = new Event(owner, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id, "CET", LocalDateTime.now(), LocalDateTime.now().plusDays(3));

        mockMvc.perform(put("/events/"+id+"/set-start-and-end?start=1&end=3")
                .contentType("application/json")
                .accept("application/json")
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        Assertions.assertEquals(eventService.getEventById(id).getCalendar().getStarthour(),1);
        Assertions.assertEquals(eventService.getEventById(id).getCalendar().getEndhour(),3);
    }

    @Test
    public void shouldResetStartAndEnd() throws Exception{
        User owner = userService.saveUser(new User("felh1", "asznalo1", "email1@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh1");

        WebToken jwt = loginUserAndGetWebToken(credentials, owner);

        Event event = new Event(owner, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id, "CET", LocalDateTime.now(), LocalDateTime.now().plusDays(3));

        eventService.setStartTimeAndEndTime(id,1,3);

        mockMvc.perform(delete("/events/"+id+"/set-start-and-end")
                .contentType("application/json")
                .accept("application/json")
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        Assertions.assertEquals(eventService.getEventById(id).getCalendar().getStarthour(),-1);
        Assertions.assertEquals(eventService.getEventById(id).getCalendar().getEndhour(),-1);
    }

    @Test
    public void shouldFinalizeEvent() throws Exception {
        User owner = userService.saveUser(new User("felh1", "asznalo1", "email1@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh1");

        WebToken jwt = loginUserAndGetWebToken(credentials, owner);

        Event event = new Event(owner, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        Option option = new Option("ertek");
        SelectionField field = new SelectionField("mezo",true,true,Set.of(option));
        Set<SelectionField> fieldset = new HashSet<>();
        fieldset.add(field);

        eventService.addFieldsToEvent(id,fieldset);

        long fieldid = eventService.getEventById(id).getSelectionFields().stream().findFirst().get().getId();
        long firstoptionid = eventService.getSelectionFieldById(fieldid).getOptions().stream().findFirst().get().getId();

        eventService.selectOption(firstoptionid,fieldid);

        mockMvc.perform(put("/events/"+id+"/finalize")
                .contentType("application/json")
                .accept("application/json")
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        Assertions.assertTrue(eventService.getEventById(id).isFinalized());
    }

    @Test
    public void shouldUnFinalizeEvent() throws Exception{
        User owner = userService.saveUser(new User("felh1", "asznalo1", "email1@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh1");

        WebToken jwt = loginUserAndGetWebToken(credentials, owner);

        Event event = new Event(owner, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        Option option = new Option("ertek");
        SelectionField field = new SelectionField("mezo",true,true,Set.of(option));
        Set<SelectionField> fieldset = new HashSet<>();
        fieldset.add(field);

        eventService.addFieldsToEvent(id,fieldset);

        long fieldid = eventService.getEventById(id).getSelectionFields().stream().findFirst().get().getId();
        long firstoptionid = eventService.getSelectionFieldById(fieldid).getOptions().stream().findFirst().get().getId();

        eventService.selectOption(firstoptionid,fieldid);

        eventService.finalizeEvent(id);

        mockMvc.perform(delete("/events/"+id+"/finalize")
                .contentType("application/json")
                .accept("application/json")
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        Assertions.assertFalse(eventService.getEventById(id).isFinalized());
    }

    @Test
    public void shouldNotLetUserModifyFinalizedEvent() throws Exception {
        User owner = userService.saveUser(new User("felh1", "asznalo1", "email1@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh1");

        WebToken jwt = loginUserAndGetWebToken(credentials, owner);

        Event event = new Event(owner, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        Option option = new Option("ertek");
        SelectionField field = new SelectionField("mezo",true,true,Set.of(option));
        Set<SelectionField> fieldset = new HashSet<>();
        fieldset.add(field);

        eventService.addFieldsToEvent(id,fieldset);

        long fieldid = eventService.getEventById(id).getSelectionFields().stream().findFirst().get().getId();
        long optionid = eventService.getSelectionFieldById(fieldid).getOptions().stream().findFirst().get().getId();

        eventService.selectOption(optionid,fieldid);

        eventService.finalizeEvent(id);

        int status = mockMvc.perform(put("/events/" + id + "/fields/" + fieldid + "/select/" + optionid)
                        .contentType("application/json")
                        .accept("application/json")
                        .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)))
                .andReturn().getResponse().getStatus();

        Assertions.assertEquals(status,HttpStatus.LOCKED.value());
    }

    @Test
    public void shouldGetBestTimeIntervals() throws Exception{
        User owner = new User("felh", "asznalo1", "email@gmail.com");
        User savedOwner = userService.saveUser(owner);
        Credentials ownerCredentials = new Credentials();
        ownerCredentials.setPassword("asznalo1");
        ownerCredentials.setUsername("felh");

        WebToken ownerJwt = loginUserAndGetWebToken(ownerCredentials,owner);

        User participant = new User("felh2", "asznalo1", "email2@gmail.com");
        User savedParticipant = userService.saveUser(participant);
        Credentials participantCredentials = new Credentials();
        participantCredentials.setPassword("asznalo1");
        participantCredentials.setUsername("felh2");

        WebToken participantJwt = loginUserAndGetWebToken(participantCredentials,participant);

        Event event = new Event(savedOwner, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id, "CET", LocalDateTime.now(), LocalDateTime.now().plusDays(3));

        eventService.setUserOpinion(id,Set.of(0,1,2,3,4,5),savedOwner, Opinion.UserOpinion.TOLERABLE);
        eventService.setUserOpinion(id,Set.of(3,4,5,6,7,8),savedParticipant, Opinion.UserOpinion.GOOD);

        String responseJson = mockMvc.perform(get("/events/" + id + "/get-best-time-intervals?participants=2&length=3")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(objectMapper.writeValueAsString(Set.of(Opinion.UserOpinion.GOOD, Opinion.UserOpinion.TOLERABLE)))
                        .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(ownerJwt)))
                .andReturn().getResponse().getContentAsString();

        List<TimeIntervalDetails> momentDetails = new ArrayList<>(List.of(objectMapper.readValue(responseJson, TimeIntervalDetails[].class)));

        momentDetails.sort(Comparator.comparingInt(TimeIntervalDetails::getLength).reversed());

        Assertions.assertEquals(momentDetails.getFirst().getParticipantNumber(),2);
        Assertions.assertEquals(momentDetails.getFirst().getLength(),3);

    }

    @Test
    public void shouldCreateEventBluePrint() throws Exception {
        User owner = userService.saveUser(new User("felh1", "asznalo1", "email1@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh1");

        WebToken jwt = loginUserAndGetWebToken(credentials, owner);

        Event event = new Event(owner, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        Option option = new Option("ertek");
        SelectionField field = new SelectionField("mezo",true,true,Set.of(option));
        Set<SelectionField> fieldset = new HashSet<>();
        fieldset.add(field);

        eventService.addFieldsToEvent(id,fieldset);

        long fieldid = eventService.getEventById(id).getSelectionFields().stream().findFirst().get().getId();
        long firstoptionid = eventService.getSelectionFieldById(fieldid).getOptions().stream().findFirst().get().getId();

        mockMvc.perform(post("/events/"+id+"/create-blueprint?name=schema")
                .contentType("application/json")
                .accept("application/json")
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        Assertions.assertNotNull(eventService.getEventBlueprintByName("schema").getContent());
    }

    @Test
    public void shouldAddFieldsFromEventBluePrint() throws Exception {
        User owner = userService.saveUser(new User("felh1", "asznalo1", "email1@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh1");

        WebToken jwt = loginUserAndGetWebToken(credentials, owner);

        Event event = new Event(owner, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        Option option = new Option("ertek");
        SelectionField field = new SelectionField("mezo",true,true,Set.of(option));
        Set<SelectionField> fieldset = new HashSet<>();
        fieldset.add(field);

        eventService.addFieldsToEvent(id,fieldset);

        long fieldid = eventService.getEventById(id).getSelectionFields().stream().findFirst().get().getId();
        long firstoptionid = eventService.getSelectionFieldById(fieldid).getOptions().stream().findFirst().get().getId();

        EventBlueprint savedBlueprint = eventService.createEventBlueprint(id,"schema",owner);

        Event newevent = new Event(owner, "ujbuli", new HashSet<>(), "");
        long newid = eventService.saveEvent(event).getId();

        mockMvc.perform(put("/events/"+newid+"/add-from-blueprint/"+savedBlueprint.getId())
                .contentType("application/json")
                .accept("application/json")
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)));

        Option importedOption = eventService.getEventById(newid).getSelectionFields().stream()
                .findFirst().get()
                .getOptions()
                .stream()
                .findFirst().get();

        Assertions.assertEquals(importedOption.getValue(),"ertek");
    }

    @Test
    public void shouldGetBlueprintsForUser() throws Exception {
        User owner = userService.saveUser(new User("felh1", "asznalo1", "email1@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh1");

        WebToken jwt = loginUserAndGetWebToken(credentials, owner);

        Event event = new Event(owner, "buli", new HashSet<>(), "");
        long id = eventService.saveEvent(event).getId();

        Option option = new Option("ertek");
        SelectionField field = new SelectionField("mezo",true,true,Set.of(option));
        Set<SelectionField> fieldset = new HashSet<>();
        fieldset.add(field);

        eventService.addFieldsToEvent(id,fieldset);

        long fieldid = eventService.getEventById(id).getSelectionFields().stream().findFirst().get().getId();
        long firstoptionid = eventService.getSelectionFieldById(fieldid).getOptions().stream().findFirst().get().getId();

        EventBlueprint savedBlueprint = eventService.createEventBlueprint(id,"schema",owner);

        MockHttpServletResponse response = mockMvc.perform(get("/events/my-blueprints")
                        .contentType("application/json")
                        .accept("application/json")
                        .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)))
                .andReturn()
                .getResponse();

        EventBlueprint blueprint = objectMapper.readValue(response.getContentAsString(),EventBlueprint[].class)[0];

        Assertions.assertEquals(blueprint.getName(),"schema");
    }
    private WebToken loginUserAndGetWebToken(Credentials credentials, User user) throws AuthenticationException, UserException {
        String jsessionid = authService.login(credentials);

        WebToken jwt = new WebToken();
        jwt.setUser(user);
        jwt.setJsessionid(jsessionid);
        jwt.setTimestamp(System.currentTimeMillis());
        return jwt;
    }
}
