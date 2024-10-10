package com.example.eowa.controller;

import com.example.eowa.model.Credentials;
import com.example.eowa.model.User;
import com.example.eowa.service.AuthService;
import com.example.eowa.service.SessionService;
import com.example.eowa.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.authenticationExceptions.AuthenticationException;
import exceptions.userExceptions.UserException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserService userService;

    private final AuthService authService;

    private final SessionService sessionService;

    public AuthController(UserService userService, AuthService authService, SessionService sessionService) {
        this.userService = userService;
        this.authService = authService;
        this.sessionService = sessionService;
    }

    @PostMapping("/signup")
    public HttpServletResponse signUp(@RequestBody User user, HttpServletResponse response) throws UserException {
        authService.signUpUser(user);
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    @PostMapping("/login")
    public HttpServletResponse login(@RequestBody Credentials credentials, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        String jsessionid = authService.login(credentials,request.getSession().getId());
        User loggedInUser = sessionService.getUserBySessionId(jsessionid);

        String userJson = objectMapper.writeValueAsString(loggedInUser);

        Cookie cookie = new Cookie("jsessionid",jsessionid);
        cookie.setMaxAge(60*60*4);

        response.addCookie(cookie);
        response.setContentType("application/json");
        response.getWriter().print(userJson);
        response.setStatus(HttpStatus.OK.value());
        return response;
    }

    @DeleteMapping("/logout")
    public HttpServletResponse logout(HttpServletRequest request, HttpServletResponse response){
        CookieReader cookieReader = new CookieReader(request.getCookies());
        if(!cookieReader.hasCookie("jsessionid")) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return response;
        }

        Cookie sessionCookie = cookieReader.getCookie("jsessionid");
        String jsessionid = sessionCookie.getValue();
        authService.logout(jsessionid);
        sessionCookie.setMaxAge(0);
        response.addCookie(sessionCookie);
        response.setStatus(HttpStatus.OK.value());
        return response;

    }
}
