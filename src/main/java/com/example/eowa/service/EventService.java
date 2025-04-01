package com.example.eowa.service;

import com.example.eowa.exceptions.CalendarExceptions.CalendarException;
import com.example.eowa.exceptions.authenticationExceptions.InvalidInvitationCodeException;
import com.example.eowa.exceptions.eventExceptions.BlueprintCannotBeAccessedException;
import com.example.eowa.exceptions.eventExceptions.EventCannotBeFinalizedException;
import com.example.eowa.exceptions.eventExceptions.EventIsFinalizedException;
import com.example.eowa.model.*;
import com.example.eowa.repository.EventBluePrintRepository;
import com.example.eowa.repository.EventRepository;
import com.example.eowa.repository.OptionRepository;
import com.example.eowa.repository.SelectionFieldRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.eowa.service.HTMLWriter.*;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    private final SelectionFieldRepository selectionFieldRepository;

    private final OptionRepository optionRepository;

    private final CalendarService calendarService;

    private final EventBluePrintRepository eventBluePrintRepository;

    private final MailService mailService;

    public EventService(EventRepository eventRepository, SelectionFieldRepository selectionFieldRepository, OptionRepository optionRepository, CalendarService calendarService, EventBluePrintRepository eventBluePrintRepository, MailService mailService) {
        this.eventRepository = eventRepository;
        this.selectionFieldRepository = selectionFieldRepository;
        this.optionRepository = optionRepository;
        this.calendarService = calendarService;
        this.eventBluePrintRepository = eventBluePrintRepository;
        this.mailService = mailService;
    }

    public Event saveEvent(Event event) {
        event.addParticipant(event.getOwner());
        if(event.getSelectionFields() != null){
            Set<SelectionField> savedFields = new HashSet<>(event.getSelectionFields().stream().map((field)->{
                field.setOptions(new HashSet<>(field.getOptions().stream().map((option -> optionRepository.save(option))).toList()));
                return selectionFieldRepository.save(field);
            }).toList());
            event.setSelectionFields(savedFields);
        }
        return eventRepository.save(event);
    }

    public void setEventCalendar(long eventId, String zoneId, LocalDateTime startTime, LocalDateTime endTime) throws CalendarException {
        Calendar calendar = calendarService.createCalendar(zoneId, startTime, endTime);
        Event event = getEventById(eventId);
        event.setCalendar(calendar);
    }

    public void deleteEventById(Long id) {
        eventRepository.deleteById(id);
    }

    public void deleteAllEvent() {
        eventBluePrintRepository.deleteAll();
        optionRepository.deleteAll();
        selectionFieldRepository.deleteAll();
        calendarService.deleteAllCalendarData();
        eventRepository.deleteAll();
    }

    public void setUnavailableDays(long id, Set<Integer> serialNumbers) {
        Calendar calendar = getEventById(id).getCalendar();
        if(calendar == null){
            return;
        }
        calendarService.setUnavailableDays(calendar, serialNumbers);
    }

    public void setUnavailableHours(long id, Set<Integer> serialNumbers) {
        Calendar calendar = getEventById(id).getCalendar();
        if(calendar == null){
            return;
        }
        calendarService.setUnavailableHours(calendar, serialNumbers);
    }

    private void setUnavailableHoursDaily(long id, Set<Integer> hourNumbers){
        setUnavailableHoursPeriodically(id,hourNumbers, CalendarService.Period.DAILY);
    }

    private void setUnavailableHoursWeekly(long id, Set<Integer> hourNumbers){
        setUnavailableHoursPeriodically(id,hourNumbers, CalendarService.Period.WEEKLY);
    }

    public void setUnavailableHoursPeriodically(long id, Set<Integer> hourNumbers, int period){
        Calendar calendar = getEventById(id).getCalendar();
        if(calendar == null){
            return;
        }
        calendarService.setUnavailableHoursPeriodically(calendar,hourNumbers,period);
    }

    public void setUserOpinion(long id, Set<Integer> hourSerials, User user, Opinion.UserOpinion userOpinion){
        Calendar calendar = getEventById(id).getCalendar();
        if(calendar == null){
            return;
        }
        calendarService.setUserOpinion(calendar,user,hourSerials,userOpinion);
    }

    public void removeUserOpinion(long id, Set<Integer> hourSerials, User user){
        Calendar calendar = getEventById(id).getCalendar();
        if(calendar == null){
            return;
        }
        calendarService.removeUserOpinion(calendar,user,hourSerials);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public SelectionField getSelectionFieldById(Long id) {
        return selectionFieldRepository.findById(id).orElse(null);
    }

    public Option getOptionById(Long id) {
        return optionRepository.findById(id).orElse(null);
    }

    public EventBlueprint getEventBlueprintById(Long id) {
        return eventBluePrintRepository.findById(id).orElse(null);
    }

    public EventBlueprint getEventBlueprintByName(String name) {
        return eventBluePrintRepository.findEventBlueprintByName(name);
    }

    public Event joinEventWithInvitationCode(User user, String invitationCode) throws InvalidInvitationCodeException {
        Event event = eventRepository.findEventByInvitationCode(invitationCode);
        if(event == null){
            throw new InvalidInvitationCodeException();
        }
        if(!event.getParticipants().contains(user)){
            event.addParticipant(user);
        }
        return event;
    }

    public void addFieldsToEvent(long id,Set<SelectionField> selectionFields){
        Event event = getEventById(id);
        event.addSelectionFields(selectionFields.stream().map((field)-> {
            field.setOptions(field.getOptions().stream().map(optionRepository::save).collect(Collectors.toSet()));
            return selectionFieldRepository.save(field);
        }).collect(Collectors.toSet()));
    }

    public void removeFieldsFromEvent(long id, Set<Long> selectionFieldIds){
            Event event = getEventById(id);
        Set<SelectionField> selectionFields = selectionFieldIds
                .stream()
                .map(this::getSelectionFieldById)
                .collect(Collectors.toSet());
        event.removeSelectionFields(selectionFields);
        eventRepository.save(event);
        selectionFields.forEach(field->{
            field.getOptions().forEach(option -> optionRepository.deleteById(option.getId()));
            selectionFieldRepository.deleteById(field.getId());
        });
    }

    public void addFieldOptions(long selectionId, Set<Option> options, boolean owner){
        SelectionField selectionField = getSelectionFieldById(selectionId);
        if(owner || selectionField.isOpenForModification()){
            options.forEach(option -> {
                optionRepository.save(option);
            });
            selectionField.addOptions(options);
        }
    }

    public void removeFieldOptions(long selectionId, Set<Long> optionids, boolean owner){
        SelectionField selectionField = getSelectionFieldById(selectionId);
        if(owner || selectionField.isOpenForModification()){
            Set<Option> options = optionids.stream().map(this::getOptionById).collect(Collectors.toSet());
            selectionField.removeOptions(options);
        }
    }

    public void addVote(long optionid,long fieldid, User user){
        Option option = getOptionById(optionid);
        SelectionField field = getSelectionFieldById(fieldid);
        if(field.isSelectedByPoll()){
            option.addVoter(user);
            selectMostVoted(fieldid);
        }
    }

    public void removeVote(long optionid, long fieldid, User user){
        Option option = getOptionById(optionid);
        SelectionField field = getSelectionFieldById(fieldid);
        if(field.isSelectedByPoll()){
            option.removeVoter(user);
            selectMostVoted(fieldid);
        }
    }

    public void selectOption(long optionid, long fieldid){
        Set<Option> options = getSelectionFieldById(fieldid).getOptions();
        options.forEach(option -> option.setSelected(false));
        Option selected = getOptionById(optionid);
        selected.setSelected(true);
    }

    public void selectMostVoted(long fieldid){
        Set<Option> options = getSelectionFieldById(fieldid).getOptions();
        options.forEach(option -> option.setSelected(false));
        Option winner = options.stream().max(Comparator.comparingInt(o -> o.getVoters().size())).get();
        winner.setSelected(true);
    }

    public EventBlueprint createEventBlueprint(long id, String name,User user) {
        Event event = getEventById(id);
        EventBlueprint blueprint = new EventBlueprint();
        blueprint.setName(name);
        blueprint.setInsertUser(user);

        Set<SelectionField> fields = event.getSelectionFields()
                .stream()
                .map(SelectionField::new)
                .collect(Collectors.toSet());

        ObjectMapper objectMapper = new ObjectMapper();

        try{
            blueprint.setContent(objectMapper.writeValueAsString(fields));
        }catch (JsonProcessingException e){
            blueprint.setContent("");
        }
        return eventBluePrintRepository.save(blueprint);
    }

    public Event addFieldsFromBluePrint(long eventId, long bluePrintId){
        Event event = getEventById(eventId);
        EventBlueprint blueprint = getEventBlueprintById(bluePrintId);
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            Set<SelectionField> fields = Set.of(objectMapper.readValue(blueprint.getContent(),SelectionField[].class));
            addFieldsToEvent(eventId,fields);
        } catch (JsonProcessingException e){
            return event;
        }
        return event;
    }

    public void setStartTimeAndEndTime(long eventid, int startHour, int endHour){
        Calendar calendar = getEventById(eventid).getCalendar();
        calendarService.setTimeInterval(calendar,startHour,endHour);
    }

    public void resetStartHourAndEndHour(long eventid){
        Calendar calendar = getEventById(eventid).getCalendar();
        calendarService.resetTimeInterval(calendar);
    }

    public void finalizeEvent(long eventid) throws EventCannotBeFinalizedException {
        Event event = getEventById(eventid);
        checkIfEventCanBeFinalized(event);

        event.setFinalized(true);
        String title = event.getEventName() + " has been finalized.";
        String content = getMailContentForEvent(event);

        event.getParticipants().forEach(
                participant -> mailService.sendMail(event.getOwner(),participant,title,content)
        );
    }

    public void checkIfEventCanBeFinalized(Event event) throws EventCannotBeFinalizedException {
        Calendar calendar = event.getCalendar();
        if(calendar != null && (calendar.getStarthour() == -1 || calendar.getEndhour() == -1)){
            throw new EventCannotBeFinalizedException();
        }
        Set<SelectionField> selectionFields = event.getSelectionFields();
        if(selectionFields.stream().anyMatch(selectionField -> selectionField.getOptions().stream().noneMatch(Option::isSelected))){
            throw new EventCannotBeFinalizedException();
        };
    }

    private String getMailContentForEvent(Event event) {
        //TODO

        String message = h2("An event you're participating in has been finalized");

        String header = h1(event.getEventName());

        String description = p(event.getDescription());

        String tableContent = "";

        if(event.getCalendar() != null){
            int start = event.getCalendar().getStarthour();
            int end = event.getCalendar().getEndhour();

            List<Day> days = event.getCalendar().getDays();

            Day startDay = days.stream()
                    .filter(d-> d.getHours().stream().anyMatch(h->h.getNumberInTotal() == start))
                    .findFirst().get();

            Hour startHour = startDay.getHours().stream()
                    .filter(h->h.getNumberInTotal() == start).findFirst().get();

            Day endDay = days.stream()
                    .filter(d-> d.getHours().stream().anyMatch(h->h.getNumberInTotal() == end))
                    .findFirst().get();

            Hour endHour = endDay.getHours().stream()
                    .filter(h->h.getNumberInTotal() == end).findFirst().get();

            tableContent+=tr(
                    td("Start of the event")
                            +td(startDay.getDayStartTime().toLocalDate()+":"+getHourString(startHour.getNumber()))
                            )
                    +tr(td("End of the event")
                            +td(endDay.getDayStartTime().toLocalDate().toString()+":"+getHourString(endHour.getNumber()))
                    );
        }

        for (SelectionField field : event.getSelectionFields()){
            Option option = field.getOptions().stream().filter(Option::isSelected).findFirst().get();
            tableContent += tr(td(field.getTitle()+td(option.getValue())));
        }

        String table = table(
                tr(th("Field")+th("Value"))
                +tableContent
        );

        return message+header+description+table;
    }

    private String getHourString(int number){
        if(number>9){
            return String.valueOf(number);
        } else {
            return "0"+String.valueOf(number);
        }
    }

    public void unFinalizeEvent(long eventid){
        Event event = getEventById(eventid);
        event.setFinalized(false);

        String title = "An event you are participating in has been unfinalized";
        String content = h1(event.getEventName()+" was un-finalized by "+event.getOwner().getUsername())
                        + p("This happened at "+LocalDateTime.now()+". The event fields can now be modified again until the next finalisation.");
        event.getParticipants().forEach(
                participant -> mailService.sendMail(event.getOwner(),participant,title,content)
        );
    }

    public void checkIfEventIsFinalized(long eventid) throws EventIsFinalizedException {
        Event event = getEventById(eventid);
        if(event.isFinalized()){
            throw new EventIsFinalizedException();
        }
    }

    public List<TimeIntervalDetails> getBestTimeIntervals(long eventId, int minParticipants, int minLength, Set<Opinion.UserOpinion> allowedOpinions){
        Calendar calendar = getEventById(eventId).getCalendar();
        return calendarService.getBestTimeIntervals(calendar,minParticipants,minLength,allowedOpinions);
    }

    public void checkIfUserHasRightsToBlueprint(long blueprintId, User user) throws BlueprintCannotBeAccessedException {
        EventBlueprint blueprint = getEventBlueprintById(blueprintId);
        if(!blueprint.getInsertUser().equals(user)){
            throw new BlueprintCannotBeAccessedException();
        }
    }

    public Set<EventBlueprint> getBlueprintsForUser(User user){
        return eventBluePrintRepository.getBlueprintsForUser(user.getUsername());
    }

    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }
}
