package com.example.eowa.service;

import com.example.eowa.exceptions.CalendarExceptions.CalendarException;
import com.example.eowa.exceptions.authenticationExceptions.*;
import com.example.eowa.model.*;
import com.example.eowa.exceptions.userExceptions.UserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

@ActiveProfiles("test")
@SpringBootTest
public class AuthServiceTests {

    private static final long ONE_HOUR_IN_MILLIS = 60*60*1000;

    @Autowired
    private EventService eventService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;
    private final Calendar calendar = new Calendar();

    @BeforeEach
    public void beforeTests(){
        eventService.deleteAllEvent();
        sessionService.deleteAllSession();
        userService.deleteAllUsers();
    }

    @Test
    public void shouldLoginUser() throws UserException, AuthenticationException {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");
        authService.login(credentials,"session");
        Session storedSession = sessionService.getSessionById("session");
        Assertions.assertEquals(storedSession.getUser().getUsername(),savedUser.getUsername());
    }

    @Test
    public void shouldNotLoginUserWithWrongPassword() throws UserException, AuthenticationException {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo2");
        Assertions.assertThrows(WrongPasswordException.class, ()->{
            authService.login(credentials,"session");
        });
    }

    @Test
    public void shouldThrowExceptionIfUserDoesNotExist(){
        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo2");
        Assertions.assertThrows(UserDoesNotExistException.class, ()->{
            authService.login(credentials,"session");
        });
    }

    @Test
    public void shouldLogoutUser() throws UserException, AuthenticationException {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");
        authService.login(credentials,"session");
        Session storedSession = sessionService.getSessionById("session");
        authService.logout(storedSession.getJsessionid());
        Assertions.assertNull(sessionService.getSessionById(storedSession.getJsessionid()));
    }

    @Test
    public void shouldThrowExceptionIfSessionDoesNotExist(){
        Assertions.assertThrows(InvalidSessionException.class,()->{
            authService.validateSession("session");
        });
    }

    @Test
    public void shouldValidateSession() throws UserException, AuthenticationException {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");
        authService.login(credentials,"session");
        Session storedSession = sessionService.getSessionById("session");
        authService.validateSession(storedSession.getJsessionid());
    }

    @Test
    public void shouldThrowExceptionIfSessionExpired() throws UserException, AuthenticationException {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");
        authService.login(credentials,"session");
        Session storedSession = sessionService.getSessionById("session");
        storedSession.setTimestamp(System.currentTimeMillis()-ONE_HOUR_IN_MILLIS*20);
        sessionService.updateSession(storedSession);
        Assertions.assertThrows(InvalidSessionException.class, ()->{
            authService.validateSession(storedSession.getJsessionid());
        });
    }

    @Test
    public void shouldValidateEventOwnerBySessionPositive() throws UserException, AuthenticationException {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));

        Event event = new Event(savedUser,"kertiparti",new HashSet<>(),"");
        Event savedEvent = eventService.saveEvent(event);

        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");

        authService.login(credentials,"session");
        Session storedSession = sessionService.getSessionById("session");

        authService.validateEventOwner(storedSession.getJsessionid(), savedEvent.getId());
    }

    @Test
    public void shouldValidateEventOwnerBySessionNegative() throws UserException, AuthenticationException {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));

        User owner = userService.saveUser(new User("felh2","asznalo2","freemail@gmail.com"));
        Event event = new Event(owner,"kertiparti",new HashSet<>(),"");
        Event savedEvent = eventService.saveEvent(event);

        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");

        authService.login(credentials,"session");
        Session storedSession = sessionService.getSessionById("session");

        Assertions.assertThrows(UserIsNotEventOwnerException.class,()->{
            authService.validateEventOwner(storedSession.getJsessionid(), savedEvent.getId());
        });

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

        authService.login(credentials,"session");
        Session storedSession = sessionService.getSessionById("session");

        authService.validateParticipant(storedSession.getJsessionid(), savedEvent.getId());
    }

    @Test
    public void shouldValidateEventParticipantBySessionNegative() throws UserException, AuthenticationException {
        User savedUser = userService.saveUser(new User("felh","asznalo1","email@gmail.com"));

        User owner = userService.saveUser(new User("felh2","asznalo2","freemail@gmail.com"));

        Event event = new Event(owner,"kertiparti",new HashSet<>(),"");
        Event savedEvent = eventService.saveEvent(event);

        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");

        authService.login(credentials,"session");
        Session storedSession = sessionService.getSessionById("session");


        Assertions.assertThrows(UserIsNotParticipantException.class,()->{
            authService.validateParticipant(storedSession.getJsessionid(), savedEvent.getId());
        });

    }
}
