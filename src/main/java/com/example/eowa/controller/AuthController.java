package com.example.eowa.controller;

import com.example.eowa.exceptions.authenticationExceptions.AuthenticationException;
import com.example.eowa.exceptions.userExceptions.UserException;
import com.example.eowa.model.Credentials;
import com.example.eowa.model.User;
import com.example.eowa.model.WebToken;
import com.example.eowa.service.AuthService;
import com.example.eowa.service.SessionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AuthService authService;

    private final SessionService sessionService;

    public AuthController(AuthService authService, SessionService sessionService) {
        this.authService = authService;
        this.sessionService = sessionService;
    }

    @PostMapping("/signup")
    public void signUp(
            @RequestBody User user,
            HttpServletResponse response) throws UserException, IOException {
        authService.signUpUser(user);
        response.setStatus(HttpStatus.OK.value());
    }

    @PostMapping("/login")
    public void login(
            @RequestBody Credentials credentials,
            HttpServletResponse response) throws AuthenticationException, IOException, UserException {
        String jsessionid = authService.login(credentials);
        User loggedInUser = sessionService.getUserBySessionId(jsessionid);
        loggedInUser.setSession(sessionService.getSessionById(jsessionid));

        WebToken webToken = new WebToken();
        webToken.setUser(loggedInUser);
        webToken.setJsessionid(loggedInUser.getSession().getJsessionid());
        webToken.setTimestamp(loggedInUser.getSession().getTimestamp());
        String jwt = objectMapper.writeValueAsString(webToken);

        response.setContentType("application/json");
        response.getWriter().print(jwt);
        response.setStatus(HttpStatus.OK.value());
    }

    @DeleteMapping("/logout")
    public void logout(
            @RequestHeader(HttpHeaders.AUTHORIZATION) WebToken jwt,
            HttpServletResponse response) throws JsonProcessingException {
        authService.logout(jwt.getJsessionid());
        Cookie sessionCookie = new Cookie("jsessionid",jwt.getJsessionid());
        sessionCookie.setMaxAge(0);
        response.addCookie(sessionCookie);
        response.setStatus(HttpStatus.OK.value());

    }
}
