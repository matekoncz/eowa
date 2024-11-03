package com.example.eowa.service;

import com.example.eowa.model.Event;
import com.example.eowa.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EventService {

    private EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event saveEvent(Event event){
        event.addParticipant(event.getOwner());
        return eventRepository.save(event);
    }

    public void deleteEventById(Long id){
        eventRepository.deleteById(id);
    }

    public void deleteAllEvent(){
        eventRepository.deleteAll();
    }

    public Event getEventById(Long id){
        return eventRepository.findById(id).orElse(null);
    }

    public Event updateEvent(Event event){
        return eventRepository.save(event);
    }
}
