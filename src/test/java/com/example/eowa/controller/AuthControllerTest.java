package com.example.eowa.controller;

import com.example.eowa.EowaIntegrationTest;
import com.example.eowa.model.Credentials;
import com.example.eowa.model.User;
import com.example.eowa.model.WebToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class AuthControllerTest extends EowaIntegrationTest {

    @Test
    public void shouldSignUpUser() throws Exception {
        String serializedUser = "{ \"username\" : \"felh\" , \"password\" : \"asznalo1\" , \"email\" : \"email@gmail.com\" }";
        MockHttpServletResponse response = mockMvc.perform(post("/auth/signup")
                .accept("application/json")
                .content(serializedUser)
                .contentType("application/json")).andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(),HttpStatus.OK.value());
        Assertions.assertNotNull(userService.getUserByUsername("felh"));
    }

    @Test
    public void shouldNotSignUpNotUniqueUser() throws Exception {
        authService.signUpUser(new User("felh","asznalo1","email@gmail.com"));

        String serializedUser = "{ \"username\" : \"felh\" , \"password\" : \"asznalo1\" , \"email\" : \"email@gmail.com\" }";
        MockHttpServletResponse response = mockMvc.perform(post("/auth/signup")
                .accept("application/json")
                .content(serializedUser)
                .contentType("application/json"))
                .andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(),HttpStatus.CONFLICT.value());
    }

    @Test
    public void shouldNotSignUpInvalidUser() throws Exception {
        String serializedUser = "{ \"username\" : \"felh\" , \"password\" : \"asznalo1\" , \"email\" : \". @.@gmail.com\"}";
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
        authService.signUpUser(user);

        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");

        String serializedCredentials = objectMapper.writeValueAsString(credentials);
        MockHttpServletResponse response = mockMvc.perform(post("/auth/login")
                .accept("application/json")
                .contentType("application/json")
                .content(serializedCredentials))
                .andReturn().getResponse();

        WebToken jwt = objectMapper.readValue(response.getContentAsString(), WebToken.class);

        Assertions.assertEquals(user.getUsername(),sessionService.getUserBySessionId(jwt.getJsessionid()).getUsername());
    }

    @Test
    public void shouldNotLoginUserWithInvalidPassword() throws Exception{
        User user = new User("felh","asznalo1","email@gmail.com");
        authService.signUpUser(user);

        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("invalid");

        String serializedCredentials = objectMapper.writeValueAsString(credentials);
        MockHttpServletResponse response = mockMvc.perform(post("/auth/login")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(serializedCredentials))
                .andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(),HttpStatus.NOT_ACCEPTABLE.value());
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
        authService.signUpUser(user);

        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");

        String serializedCredentials = objectMapper.writeValueAsString(credentials);
        MockHttpServletResponse response = mockMvc.perform(post("/auth/login").accept("application/json").contentType("application/json").content(serializedCredentials)).andReturn().getResponse();

        Assertions.assertEquals(response.getStatus(),HttpStatus.OK.value());

        mockMvc.perform(delete("/auth/logout").header(HttpHeaders.AUTHORIZATION,response.getContentAsString())).andReturn().getResponse();

        WebToken jwt = objectMapper.readValue(response.getContentAsString(), WebToken.class);

        Assertions.assertNull(sessionService.getSessionById(jwt.getJsessionid()));
    }
}
