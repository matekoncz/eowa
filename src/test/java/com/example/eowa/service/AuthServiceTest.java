package com.example.eowa.service;

import com.example.eowa.EowaIntegrationTest;
import com.example.eowa.exceptions.authenticationExceptions.*;
import com.example.eowa.exceptions.userExceptions.UserDoesNotExistException;
import com.example.eowa.exceptions.userExceptions.UserException;
import com.example.eowa.model.Credentials;
import com.example.eowa.model.Event;
import com.example.eowa.model.Session;
import com.example.eowa.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
public class AuthServiceTest extends EowaIntegrationTest {

    private static final long ONE_HOUR_IN_MILLIS = 60*60*1000;

    @Test
    public void shouldLoginUser() throws UserException, AuthenticationException {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");
        String jsessionid = authService.login(credentials);
        Session storedSession = sessionService.getSessionById(jsessionid);
        Assertions.assertEquals(storedSession.getUser().getUsername(),savedUser.getUsername());
    }

    @Test
    public void shouldNotLoginUserWithWrongPassword() throws UserException {
        userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo2");
        Assertions.assertThrows(WrongPasswordException.class, ()-> authService.login(credentials));
    }

    @Test
    public void shouldThrowExceptionIfUserDoesNotExist(){
        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo2");
        Assertions.assertThrows(UserDoesNotExistException.class, ()-> authService.login(credentials));
    }

    @Test
    public void shouldLogoutUser() throws UserException, AuthenticationException {
        userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");
        String jsessionid = authService.login(credentials);
        Session storedSession = sessionService.getSessionById(jsessionid);
        authService.logout(storedSession.getJsessionid());
        Assertions.assertNull(sessionService.getSessionById(storedSession.getJsessionid()));
    }

    @Test
    public void shouldThrowExceptionIfSessionDoesNotExist(){
        Assertions.assertThrows(InvalidSessionException.class,()-> authService.validateSession("session"));
    }

    @Test
    public void shouldValidateSession() throws UserException, AuthenticationException {
        userService.saveUser(new User("felh", "asznalo1", "email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");
        String jsessionid = authService.login(credentials);
        Session storedSession = sessionService.getSessionById(jsessionid);
        authService.validateSession(storedSession.getJsessionid());
    }

    @Test
    public void shouldThrowExceptionIfSessionExpired() throws UserException, AuthenticationException {
        userService.saveUser(new User("felh", "asznalo1", "email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");
        String jsessionid = authService.login(credentials);
        Session storedSession = sessionService.getSessionById(jsessionid);
        storedSession.setTimestamp(System.currentTimeMillis()-ONE_HOUR_IN_MILLIS*20);
        sessionService.updateSession(storedSession);
        Assertions.assertThrows(InvalidSessionException.class, ()-> authService.validateSession(storedSession.getJsessionid()));
    }

    @Test
    public void shouldValidateEventOwnerBySessionPositive() throws UserException, AuthenticationException {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));

        Event event = new Event(savedUser,"kertiparti",new HashSet<>(),"");
        Event savedEvent = eventService.saveEvent(event);

        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");

        String jsessionid = authService.login(credentials);
        Session storedSession = sessionService.getSessionById(jsessionid);

        authService.validateEventOwner(storedSession.getJsessionid(), savedEvent.getId());
    }

    @Test
    public void shouldValidateEventOwnerBySessionNegative() throws UserException, AuthenticationException {
        userService.saveUser(new User("felh", "asznalo1", "email@gmail.com"));

        User owner = userService.saveUser(new User("felh2","asznalo2","freemail@gmail.com"));
        Event event = new Event(owner,"kertiparti",new HashSet<>(),"");
        Event savedEvent = eventService.saveEvent(event);

        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");

        String jsessionid = authService.login(credentials);
        Session storedSession = sessionService.getSessionById(jsessionid);

        Assertions.assertThrows(UserIsNotEventOwnerException.class,
                ()-> authService.validateEventOwner(storedSession.getJsessionid(), savedEvent.getId()));

    }

    @Test
    public void shouldValidateEventParticipantBySessionPositive() throws UserException, AuthenticationException {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));

        User owner = userService.saveUser(new User("felh2","asznalo2","freemail@gmail.com"));

        Set<User> participants = new HashSet<>();
        participants.add(savedUser);

        Event event = new Event(owner,"kertiparti",participants,"");
        Event savedEvent = eventService.saveEvent(event);

        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");

        String jsessionid = authService.login(credentials);
        Session storedSession = sessionService.getSessionById(jsessionid);

        authService.validateParticipant(storedSession.getJsessionid(), savedEvent.getId());
    }

    @Test
    public void shouldValidateEventParticipantBySessionNegative() throws UserException, AuthenticationException {
        userService.saveUser(new User("felh", "asznalo1", "email@gmail.com"));

        User owner = userService.saveUser(new User("felh2","asznalo2","freemail@gmail.com"));

        Event event = new Event(owner,"kertiparti",new HashSet<>(),"");
        Event savedEvent = eventService.saveEvent(event);

        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");

        String jsessionid = authService.login(credentials);
        Session storedSession = sessionService.getSessionById(jsessionid);


        Assertions.assertThrows(UserIsNotParticipantException.class,
                ()-> authService.validateParticipant(storedSession.getJsessionid(), savedEvent.getId()));

    }
}
