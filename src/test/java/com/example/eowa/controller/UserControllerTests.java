package com.example.eowa.controller;

import com.example.eowa.EowaApplication;
import com.example.eowa.model.Credentials;
import com.example.eowa.model.User;
import com.example.eowa.service.EventService;
import com.example.eowa.service.SessionService;
import com.example.eowa.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = EowaApplication.class)
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private EventService eventService;

    @BeforeEach
    public void beforeTests() throws Exception {
        eventService.deleteAllEvent();
        userService.deleteAllUsers();
        sessionService.deleteAllSession();
    }

    @Test
    public void shouldGetLoggedInUser() throws Exception {
        User user = new User("felh","asznalo1","email@gmail.com");
        signUpUser(user);
        loginuser(user);

        String jsessionid = userService.getUserByUsername("felh").getSession().getJsessionid();
        Cookie cookie = new Cookie("jsessionid",jsessionid);
        cookie.setMaxAge(60*60*4);
        MockHttpServletResponse response = mockMvc.perform(get("/users/currentuser").accept("application/json").cookie(cookie)).andReturn().getResponse();
        User currentuser = objectMapper.readValue(response.getContentAsString(),User.class);
        Assertions.assertEquals(currentuser.getUsername(),"felh");
    }

    private void loginuser(User user) throws Exception {
        Credentials credentials = new Credentials();
        credentials.setUsername(user.getUsername());
        credentials.setPassword(user.getPassword());

        String serializedCredentials = objectMapper.writeValueAsString(credentials);
        mockMvc.perform(post("/auth/login").accept("application/json").contentType("application/json").content(serializedCredentials));
    }

    private void signUpUser(User user) throws Exception {
        String serializedUser = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/auth/signup").accept("application/json").content(serializedUser).contentType("application/json"));
    }
}
