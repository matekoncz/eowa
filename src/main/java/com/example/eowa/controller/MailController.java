package com.example.eowa.controller;

import com.example.eowa.exceptions.authenticationExceptions.AuthenticationException;
import com.example.eowa.model.Mail;
import com.example.eowa.model.WebToken;
import com.example.eowa.service.AuthService;
import com.example.eowa.service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;

@Controller
@RequestMapping("/mails")
public class MailController {

    private final AuthService authService;

    private final MailService mailService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MailController(AuthService authService, MailService mailService) {
        this.authService = authService;
        this.mailService = mailService;
    }

    @GetMapping("/get-unread")
    public void getUnreadMails(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            HttpServletResponse response) throws AuthenticationException, IOException {
        authService.authorizeUser(jwt.getJsessionid());
        Set<Mail> mails = mailService.getUnreadMails(jwt.getUser());

        response.setStatus(HttpStatus.OK.value());
        response.getWriter().print(objectMapper.writeValueAsString(mails));
    }

    @GetMapping("/get-all")
    public void getEveryMail(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            HttpServletResponse response) throws AuthenticationException, IOException {
        authService.authorizeUser(jwt.getJsessionid());
        Set<Mail> mails = mailService.getEveryMail(jwt.getUser());

        response.setStatus(HttpStatus.OK.value());
        response.getWriter().print(objectMapper.writeValueAsString(mails));
    }

    @PutMapping("/read/{id}")
    public void readMail(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            HttpServletResponse response,
            @PathVariable("id") long id
    ){
        mailService.readMail(id);
        response.setStatus(HttpStatus.OK.value());
    }
}
