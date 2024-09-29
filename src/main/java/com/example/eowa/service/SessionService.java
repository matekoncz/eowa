package com.example.eowa.service;

import com.example.eowa.model.Session;
import com.example.eowa.model.User;
import com.example.eowa.repository.SessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

//    @Transactional
    public Session saveSession(Session session){
        session.setTimestamp(System.currentTimeMillis());
        setSessionOnUser(session);
        return sessionRepository.save(session);
    }

    private static void setSessionOnUser(Session session) {
        User user = session.getUser();
        user.setSession(session);
    }

    public Session getSessionById(String jsessionid){
        return sessionRepository.findById(jsessionid).orElse(null);
    }

    public void deleteSessionById(String jsessionid){
        sessionRepository.deleteById(jsessionid);
    }

    public User getUserBySessionId(String jsessionid) {
        Session session = getSessionById(jsessionid);
        return session.getUser();
    }

    public void deleteAllSession() {
        sessionRepository.deleteAll();
    }
}
