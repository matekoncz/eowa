package com.example.eowa.service;

import com.example.eowa.exceptions.CalendarExceptions.CalendarException;
import com.example.eowa.model.Calendar;
import com.example.eowa.model.Event;
import com.example.eowa.model.Opinion;
import com.example.eowa.model.User;
import com.example.eowa.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    private final CalendarService calendarService;

    public EventService(EventRepository eventRepository, CalendarService calendarService) {
        this.eventRepository = eventRepository;
        this.calendarService = calendarService;
    }

    public Event saveEvent(Event event) {
        event.addParticipant(event.getOwner());
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

    public void setUnavailableHoursDaily(long id, Set<Integer> hourNumbers){
        setUnavailableHoursPeriodically(id,hourNumbers, CalendarService.Period.DAILY);
    }

    public void setUnavailableHoursWeekly(long id, Set<Integer> hourNumbers){
        setUnavailableHoursPeriodically(id,hourNumbers, CalendarService.Period.WEEKLY);
    }

    private void setUnavailableHoursPeriodically(long id, Set<Integer> hourNumbers, int period){
        Calendar calendar = getEventById(id).getCalendar();
        if(calendar == null){
            return;
        }
        calendarService.setUnavailableHoursPeriodically(calendar,hourNumbers,period);
    }

    public void setUserOpinion(long id, Set<Integer> hourSerials, User user, Opinion.UserOpinion userOpinion){
        Calendar calendar = getEventById(id).getCalendar();
        calendarService.setUserOpinion(calendar,user,hourSerials,userOpinion);
    }

    public void removeUserOpinion(long id, Set<Integer> hourSerials, User user){
        Calendar calendar = getEventById(id).getCalendar();
        calendarService.removeUserOpinion(calendar,user,hourSerials);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElse(null);
    }

    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }
}
