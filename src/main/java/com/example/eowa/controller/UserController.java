package com.example.eowa.controller;

import com.example.eowa.model.User;
import com.example.eowa.service.SessionService;
import com.example.eowa.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/users")
public class UserController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserService userService;

    private final SessionService sessionService;

    public UserController(UserService userService, SessionService sessionService) {
        this.userService = userService;
        this.sessionService = sessionService;
    }

    @GetMapping("/currentuser")
    public HttpServletResponse getCurrentUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CookieReader cookieReader = new CookieReader(request.getCookies());
        User currentUser = sessionService.getUserBySessionId(cookieReader.getCookie("jsessionid").getValue());
        String userJson = objectMapper.writeValueAsString(currentUser);
        response.getWriter().print(userJson);
        return response;
    }


}
