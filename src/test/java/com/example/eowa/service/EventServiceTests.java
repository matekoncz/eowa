package com.example.eowa.service;

import com.example.eowa.model.Event;
import com.example.eowa.model.User;
import com.example.eowa.exceptions.userExceptions.UserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EventServiceTests {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

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
        Event event = new Event(savedUser,"kertiparti",new HashSet<User>());
        Event savedEvent = eventService.saveEvent(event);
        Assertions.assertNotNull(savedEvent);
    }

    @Test
    public void shouldGetEventById() throws UserException {
        User user = new User("felh","asznalo1","email@gmail.com");
        User savedUser = userService.saveUser(user);
        Event event = new Event(savedUser,"kertiparti",new HashSet<User>());
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

        Event event = new Event(savedUser,"kertiparti",participants);
        Event savedEvent = eventService.saveEvent(event);

        Assertions.assertEquals(savedEvent.getParticipants().size(),3);
    }
}
