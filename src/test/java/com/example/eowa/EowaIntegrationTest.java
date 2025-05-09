package com.example.eowa;

import com.example.eowa.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = EowaApplication.class)
@AutoConfigureMockMvc
public abstract class EowaIntegrationTest {

    @Autowired
    protected AuthService authService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected EventService eventService;

    @Autowired
    protected SessionService sessionService;

    @Autowired
    protected CalendarService calendarService;

    @Autowired
    protected MailService mailService;

    @Autowired
    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper = new ObjectMapper();

    public EowaIntegrationTest(){
        objectMapper.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    public void beforeTests(){
        mailService.deleteEveryMail();
        eventService.deleteAllEvent();
        userService.deleteAllUsers();
        sessionService.deleteAllSession();
    }
}
