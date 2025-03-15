package com.example.eowa.service;

import com.example.eowa.model.Mail;
import com.example.eowa.model.User;
import com.example.eowa.repository.MailRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Transactional
public class MailService {
    private final MailRepository mailRepository;

    public MailService(MailRepository mailRepository) {
        this.mailRepository = mailRepository;
    }

    public void deleteEveryMail() {
        this.mailRepository.deleteAll();
    }

    public Mail sendMail(User from, User to, String title, String content){
        Mail mail = new Mail(from,to,title,content,false);
        return mailRepository.save(mail);
    }

    public Set<Mail> getUnreadMails(User user){
        return mailRepository.getUnreadMails(user.getUsername());
    }

    public Set<Mail> getEveryMail(User user){
        return mailRepository.getAllMails(user.getUsername());
    }

    public Mail getMailById(long id){
        return mailRepository.findById(id).orElse(null);
    }

    public void readMail(long id){
        getMailById(id).setRead(true);
    }

    public void deleteMailById(long id){
        mailRepository.deleteById(id);
    }
}
