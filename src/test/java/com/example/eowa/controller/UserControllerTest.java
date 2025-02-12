package com.example.eowa.controller;

import com.example.eowa.EowaIntegrationTest;
import com.example.eowa.model.Credentials;
import com.example.eowa.model.User;
import com.example.eowa.model.WebToken;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
public class UserControllerTest extends EowaIntegrationTest {
    @Test
    public void shouldGetLoggedInUser() throws Exception {
        User user = new User("felh","asznalo1","email@gmail.com");
        authService.signUpUser(user);

        Credentials credentials = new Credentials();
        credentials.setUsername("felh");
        credentials.setPassword("asznalo1");
        authService.login(credentials);

        String jsessionid = userService.getUserByUsername("felh").getSession().getJsessionid();

        WebToken jwt = new WebToken();
        jwt.setUser(user);
        jwt.setTimestamp(System.currentTimeMillis());
        jwt.setJsessionid(jsessionid);

        MockHttpServletResponse response = mockMvc.perform(get("/users/currentuser")
                .contentType("application/json")
                .accept("application/json")
                .header(HttpHeaders.AUTHORIZATION, objectMapper.writeValueAsString(jwt)))
                .andReturn().getResponse();

        User currentuser = objectMapper.readValue(response.getContentAsString(),User.class);
        Assertions.assertEquals(currentuser.getUsername(),"felh");
    }
}
