package com.example.eowa.service;

import com.example.eowa.EowaIntegrationTest;
import com.example.eowa.exceptions.userExceptions.UserException;
import com.example.eowa.model.Session;
import com.example.eowa.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
public class SessionServiceTest extends EowaIntegrationTest {

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
        userService.saveUser(user);
        Session session = new Session();
        session.setJsessionid("id");
        session.setUser(user);
        sessionService.saveSession(session);
        Assertions.assertEquals("felh",sessionService.getUserBySessionId("id").getUsername());
    }

    @Test
    public void shouldDeleteSession() throws UserException {
        User user = new User("felh","asznalo3","email@gmail.com");
        userService.saveUser(user);
        Session session = new Session();
        session.setJsessionid("id");
        session.setUser(user);
        sessionService.saveSession(session);

        Assertions.assertNotNull(userService.getUserByUsername("felh"));

        sessionService.removeSessionById(session.getJsessionid());

        Assertions.assertNull(sessionService.getSessionById("id"));
        Assertions.assertNotNull(userService.getUserByUsername("felh"));
    }
}
