package com.example.eowa.service;

import com.example.eowa.exceptions.authenticationExceptions.*;
import com.example.eowa.exceptions.userExceptions.UserDoesNotExistException;
import com.example.eowa.model.Credentials;
import com.example.eowa.model.Session;
import com.example.eowa.model.User;
import com.example.eowa.model.Event;
import com.example.eowa.exceptions.userExceptions.UserException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Transactional
@Service
public class AuthService {

    @Value("${session.durationInHours}")
    private int sessionDuration;

    private static final long ONE_HOUR_IN_MILLIS = 60*60*1000;

    private final UserService userService;

    private final SessionService sessionService;

    private final EventService eventService;

    public AuthService(UserService userService, SessionService sessionService, EventService eventService) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.eventService = eventService;
    }

    public User signUpUser(User user) throws UserException {
        return userService.saveUser(user);
    }

    public String login(Credentials credentials) throws AuthenticationException, UserException {
        authenticate(credentials);
        User user = userService.getUserByUsername(credentials.getUsername());
        Session session = user.getSession();
        if(session != null){
            try{
                validateSession(session.getJsessionid());
                return session.getJsessionid();
            } catch (AuthenticationException ignored){
                sessionService.flush();
            }
        }
        Session newsession = new Session();
        newsession.setUser(user);
        newsession.setJsessionid(UUID.randomUUID().toString().replace("-",""));
        sessionService.saveSession(newsession);
        return newsession.getJsessionid();
    }

    public void logout(String jsessionid){
            sessionService.removeSessionById(jsessionid);
    }

    public void authenticate(Credentials credentials) throws AuthenticationException, UserDoesNotExistException {
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
            sessionService.removeSessionById(jsessionid);
            throw new InvalidSessionException();
        }
    }

    public void validateParticipant(String sessionid, long eventid) throws UserIsNotParticipantException {
        Event event = eventService.getEventById(eventid);
        User currentuser = sessionService.getUserBySessionId(sessionid);

        if(!event.getParticipants().contains(currentuser)){
            throw new UserIsNotParticipantException();
        }
    }

    public void validateEventOwner(String sessionid, long eventid) throws UserIsNotEventOwnerException {
        Event event = eventService.getEventById(eventid);
        User currentuser = sessionService.getUserBySessionId(sessionid);

        if(!event.getOwner().equals(currentuser)){
            throw new UserIsNotEventOwnerException();
        }
    }

    public User getUserBySessionId(String jsessionid) {
        return sessionService.getUserBySessionId(jsessionid);
    }
}
