package com.example.eowa.controller;

import com.example.eowa.EowaIntegrationTest;
import com.example.eowa.model.Credentials;
import com.example.eowa.model.User;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
public class DefaultExceptionHandlerTest extends EowaIntegrationTest {
    @Test
    public void testDefaultExceptionHandling() throws Exception {
        userService.saveUser(new User("felh", "asznalo1", "email@gmail.com"));
        Credentials credentials = new Credentials();
        credentials.setPassword("asznalo1");
        credentials.setUsername("felh");

        String jsessionid = authService.login(credentials);

        var response = mockMvc.perform(post("/events/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new Cookie("okie","dokie")))
                        .accept("application/json")
                        .cookie(new Cookie("jsessionid",jsessionid)))
                .andReturn()
                .getResponse();

        Assertions.assertEquals(response.getStatus(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
