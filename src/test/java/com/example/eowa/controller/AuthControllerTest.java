package com.example.eowa.controller;

import com.example.eowa.EowaIntegrationTest;
import com.example.eowa.model.Credentials;
import com.example.eowa.model.User;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class AuthControllerTest extends EowaIntegrationTest {

    @Test
    public void shouldSignUpUser() throws Exception {
        User user = new User("felh","asznalo1","email@gmail.com");
        String serializedUser = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/auth/signup")
                .accept("application/json")
                .content(serializedUser)
                .contentType("application/json"));

        Assertions.assertNotNull(userService.getUserByUsername("felh"));
    }

    @Test
    public void shouldNotSignUpNotUniqueUser() throws Exception {
        userService.saveUser(new User("felh","asznalo1","email@gmail.com"));

        User user = new User("felh","asznalo1","email@gmail.com");
        String serializedUser = objectMapper.writeValueAsString(user);
        MockHttpServletResponse response = mockMvc.perform(post("/auth/signup")
                .accept("application/json")
                .content(serializedUser)
                .contentType("application/json"))
                .andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(),HttpStatus.CONFLICT.value());
    }

    @Test
    public void shouldNotSignUpInvalidUser() throws Exception {
        User user = new User("felh","asznalo1",".@.@gmail.com");
        String serializedUser = objectMapper.writeValueAsString(user);
        MockHttpServletResponse response = mockMvc.perform(post("/auth/signup")
                .accept("application/json")
                .content(serializedUser)
                .contentType("application/json"))
                .andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(), HttpStatus.NOT_ACCEPTABLE.value());
    }

    @Test
    public void shouldLoginUser() throws Exception{
        User user = new User("felh","asznalo1","email@gmail.com");
        userService.saveUser(user);

        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");

        String serializedCredentials = objectMapper.writeValueAsString(credentials);
        MockHttpServletResponse response = mockMvc.perform(post("/auth/login")
                .accept("application/json")
                .contentType("application/json")
                .content(serializedCredentials))
                .andReturn().getResponse();

        CookieReader cookieReader = new CookieReader(response.getCookies());
        Assertions.assertTrue(cookieReader.hasCookie("jsessionid"));

        String jsessionid = cookieReader.getCookie("jsessionid").getValue();
        User currentuser = sessionService.getUserBySessionId(jsessionid);
        Assertions.assertEquals(currentuser.getUsername(),"felh");
    }

    @Test
    public void shouldNotLoginUserWithInvalidPassword() throws Exception{
        User user = new User("felh","asznalo1","email@gmail.com");
        userService.saveUser(user);

        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("invalid");

        String serializedCredentials = objectMapper.writeValueAsString(credentials);
        MockHttpServletResponse response = mockMvc.perform(post("/auth/login")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(serializedCredentials))
                .andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(),HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void shouldReportNonExistentUser() throws Exception{
        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");

        String serializedCredentials = objectMapper.writeValueAsString(credentials);
        MockHttpServletResponse response = mockMvc.perform(post("/auth/login")
                .accept("application/json")
                .contentType("application/json")
                .content(serializedCredentials))
                .andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(),HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void shouldLogOutUser() throws Exception {
        User user = new User("felh","asznalo1","email@gmail.com");
        String serializedUser = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/auth/signup")
                .accept("application/json")
                .content(serializedUser)
                .contentType("application/json"));

        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");

        String serializedCredentials = objectMapper.writeValueAsString(credentials);
        MockHttpServletResponse response = mockMvc.perform(post("/auth/login").accept("application/json").contentType("application/json").content(serializedCredentials)).andReturn().getResponse();

        CookieReader cookieReader = new CookieReader(response.getCookies());
        Assertions.assertTrue(cookieReader.hasCookie("jsessionid"));

        Cookie sessionCookie = cookieReader.getCookie("jsessionid");

        mockMvc.perform(delete("/auth/logout").cookie(sessionCookie)).andReturn().getResponse();

        Assertions.assertNull(sessionService.getSessionById(sessionCookie.getValue()));
    }
}
