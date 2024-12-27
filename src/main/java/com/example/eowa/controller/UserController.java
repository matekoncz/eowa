package com.example.eowa.controller;

import com.example.eowa.exceptions.authenticationExceptions.AuthenticationException;
import com.example.eowa.model.User;
import com.example.eowa.service.AuthService;
import com.example.eowa.service.SessionService;
import com.example.eowa.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/users")
public class UserController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final SessionService sessionService;

    private final AuthService authService;
    public UserController(UserService userService, SessionService sessionService, AuthService authService) {
        this.sessionService = sessionService;
        this.authService = authService;
    }

    @GetMapping("/currentuser")
    public HttpServletResponse getCurrentUser(@CookieValue("jsessionid") String jsessionid, HttpServletResponse response) throws IOException, AuthenticationException {
        authService.validateSession(jsessionid);
        User currentUser = sessionService.getUserBySessionId(jsessionid);
        String userJson = objectMapper.writeValueAsString(currentUser);
        response.getWriter().print(userJson);
        return response;
    }


}
