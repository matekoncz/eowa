package com.example.eowa.service;

import com.example.eowa.model.Session;
import com.example.eowa.model.User;
import com.example.eowa.repository.SessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Session saveSession(Session session){
        session.setTimestamp(System.currentTimeMillis());
        return sessionRepository.save(session);
    }


    public Session getSessionById(String jsessionid){
        return sessionRepository.findById(jsessionid).orElse(null);
    }

    public User getUserBySessionId(String jsessionid) {
        Session session = getSessionById(jsessionid);
        return session.getUser();
    }

    public void deleteAllSession() {
        sessionRepository.deleteAll();
    }

    public Session updateSession(Session session){
        return sessionRepository.save(session);
    }

    public void flush(){
        sessionRepository.flush();
    }

    public int removeSessionById(String jsessionid) {
        return sessionRepository.removeSessionById(jsessionid);
    }
}
