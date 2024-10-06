package com.example.eowa.service;

import com.example.eowa.model.Credentials;
import com.example.eowa.model.Session;
import com.example.eowa.model.User;
import exceptions.authenticationExceptions.AuthenticationException;
import exceptions.authenticationExceptions.InvalidSessionException;
import exceptions.authenticationExceptions.UserDoesNotExistException;
import exceptions.authenticationExceptions.WrongPasswordException;
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
}
