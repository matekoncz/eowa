package com.example.eowa.controller;

import com.example.eowa.exceptions.CalendarExceptions.CalendarException;
import com.example.eowa.exceptions.authenticationExceptions.AuthenticationException;
import com.example.eowa.exceptions.authenticationExceptions.InvalidInvitationCodeException;
import com.example.eowa.exceptions.authenticationExceptions.UserIsNotEventOwnerException;
import com.example.eowa.exceptions.authenticationExceptions.UserIsNotParticipantException;
import com.example.eowa.exceptions.eventExceptions.BlueprintCannotBeAccessedException;
import com.example.eowa.exceptions.eventExceptions.EventCannotBeFinalizedException;
import com.example.eowa.exceptions.eventExceptions.EventException;
import com.example.eowa.exceptions.eventExceptions.EventIsFinalizedException;
import com.example.eowa.model.*;
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
import java.util.List;
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

    @PutMapping("/{id}/add-fields")
    public void addSelectionFieldToEvent(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @RequestBody Set<SelectionField> selectionFields
    ) throws UserIsNotEventOwnerException, EventException {
        eventService.checkIfEventIsFinalized(id);
        authService.validateEventOwner(jwt.getJsessionid(),id);
        eventService.addFieldsToEvent(id,selectionFields);
    }

    @DeleteMapping("/{id}/remove-fields")
    public void removeSelectionFieldFromToEvent(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @RequestBody Set<Long> selectionids
    ) throws UserIsNotEventOwnerException, EventException {
        eventService.checkIfEventIsFinalized(id);
        authService.validateEventOwner(jwt.getJsessionid(),id);
        eventService.removeFieldsFromEvent(id,selectionids);
    }

    @PutMapping("/{id}/fields/{fieldid}")
    public void addOptionsToField(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @PathVariable("fieldid") long fieldid,
            @RequestBody Set<Option> options
    ) throws UserIsNotParticipantException, EventException {
        boolean owner;

        try{
            authService.validateEventOwner(jwt.getJsessionid(),id);
            owner = true;
        } catch (UserIsNotEventOwnerException ignored){
            authService.validateParticipant(jwt.getJsessionid(),id);
            owner = false;
        }

        eventService.checkIfEventIsFinalized(id);

        eventService.addFieldOptions(id,options,owner);
    }

    @DeleteMapping("/{id}/fields/{fieldid}")
    public void removeOptionsFromField(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @PathVariable("fieldid") long fieldid,
            @RequestBody Set<Long> optionids
    ) throws UserIsNotParticipantException, EventException {

        boolean owner;

        try{
            authService.validateEventOwner(jwt.getJsessionid(),id);
            owner = true;
        } catch (UserIsNotEventOwnerException ignored){
            authService.validateParticipant(jwt.getJsessionid(),id);
            owner = false;
        }

        eventService.checkIfEventIsFinalized(id);

        eventService.removeFieldOptions(id,optionids,owner);
    }

    @PutMapping("/{id}/fields/{fieldid}/vote/{optionid}")
    public void voteForOption(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @PathVariable("fieldid") long fieldid,
            @PathVariable("optionid") long optionid
    ) throws UserIsNotParticipantException, EventException {
        authService.validateParticipant(jwt.getJsessionid(),id);
        eventService.checkIfEventIsFinalized(id);

        eventService.addVote(optionid,fieldid,jwt.getUser());
    }

    @DeleteMapping("/{id}/fields/{fieldid}/remove-vote/{optionid}")
    public void removeOptionVote(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @PathVariable("fieldid") long fieldid,
            @PathVariable("optionid") long optionid
    ) throws UserIsNotParticipantException, EventException {
        authService.validateParticipant(jwt.getJsessionid(),id);
        eventService.checkIfEventIsFinalized(id);

        eventService.removeVote(optionid,fieldid,jwt.getUser());
    }

    @PutMapping("/{id}/fields/{fieldid}/select/{optionid}")
    public void selectOption(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @PathVariable("fieldid") long fieldid,
            @PathVariable("optionid") long optionid
    ) throws EventException, UserIsNotEventOwnerException {
        authService.validateEventOwner(jwt.getJsessionid(),id);
        eventService.checkIfEventIsFinalized(id);

        eventService.selectOption(optionid,fieldid);
    }

    @PutMapping("/{id}/set-start-and-end")
    public void setStartAndEndTime(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @RequestParam("start") int start,
            @RequestParam("end") int end
    ) throws EventException, UserIsNotEventOwnerException {
        authService.validateEventOwner(jwt.getJsessionid(),id);
        eventService.checkIfEventIsFinalized(id);

        eventService.setStartTimeAndEndTime(id,start,end);
    }

    @DeleteMapping("/{id}/set-start-and-end")
    public void resetStartAndEnd(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id
    ) throws EventException, UserIsNotEventOwnerException {
        authService.validateEventOwner(jwt.getJsessionid(),id);
        eventService.checkIfEventIsFinalized(id);

        eventService.resetStartHourAndEndHour(id);
    }

    @PutMapping("/{id}/finalize")
    public void finalizeEvent(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id
    ) throws EventCannotBeFinalizedException, UserIsNotEventOwnerException {
        authService.validateEventOwner(jwt.getJsessionid(),id);
        eventService.finalizeEvent(id);
    }

    @DeleteMapping("/{id}/finalize")
    public void unFinalizeEvent(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id
    ) throws UserIsNotEventOwnerException {
        authService.validateEventOwner(jwt.getJsessionid(),id);
        eventService.unFinalizeEvent(id);
    }

    @GetMapping("/{id}/get-best-time-intervals")
    public void getBestTimeIntervals(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @RequestParam("participants") int minParticipants,
            @RequestParam("length") int minLength,
            @RequestBody() Set<Opinion.UserOpinion> allowedOpinions,
            HttpServletResponse response
    ) throws UserIsNotEventOwnerException, IOException {
        authService.validateEventOwner(jwt.getJsessionid(),id);
        List<MomentDetails> details = eventService.getBestTimeIntervals(id,minParticipants,minLength,allowedOpinions);
        response.getWriter().print(objectMapper.writeValueAsString(details));
    }

    @PostMapping("/{id}/create-blueprint")
    public void createBlueprint(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @RequestParam("name") String name
    ) throws UserIsNotParticipantException {
        authService.validateParticipant(jwt.getJsessionid(),id);
        User user = authService.getUserBySessionId(jwt.getJsessionid());
        eventService.createEventBlueprint(id,name,user);
    }

    @PutMapping("/{id}/add-from-blueprint/{bpid}")
    public void addFieldsFromBluePrint(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            @PathVariable("id") long id,
            @PathVariable("bpid") long blueprintId
    ) throws UserIsNotEventOwnerException, EventIsFinalizedException, BlueprintCannotBeAccessedException {
        eventService.checkIfEventIsFinalized(id);
        eventService.checkIfUserHasRightsToBlueprint(blueprintId,jwt.getUser());
        authService.validateEventOwner(jwt.getJsessionid(),id);
        eventService.addFieldsFromBluePrint(id,blueprintId);
    }
}
