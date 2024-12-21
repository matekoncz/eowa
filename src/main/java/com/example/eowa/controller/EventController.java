package com.example.eowa.controller;

import com.example.eowa.exceptions.CalendarExceptions.CalendarException;
import com.example.eowa.exceptions.authenticationExceptions.AuthenticationException;
import com.example.eowa.exceptions.authenticationExceptions.InvalidInvitationCodeException;
import com.example.eowa.exceptions.authenticationExceptions.UserIsNotEventOwnerException;
import com.example.eowa.exceptions.authenticationExceptions.UserIsNotParticipantException;
import com.example.eowa.model.Event;
import com.example.eowa.model.Opinion;
import com.example.eowa.model.User;
import com.example.eowa.service.AuthService;
import com.example.eowa.service.EventService;
import com.example.eowa.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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
        this.eventService = eventService;
        this.authService = authService;
    }

    @PostMapping("/create")
    public HttpServletResponse createEvent(
            @RequestBody Event event,
            @CookieValue("jsessionid") String jsessionid,
            HttpServletResponse response) throws AuthenticationException, IOException {
        authService.validateSession(jsessionid);
        User owner = authService.getUserBySessionId(jsessionid);
        event.setOwner(owner);
        Event savedevent = eventService.saveEvent(event);
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().print(objectMapper.writeValueAsString(savedevent));
        return response;
    }

    @GetMapping("/{id}")
    public HttpServletResponse getEventById(
            @CookieValue("jsessionid") String jsessionid,
            @PathVariable("id") long eventId,
            HttpServletResponse response) throws IOException, AuthenticationException {
        authService.validateSession(jsessionid);
        authService.validateParticipant(jsessionid,eventId);
        Event event = eventService.getEventById(eventId);
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().print(objectMapper.writeValueAsString(event));
        return response;
    }

    @GetMapping("/currentuser-events")
    public HttpServletResponse getCurrentUserEvents(
            @CookieValue("jsessionid") String jsessionid,
            HttpServletResponse response) throws AuthenticationException, IOException {
        authService.validateSession(jsessionid);
        User currentUser = authService.getUserBySessionId(jsessionid);
        Set<Event> events = currentUser.getEvents();
        response.getWriter().print(objectMapper.writeValueAsString(events));
        return response;
    }

    @DeleteMapping("/{id}")
    public HttpServletResponse deleteEventById(
            @CookieValue("jsessionid") String jsessionid,
            @PathVariable("id") long id,
            HttpServletResponse response) throws AuthenticationException {
        authService.validateSession(jsessionid);
        authService.validateEventOwner(jsessionid, id);
        eventService.deleteEventById(id);
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    @Transactional
    @PutMapping("/{id}/add-users")
    public HttpServletResponse addParticipants(
            @CookieValue("jsessionid") String jsessionid,
            @PathVariable("id") long id,
            @RequestBody Set<User> participants,
            HttpServletResponse response) throws AuthenticationException {
        authService.validateSession(jsessionid);
        authService.validateEventOwner(jsessionid, id);
        eventService.getEventById(id).addALlParticipant(participants);
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    @PutMapping("/{id}/add-calendar")
    public HttpServletResponse addCalendar(
            @CookieValue("jsessionid") String jsessionid,
            @PathVariable("id") long id,
            @RequestParam("start") String startString,
            @RequestParam("end") String endString,
            @RequestParam("zone") String zoneId,
            HttpServletResponse response) throws UserIsNotEventOwnerException, CalendarException {
        authService.validateEventOwner(jsessionid,id);
        LocalDateTime startTime = LocalDateTime.parse(startString);
        LocalDateTime endTime = LocalDateTime.parse(endString);
        eventService.setEventCalendar(id,zoneId,startTime,endTime);
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    @PutMapping("/{id}/set-unavailable-days")
    public HttpServletResponse setUnavailableDays(
            @CookieValue("jsessionid") String jsessionid,
            @PathVariable("id") long id,
            @RequestBody Set<Integer> serialNumbers,
            HttpServletResponse response) throws UserIsNotEventOwnerException {
        authService.validateEventOwner(jsessionid,id);
        eventService.setUnavailableDays(id,serialNumbers);
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    @PutMapping("/{id}/set-unavailable-hours")
    public HttpServletResponse setUnavailableHours(
            @CookieValue("jsessionid") String jsessionid,
            @PathVariable("id") long id,
            @RequestBody Set<Integer> serialNumbers,
            HttpServletResponse response) throws UserIsNotEventOwnerException {
        authService.validateEventOwner(jsessionid,id);
        eventService.setUnavailableHours(id,serialNumbers);
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    @PutMapping("/{id}/set-unavailable-hours-periodically")
    public HttpServletResponse setUnavailableHoursPeriodically(
            @CookieValue("jsessionid") String jsessionid,
            @PathVariable("id") long id,
            @RequestParam("period") int period,
            @RequestBody Set<Integer> hourNumbers,
            HttpServletResponse response) throws UserIsNotEventOwnerException {
        authService.validateEventOwner(jsessionid,id);
        eventService.setUnavailableHoursPeriodically(id,hourNumbers,period);
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    @PutMapping("/{id}/set-user-opinion")
    public HttpServletResponse setUserOpinion(
            @CookieValue("jsessionid") String jsessionid,
            @PathVariable("id") long id,
            @RequestParam("opinion") Opinion.UserOpinion userOpinion,
            @RequestBody Set<Integer> hourSerials,
            HttpServletResponse response) throws UserIsNotParticipantException {
        authService.validateParticipant(jsessionid,id);
        User user = authService.getUserBySessionId(jsessionid);
        eventService.setUserOpinion(id,hourSerials,user,userOpinion);
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    @PutMapping("/{id}/remove-user-opinion")
    public HttpServletResponse removeUserOpinion(
            @CookieValue("jsessionid") String jsessionid,
            @PathVariable("id") long id,
            @RequestBody Set<Integer> hourSerials,
            HttpServletResponse response) throws UserIsNotParticipantException {
        authService.validateParticipant(jsessionid,id);
        User user = authService.getUserBySessionId(jsessionid);
        eventService.removeUserOpinion(id,hourSerials,user);
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    @PutMapping("/join-event")
    public HttpServletResponse joinEventWithInvitationCode(
            @CookieValue("jsessionid") String jsessionid,
            @RequestParam("invitation") String invitation,
            HttpServletResponse response) throws InvalidInvitationCodeException {
        User user = authService.getUserBySessionId(jsessionid);
        eventService.joinEventWithInvitationCode(user,invitation);
        response.setStatus(HttpStatus.OK.value());
        return response;
    }
}
