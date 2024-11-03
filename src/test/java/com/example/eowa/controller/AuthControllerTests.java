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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = EowaApplication.class)
@AutoConfigureMockMvc
public class AuthControllerTests {

    @Autowired
    SessionService sessionService;

    @Autowired
    EventService eventService;

    @Autowired
    UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserController authController;

    @Autowired
    private MockMvc mockMvc;
    @BeforeEach
    public void beforeTests() throws Exception {
        userService.deleteAllUsers();
        eventService.deleteAllEvent();
        userService.deleteAllUsers();
    }

    @Test
    public void shouldSignUpUser() throws Exception {
        User user = new User("felh","asznalo1","email@gmail.com");
        String serializedUser = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/auth/signup").accept("application/json").content(serializedUser).contentType("application/json"));

        Assertions.assertNotNull(userService.getUserByUsername("felh"));
    }

    @Test
    public void shouldLoginUser() throws Exception{
        User user = new User("felh","asznalo1","email@gmail.com");
        String serializedUser = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/auth/signup").accept("application/json").content(serializedUser).contentType("application/json"));

        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");

        String serializedCredentials = objectMapper.writeValueAsString(credentials);
        MockHttpServletResponse response = mockMvc.perform(post("/auth/login").accept("application/json").contentType("application/json").content(serializedCredentials)).andReturn().getResponse();

        CookieReader cookieReader = new CookieReader(response.getCookies());
        Assertions.assertTrue(cookieReader.hasCookie("jsessionid"));

        String jsessionid = cookieReader.getCookie("jsessionid").getValue();
        User currentuser = sessionService.getUserBySessionId(jsessionid);
        Assertions.assertEquals(currentuser.getUsername(),"felh");
    }

    @Test
    public void shouldLogOutUser() throws Exception {
        User user = new User("felh","asznalo1","email@gmail.com");
        String serializedUser = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/auth/signup").accept("application/json").content(serializedUser).contentType("application/json"));

        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");

        String serializedCredentials = objectMapper.writeValueAsString(credentials);
        MockHttpServletResponse response = mockMvc.perform(post("/auth/login").accept("application/json").contentType("application/json").content(serializedCredentials)).andReturn().getResponse();

        CookieReader cookieReader = new CookieReader(response.getCookies());
        Assertions.assertTrue(cookieReader.hasCookie("jsessionid"));

        Cookie sessionCookie = cookieReader.getCookie("jsessionid");

        MockHttpServletResponse logoutResponse = mockMvc.perform(delete("/auth/logout").cookie(sessionCookie)).andReturn().getResponse();

        Assertions.assertNull(sessionService.getSessionById(sessionCookie.getValue()));
    }
}
