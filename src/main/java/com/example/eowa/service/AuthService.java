package com.example.eowa.service;

import com.example.eowa.model.Credentials;
import com.example.eowa.model.Session;
import com.example.eowa.model.User;
import exceptions.authenticationExceptions.AuthenticationException;
import exceptions.authenticationExceptions.InvalidSessionException;
import exceptions.authenticationExceptions.UserDoesNotExistException;
import exceptions.authenticationExceptions.WrongPasswordException;
import exceptions.userExceptions.PasswordTooShortException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class AuthService {

    @Value("${session.durationInHours}")
    private int sessionDuration;

    private static final long ONE_HOUR_IN_MILLIS = 60*60*1000;

    private final UserService userService;

    private final SessionService sessionService;

    public AuthService(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    public String login(Credentials credentials,String jsessionid) throws AuthenticationException{
        authenticate(credentials);
        User user = userService.getUserByUsername(credentials.getUsername());
        Session session = user.getSession();
        if(session == null){
            session = new Session();
            session.setUser(user);
            session.setJsessionid(jsessionid);
            sessionService.saveSession(session);
        }
        return session.getJsessionid();
    }

    public void logout(String jsessionid){
        sessionService.deleteSessionById(jsessionid);
    }

    public void authenticate(Credentials credentials) throws AuthenticationException {
        User user = userService.getUserByUsername(credentials.getUsername());
        if(user==null){
            throw new UserDoesNotExistException();
        }
        if(!userService.isPasswordCorrect(credentials.getPassword(),user)){
            throw new WrongPasswordException();
        }
    }

    public void validateSession(String jsessionid) throws AuthenticationException{
        Session session = sessionService.getSessionById(jsessionid);
        if(session == null){
            throw new InvalidSessionException();
        }
        long now = System.currentTimeMillis();
        if((now - session.getTimestamp())>sessionDuration*ONE_HOUR_IN_MILLIS){
            sessionService.deleteSessionById(jsessionid);
            throw new InvalidSessionException();
        }
    }
}
