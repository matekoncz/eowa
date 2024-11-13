package com.example.eowa.service;

import com.example.eowa.exceptions.CalendarExceptions.CalendarException;
import com.example.eowa.model.Calendar;
import com.example.eowa.model.Event;
import com.example.eowa.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    private final CalendarService calendarService;

    public EventService(EventRepository eventRepository, CalendarService calendarService) {
        this.eventRepository = eventRepository;
        this.calendarService = calendarService;
    }

    public Event saveEvent(Event event){
        event.addParticipant(event.getOwner());
        return eventRepository.save(event);
    }

    public void setEventCalendar(long eventId, String zoneId, LocalDateTime startTime, LocalDateTime endTime) throws CalendarException {
        Calendar calendar = calendarService.createCalendar(zoneId,startTime,endTime);
        Event event = getEventById(eventId);
        event.setCalendar(calendar);
    }

    public void deleteEventById(Long id){
        eventRepository.deleteById(id);
    }

    public void deleteAllEvent(){
        calendarService.deleteAllCalendarData();
        eventRepository.deleteAll();
    }

    public Event getEventById(Long id){
        return eventRepository.findById(id).orElse(null);
    }

    public Event updateEvent(Event event){
        return eventRepository.save(event);
    }
}
