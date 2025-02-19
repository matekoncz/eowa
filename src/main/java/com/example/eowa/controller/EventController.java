package com.example.eowa.controller;

import com.example.eowa.exceptions.CalendarExceptions.CalendarException;
import com.example.eowa.exceptions.authenticationExceptions.AuthenticationException;
import com.example.eowa.exceptions.authenticationExceptions.InvalidInvitationCodeException;
import com.example.eowa.exceptions.authenticationExceptions.UserIsNotEventOwnerException;
import com.example.eowa.exceptions.authenticationExceptions.UserIsNotParticipantException;
import com.example.eowa.model.Event;
import com.example.eowa.model.Opinion;
import com.example.eowa.model.User;
import com.example.eowa.model.WebToken;
import com.example.eowa.service.AuthService;
import com.example.eowa.service.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    private final AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public EventController(EventService eventService, AuthService authService) {
        objectMapper.registerModule(new JavaTimeModule());
        this.eventService = eventService;
        this.authService = authService;
    }

    @PostMapping("/create")
    public void createEvent(
            @RequestBody Event event,
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            HttpServletResponse response) throws AuthenticationException, IOException {
        authService.validateSession(jwt.getJsessionid());
        User owner = authService.getUserBySessionId(jwt.getJsessionid());
        event.setOwner(owner);
        event.generateInvitationCode();
        Event savedevent = eventService.saveEvent(event);
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().print(objectMapper.writeValueAsString(savedevent));
    }

    @GetMapping("/{id}")
    public void getEventById(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long eventId,
            HttpServletResponse response) throws IOException, AuthenticationException {
        authService.validateSession(jwt.getJsessionid());
        authService.validateParticipant(jwt.getJsessionid(),eventId);
        Event event = eventService.getEventById(eventId);
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().print(objectMapper.writeValueAsString(event));
    }

    @GetMapping("/currentuser-events")
    public void getCurrentUserEvents(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            HttpServletResponse response) throws AuthenticationException, IOException {
        authService.validateSession(jwt.getJsessionid());
        User currentUser = authService.getUserBySessionId(jwt.getJsessionid());
        Set<Event> events = currentUser.getEvents();
        response.getWriter().print(objectMapper.writeValueAsString(events));
    }

    @DeleteMapping("/{id}")
    public void deleteEventById(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            HttpServletResponse response) throws AuthenticationException {
        authService.validateSession(jwt.getJsessionid());
        authService.validateEventOwner(jwt.getJsessionid(), id);
        eventService.deleteEventById(id);
        response.setStatus(HttpStatus.OK.value());
    }

    @Transactional
    @PutMapping("/{id}/add-users")
    public void addParticipants(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @RequestBody Set<User> participants,
            HttpServletResponse response) throws AuthenticationException {
        authService.validateSession(jwt.getJsessionid());
        authService.validateEventOwner(jwt.getJsessionid(), id);
        eventService.getEventById(id).addALlParticipant(participants);
        response.setStatus(HttpStatus.OK.value());
    }

    @PutMapping("/{id}/add-calendar")
    public void addCalendar(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @RequestParam("start") String startString,
            @RequestParam("end") String endString,
            @RequestParam("zone") String zoneId,
            HttpServletResponse response) throws AuthenticationException, CalendarException {
        authService.validateSession(jwt.getJsessionid());
        authService.validateEventOwner(jwt.getJsessionid(),id);
        LocalDateTime startTime = LocalDateTime.parse(startString);
        LocalDateTime endTime = LocalDateTime.parse(endString);
        eventService.setEventCalendar(id,zoneId,startTime,endTime);
        response.setStatus(HttpStatus.OK.value());
    }

    @PutMapping("/{id}/set-unavailable-days")
    public void setUnavailableDays(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @RequestBody Set<Integer> serialNumbers,
            HttpServletResponse response) throws UserIsNotEventOwnerException {
        authService.validateEventOwner(jwt.getJsessionid(),id);
        eventService.setUnavailableDays(id,serialNumbers);
        response.setStatus(HttpStatus.OK.value());
    }

    @PutMapping("/{id}/set-unavailable-hours")
    public void setUnavailableHours(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @RequestBody Set<Integer> serialNumbers,
            HttpServletResponse response) throws UserIsNotEventOwnerException {
        authService.validateEventOwner(jwt.getJsessionid(),id);
        eventService.setUnavailableHours(id,serialNumbers);
        response.setStatus(HttpStatus.OK.value());
    }

    @PutMapping("/{id}/set-unavailable-hours-periodically")
    public void setUnavailableHoursPeriodically(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @RequestParam("period") int period,
            @RequestBody Set<Integer> hourNumbers,
            HttpServletResponse response) throws UserIsNotEventOwnerException {
        authService.validateEventOwner(jwt.getJsessionid(),id);
        eventService.setUnavailableHoursPeriodically(id,hourNumbers,period);
        response.setStatus(HttpStatus.OK.value());
    }

    @PutMapping("/{id}/set-user-opinion")
    public void setUserOpinion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @RequestParam("opinion") Opinion.UserOpinion userOpinion,
            @RequestBody Set<Integer> hourSerials,
            HttpServletResponse response) throws UserIsNotParticipantException {
        authService.validateParticipant(jwt.getJsessionid(),id);
        User user = authService.getUserBySessionId(jwt.getJsessionid());
        eventService.setUserOpinion(id,hourSerials,user,userOpinion);
        response.setStatus(HttpStatus.OK.value());
    }

    @PutMapping("/{id}/remove-user-opinion")
    public void removeUserOpinion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @RequestBody Set<Integer> hourSerials,
            HttpServletResponse response) throws UserIsNotParticipantException {
        authService.validateParticipant(jwt.getJsessionid(),id);
        User user = authService.getUserBySessionId(jwt.getJsessionid());
        eventService.removeUserOpinion(id,hourSerials,user);
        response.setStatus(HttpStatus.OK.value());
    }

    @PutMapping("/join-event")
    public void joinEventWithInvitationCode(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @RequestParam("invitation") String invitation,
            HttpServletResponse response) throws InvalidInvitationCodeException {
        User user = authService.getUserBySessionId(jwt.getJsessionid());
        eventService.joinEventWithInvitationCode(user,invitation);
        response.setStatus(HttpStatus.OK.value());
    }
}
