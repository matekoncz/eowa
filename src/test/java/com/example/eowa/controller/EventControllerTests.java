package com.example.eowa.controller;

import com.example.eowa.EowaApplication;
import com.example.eowa.model.Credentials;
import com.example.eowa.model.Event;
import com.example.eowa.model.User;
import com.example.eowa.service.AuthService;
import com.example.eowa.service.EventService;
import com.example.eowa.service.SessionService;
import com.example.eowa.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = EowaApplication.class)
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    AuthService authService;

    @Autowired
    UserService userService;

    @Autowired
    EventService eventService;

    @Autowired
    SessionService sessionService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void beforeTests(){
        eventService.deleteAllEvent();
        userService.deleteAllUsers();
        sessionService.deleteAllSession();
    }

    @Test
    public void shouldCreateEvent() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        authService.login(credentials,"jsessionid");

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");

        var response = mockMvc.perform(post("/events/create")
                .contentType("application/json")
                        .content(objectMapper.writeValueAsString(event))
                .accept("application/json")
                .cookie(new Cookie("jsessionid","jsessionid")))
                .andReturn()
                .getResponse();
        Event savedEvent = objectMapper.readValue(response.getContentAsString(), Event.class);

        Assertions.assertEquals(savedEvent.getOwner().getUsername(),"felh");
    }

    @Test
    public void shouldGetEventById() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        authService.login(credentials,"jsessionid");

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");
        long id = eventService.saveEvent(event).getId();

        var response = mockMvc.perform(get("/events/"+id)
                        .contentType("application/json")
                        .accept("application/json")
                        .cookie(new Cookie("jsessionid","jsessionid")))
                .andReturn()
                .getResponse();
        Event savedEvent = objectMapper.readValue(response.getContentAsString(), Event.class);

        Assertions.assertEquals(savedEvent.getOwner().getUsername(),"felh");
    }

    @Test
    public void shouldGetCurrentUserEvents() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        authService.login(credentials,"jsessionid");

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");
        eventService.saveEvent(event);

        var response = mockMvc.perform(get("/events/currentuser-events")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(event))
                        .accept("application/json")
                        .cookie(new Cookie("jsessionid","jsessionid")))
                .andReturn()
                .getResponse();
        Event[] events = objectMapper.readValue(response.getContentAsString(), Event[].class);

        Assertions.assertEquals(events[0].getOwner().getUsername(),"felh");
    }

    @Test
    public void shouldDeleteEventById() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        authService.login(credentials,"jsessionid");

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");
        long id = eventService.saveEvent(event).getId();

        mockMvc.perform(delete("/events/"+id)
                        .contentType("application/json")
                        .accept("application/json")
                        .cookie(new Cookie("jsessionid","jsessionid")))
                .andReturn()
                .getResponse();

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

        authService.login(credentials,"jsessionid");

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");
        long id = eventService.saveEvent(event).getId();

        mockMvc.perform(put("/events/"+id+"/add-users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(participants))
                        .accept("application/json")
                        .cookie(new Cookie("jsessionid","jsessionid")))
                .andReturn()
                .getResponse();

        Event savedEvent = eventService.getEventById(id);

        Assertions.assertEquals(savedEvent.getParticipants().size(),3);
    }

    @Test
    public void shouldSetCalendar() throws Exception {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        authService.login(credentials,"jsessionid");

        Event event = new Event(savedUser,"buli",new HashSet<>(),"");

        var response = mockMvc.perform(post("/events/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(event))
                        .accept("application/json")
                        .cookie(new Cookie("jsessionid","jsessionid")))
                .andReturn()
                .getResponse();
        Event savedEvent = objectMapper.readValue(response.getContentAsString(), Event.class);

        String start = LocalDateTime.now().toString();
        String end = LocalDateTime.now().plusDays(3).toString();

        mockMvc.perform(put("/events/"+savedEvent.getId()+"/add-calendar")
                .cookie(new Cookie("jsessionid","jsessionid"))
                .param("start",start)
                .param("end",end)
                .param("zone","CET")
                .accept("application/json"));

        savedEvent = eventService.getEventById(savedEvent.getId());

        Assertions.assertEquals(3,savedEvent.getCalendar().getDays().size());
    }
}
