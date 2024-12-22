package com.example.eowa.controller;

import com.example.eowa.EowaIntegrationTest;
import com.example.eowa.model.*;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
public class EventControllerTest extends EowaIntegrationTest {

    @Test
    public void shouldCreateEvent() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        String jsessionid = authService.login(credentials);

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");

        var response = mockMvc.perform(post("/events/create")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(event))
                .accept("application/json")
                .cookie(new Cookie("jsessionid",jsessionid)))
                .andReturn().getResponse();
        Event savedEvent = objectMapper.readValue(response.getContentAsString(), Event.class);

        Assertions.assertEquals(savedEvent.getOwner().getUsername(),"felh");
    }

    @Test
    public void shouldGetEventById() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        String jsessionid = authService.login(credentials);

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");
        long id = eventService.saveEvent(event).getId();

        var response = mockMvc.perform(get("/events/"+id)
                        .accept("application/json")
                        .cookie(new Cookie("jsessionid",jsessionid)))
                .andReturn().getResponse();

        Event savedEvent = objectMapper.readValue(response.getContentAsString(), Event.class);

        Assertions.assertEquals(savedEvent.getOwner().getUsername(),"felh");
    }

    @Test
    public void shouldGetCurrentUserEvents() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        String jsessionid = authService.login(credentials);

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");
        eventService.saveEvent(event);

        var response = mockMvc.perform(get("/events/currentuser-events")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(event))
                        .accept("application/json")
                        .cookie(new Cookie("jsessionid",jsessionid)))
                .andReturn().getResponse();

        Event[] events = objectMapper.readValue(response.getContentAsString(), Event[].class);

        Assertions.assertEquals(events[0].getOwner().getUsername(),"felh");
    }

    @Test
    public void shouldDeleteEventById() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        String jsessionid = authService.login(credentials);

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");
        long id = eventService.saveEvent(event).getId();

        mockMvc.perform(delete("/events/"+id)
                        .accept("application/json")
                        .cookie(new Cookie("jsessionid",jsessionid)));

        Assertions.assertNull(eventService.getEventById(id));
    }

    @Test
    public void shouldAddParticipants() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        User savedUser2 = userService.saveUser(new User("felh2","asznalo2","email2@gmail.com"));
        User savedUser3 = userService.saveUser(new User("felh3","asznalo3","email3@gmail.com"));

        Set<User> participants = new HashSet<>();
        participants.add(savedUser2);
        participants.add(savedUser3);

        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        String jsessionid = authService.login(credentials);

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");
        long id = eventService.saveEvent(event).getId();

        mockMvc.perform(put("/events/"+id+"/add-users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(participants))
                        .accept("application/json")
                        .cookie(new Cookie("jsessionid",jsessionid)));

        Event savedEvent = eventService.getEventById(id);

        Assertions.assertEquals(savedEvent.getParticipants().size(),3);
    }

    @Test
    public void shouldSetCalendar() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        String jsessionid = authService.login(credentials);

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");

        var response = mockMvc.perform(post("/events/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(event))
                        .accept("application/json")
                        .cookie(new Cookie("jsessionid",jsessionid)))
                .andReturn().getResponse();

        Event savedEvent = objectMapper.readValue(response.getContentAsString(), Event.class);

        String start = LocalDateTime.now().toString();
        String end = LocalDateTime.now().plusDays(3).toString();

        mockMvc.perform(put("/events/"+savedEvent.getId()+"/add-calendar")
                .cookie(new Cookie("jsessionid",jsessionid))
                .param("start",start)
                .param("end",end)
                .param("zone","CET")
                .accept("application/json"));

        savedEvent = eventService.getEventById(savedEvent.getId());

        Assertions.assertEquals(3,savedEvent.getCalendar().getDays().size());
    }

    @Test
    public void shouldNotCreateTooLongCalendar() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        String jsessionid = authService.login(credentials);

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");

        var response = mockMvc.perform(post("/events/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(event))
                        .accept("application/json")
                        .cookie(new Cookie("jsessionid",jsessionid)))
                .andReturn().getResponse();

        Event savedEvent = objectMapper.readValue(response.getContentAsString(), Event.class);

        String start = LocalDateTime.now().toString();
        String end = LocalDateTime.now().plusDays(100).toString();

        MockHttpServletResponse mockResponse =  mockMvc.perform(put("/events/"+savedEvent.getId()+"/add-calendar")
                .cookie(new Cookie("jsessionid",jsessionid))
                .param("start",start)
                .param("end",end)
                .param("zone","CET")
                .accept("application/json"))
                .andReturn().getResponse();

        Assertions.assertEquals(mockResponse.getStatus(), HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    public void shouldSetUnavailableDays() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        String jsessionid = authService.login(credentials);

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id,"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(3));

        String daySerials = objectMapper.writeValueAsString(Set.of(0,1));

        mockMvc.perform(put("/events/"+id+"/set-unavailable-days")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(daySerials)
                        .cookie(new Cookie("jsessionid",jsessionid)));

        List<Day> updatedDays = eventService.getEventById(id).getCalendar().getDays();

        Assertions.assertFalse(updatedDays.getFirst().isEnabled());
        Assertions.assertFalse(updatedDays.get(1).isEnabled());
        Assertions.assertTrue(updatedDays.get(2).isEnabled());
    }

    @Test
    public void shouldSetUnavailableHours() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        String jsessionid = authService.login(credentials);

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id,"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(3));

        String hourSerials = objectMapper.writeValueAsString(Set.of(0,1));

        mockMvc.perform(put("/events/"+id+"/set-unavailable-hours")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(hourSerials)
                        .cookie(new Cookie("jsessionid",jsessionid)));

        List<Hour> updatedHoursOfFirstDay = eventService.getEventById(id).getCalendar().getDays().getFirst().getHours();

        Assertions.assertFalse(updatedHoursOfFirstDay.getFirst().isEnabled());
        Assertions.assertFalse(updatedHoursOfFirstDay.get(1).isEnabled());
        Assertions.assertTrue(updatedHoursOfFirstDay.get(2).isEnabled());
    }

    @Test
    public void setUnavailableHoursPeriodically() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        String jsessionid = authService.login(credentials);

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id,"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(10));

        String hourSerials = objectMapper.writeValueAsString(Set.of(15,16));

        mockMvc.perform(put("/events/"+id+"/set-unavailable-hours-periodically")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(hourSerials)
                        .queryParam("period","7")
                        .cookie(new Cookie("jsessionid",jsessionid)));

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
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        String jsessionid = authService.login(credentials);

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id,"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(3));

        String hourSerials = objectMapper.writeValueAsString(Set.of(0,1));

        mockMvc.perform(put("/events/"+id+"/set-user-opinion")
                        .contentType("application/json")
                        .accept("application/json")
                        .queryParam("opinion","GOOD")
                        .content(hourSerials)
                        .cookie(new Cookie("jsessionid",jsessionid)));

        List<Hour> updatedHoursOfFirstDay = eventService.getEventById(id).getCalendar().getDays().getFirst().getHours();


        User currentUser = userService.getUserByUsername(savedUser.getUsername());

        Assertions.assertEquals(calendarService.getUserOpinion(updatedHoursOfFirstDay.getFirst(),currentUser).getUserOpinion(), Opinion.UserOpinion.GOOD);
        Assertions.assertEquals(calendarService.getUserOpinion(updatedHoursOfFirstDay.get(1),currentUser).getUserOpinion(), Opinion.UserOpinion.GOOD);
        Assertions.assertEquals(updatedHoursOfFirstDay.get(2).getOpinions().size(),0);
    }

    @Test
    public void shouldRemoveUserOpinion() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        String jsessionid = authService.login(credentials);

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id,"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(3));

        eventService.setUserOpinion(id,Set.of(0,1,2),sessionService.getUserBySessionId(jsessionid), Opinion.UserOpinion.GOOD);

        String hourSerials = objectMapper.writeValueAsString(Set.of(2,1));

        mockMvc.perform(put("/events/"+id+"/remove-user-opinion")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(hourSerials)
                        .cookie(new Cookie("jsessionid",jsessionid)));

        List<Hour> updatedHoursOfFirstDay = eventService.getEventById(id).getCalendar().getDays().getFirst().getHours();


        User currentUser = userService.getUserByUsername(savedUser.getUsername());

        Assertions.assertEquals(calendarService.getUserOpinion(updatedHoursOfFirstDay.getFirst(),currentUser).getUserOpinion(), Opinion.UserOpinion.GOOD);
        Assertions.assertEquals(updatedHoursOfFirstDay.get(1).getOpinions().size(),0);
        Assertions.assertEquals(updatedHoursOfFirstDay.get(2).getOpinions().size(),0);
    }

    @Test
    public void shouldJoinEVentWIthInvitationCode() throws Exception {
        User savedUser = userService.saveUser(new User("felh2","asznalo2","email2@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo2");
        credentials.setUsername("felh2");

        authService.login(credentials);

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id,"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(3));

        userService.saveUser(new User("felh", "asznalo1", "email@gmail.com"));
        Credentials participantCredentials = new Credentials();
        participantCredentials.setPassword("asznalo1");
        participantCredentials.setUsername("felh");

        String participantJsessionid = authService.login(participantCredentials);

        mockMvc.perform(put("/events/join-event")
                        .accept("application/json")
                        .queryParam("invitation",eventService.getEventById(id).getInvitationCode())
                        .cookie(new Cookie("jsessionid",participantJsessionid)));

        authService.validateParticipant(participantJsessionid,id);
    }

    @Test
    public void shouldNotLetUserJoinWithInvalidInvitationCode() throws Exception {
        User savedUser = userService.saveUser(new User("felh2","asznalo2","email2@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo2");
        credentials.setUsername("felh2");

        authService.login(credentials);

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id,"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(3));

        userService.saveUser(new User("felh", "asznalo1", "email@gmail.com"));
        Credentials participantCredentials = new Credentials();
        participantCredentials.setPassword("asznalo1");
        participantCredentials.setUsername("felh");

        String participantJsessionid = authService.login(participantCredentials);

        MockHttpServletResponse response =  mockMvc.perform(put("/events/join-event")
                        .accept("application/json")
                        .queryParam("invitation",eventService.getEventById(id).getInvitationCode()+"010110")
                        .cookie(new Cookie("jsessionid",participantJsessionid)))
                .andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(),HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    public void shouldNotLetUnauthorizedUserSetAvailableDays() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));

        User stranger = userService.saveUser(new User("stranger","asznalo1","email2@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("stranger");

        String jsessionid = authService.login(credentials);

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");
        event.addParticipant(stranger);
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id,"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(3));

        String daySerials = objectMapper.writeValueAsString(Set.of(0,1));

        MockHttpServletResponse response = mockMvc.perform(put("/events/"+id+"/set-unavailable-days")
                .contentType("application/json")
                .accept("application/json")
                .content(daySerials)
                .cookie(new Cookie("jsessionid",jsessionid)))
                .andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(),HttpStatus.FORBIDDEN.value());
    }

    @Test
    public void shouldNotLetUnauthorizedUserSetUserOpinion() throws Exception {
        User owner = userService.saveUser(new User("felh1","asznalo1","email1@gmail.com"));
        userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        String jsessionid = authService.login(credentials);

        Event event = new Event(owner,"buli",new HashSet<>(),"");
        long id = eventService.saveEvent(event).getId();

        eventService.setEventCalendar(id,"CET", LocalDateTime.now(),LocalDateTime.now().plusDays(3));

        String hourSerials = objectMapper.writeValueAsString(Set.of(0,1));

        MockHttpServletResponse response = mockMvc.perform(put("/events/"+id+"/set-user-opinion")
                .contentType("application/json")
                .accept("application/json")
                .queryParam("opinion","GOOD")
                .content(hourSerials)
                .cookie(new Cookie("jsessionid",jsessionid)))
                .andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(),HttpStatus.FORBIDDEN.value());
    }

}
