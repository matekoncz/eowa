package com.example.eowa.service;

import com.example.eowa.model.Session;
import com.example.eowa.model.User;
import exceptions.userExceptions.UserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SessionServiceTests {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void beforeTests(){
        sessionService.deleteAllSession();
        userService.deleteAllUsers();
    }

    @Test
    public void shouldSaveSession() throws UserException {
        User user = new User("felh","asznalo1","email@gmail.com");
        userService.saveUser(user);
        Session session = new Session();
        session.setJsessionid("id");
        session.setUser(user);
        Session storedSession = sessionService.saveSession(session);
        Assertions.assertTrue(storedSession.getTimestamp()>0);
    }

    @Test
    public void shouldGetSavedSession() throws UserException {
        User user = new User("felh","asznalo2","email@gmail.com");
        userService.saveUser(user);
        Session session = new Session();
        session.setJsessionid("id");
        session.setUser(user);
        Session storedSession = sessionService.saveSession(session);

        Session inDBSession = sessionService.getSessionById("id");

        Assertions.assertEquals(storedSession.getTimestamp(),inDBSession.getTimestamp());
    }

    @Test
    public void shouldGetUserBySessionId() throws UserException {
        User user = new User("felh","asznalo3","email@gmail.com");
        User saveduser = userService.saveUser(user);
        Session session = new Session();
        session.setJsessionid("id");
        session.setUser(user);
        Session storedSession = sessionService.saveSession(session);
        Assertions.assertEquals("felh",sessionService.getUserBySessionId("id").getUsername());
    }
}
