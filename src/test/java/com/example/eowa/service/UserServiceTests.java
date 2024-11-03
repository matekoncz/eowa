package com.example.eowa.service;

import com.example.eowa.exceptions.userExceptions.*;
import com.example.eowa.exceptions.userExceptions.UserException;
import com.example.eowa.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTests {

    @Autowired
    private EventService eventService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void beforeTests(){
        sessionService.deleteAllSession();
        eventService.deleteAllEvent();
        userService.deleteAllUsers();
    }

    @Test
    public void shouldSaveUser() throws UserException {
        User user = new User("felh","asznalo1","a@b.com");
        User savedUser = userService.saveUser(user);
        Assertions.assertNotNull(savedUser);
        Assertions.assertNotEquals("asznalo1",savedUser.getPassword());
    }

    @Test
    public void shouldGetUserByUsername() throws UserException {
        User user = new User("felh","asznalo1","a@b.com");
        userService.saveUser(user);
        User savedUser = userService.getUserByUsername("felh");
        Assertions.assertEquals(user.getEmail(),savedUser.getEmail());
    }

    @Test
    public void shouldNotSaveInvalidUser() throws UserException{
        User user = new User();
        Assertions.assertThrows(UserMissingRequiredFieldsException.class,()->{
            userService.saveUser(user);
        });

        User user2 = new User("","","");
        Assertions.assertThrows(UserMissingRequiredFieldsException.class,()->{
            userService.saveUser(user2);
        });

        User user3 = new User("felh","asznalo3","a@b.c");
        Assertions.assertThrows(EmailAddressInInvalidFormatException.class,()->{
            userService.saveUser(user3);
        });

        User user4 = new User("felh","asznalo4","a4@b.com");
        userService.saveUser(user4);

        User user5 = new User("felh","asznalo5","a5@b.com");
        Assertions.assertThrows(UsernameNotUniqueException.class,()->{
            userService.saveUser(user5);
        });
    }

    @Test
    public void shouldNotSaveTwoUsersWithSameEmail() throws UserException {
        User user = new User("felh","asznalo1","a@b.com");
        userService.saveUser(user);
        User newuser = new User("felh2","asznalo1","a@b.com");

        Assertions.assertThrows(EmailAddressNotUniqueException.class,()->{
            userService.saveUser(newuser);
        });
    }
}
