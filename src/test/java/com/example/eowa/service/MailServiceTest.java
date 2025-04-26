package com.example.eowa.service;

import com.example.eowa.EowaIntegrationTest;
import com.example.eowa.exceptions.authenticationExceptions.AuthenticationException;
import com.example.eowa.exceptions.userExceptions.UserException;
import com.example.eowa.model.Credentials;
import com.example.eowa.model.Mail;
import com.example.eowa.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class MailServiceTest extends EowaIntegrationTest {

    @Test
    public void shouldSendMail() throws Exception{
        User sender = createAndLoginFirstUser();
        User reciever = createAndLoginSecondUser();

        mailService.sendMail(sender,reciever,"cim","tartalom");

        Mail mail = mailService.getEveryMail(reciever).stream().findFirst().orElse(null);

        Assertions.assertNotNull(mail);
        Assertions.assertEquals(mail.getSender(),sender);
        Assertions.assertEquals(mail.getReciever(),reciever);
        Assertions.assertEquals(mail.getTitle(),"cim");
        Assertions.assertEquals(mail.getContent(),"tartalom");
        Assertions.assertFalse(mail.isRead());
    }

    @Test
    public void shouldGetUnreadMails() throws Exception {
        User sender = createAndLoginFirstUser();
        User reciever = createAndLoginSecondUser();

        Mail firstMail = mailService.sendMail(sender,reciever,"elso","tartalom");
        Mail secondMail = mailService.sendMail(sender,reciever,"masodik","tartalom");

        mailService.readMail(firstMail.getId());

        Set<Mail> unreadMails = mailService.getUnreadMails(reciever);

        Assertions.assertEquals(unreadMails.size(),1);
        Assertions.assertEquals(unreadMails.stream().findFirst().orElse(null),secondMail);

    }

    @Test
    public void shouldGetEveryMail() throws Exception {
        User sender = createAndLoginFirstUser();
        User reciever = createAndLoginSecondUser();

        Mail firstMail = mailService.sendMail(sender,reciever,"elso","tartalom");
        Mail secondMail = mailService.sendMail(sender,reciever,"masodik","tartalom");

        mailService.readMail(firstMail.getId());

        Set<Mail> mails = mailService.getEveryMail(reciever);

        Assertions.assertEquals(mails.size(),2);
    }

    @Test
    public void shouldDeleteMailById() throws Exception{
        User sender = createAndLoginFirstUser();
        User reciever = createAndLoginSecondUser();

        Mail savedMail = mailService.sendMail(sender,reciever,"elso","tartalom");

        mailService.deleteMailById(savedMail.getId());

        Assertions.assertNull(mailService.getMailById(savedMail.getId()));
    }

    private User createAndLoginFirstUser() throws Exception {
        User user = new User("felhasznalo1","jelszo123","email1@gmail.com");
        return createAndLoginUser(user);
    }

    private User createAndLoginSecondUser() throws Exception {
        User user = new User("felhasznalo2","jelszo123","email2@gmail.com");
        return createAndLoginUser(user);
    }

    private User createAndLoginUser(User user) throws UserException, AuthenticationException {
        Credentials credentials = new Credentials();
        credentials.setUsername(user.getUsername());
        credentials.setPassword(user.getPassword());
        User savedUser = userService.saveUser(user);
        authService.login(credentials);
        return savedUser;
    }
}
