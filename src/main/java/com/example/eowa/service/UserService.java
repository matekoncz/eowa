package com.example.eowa.service;

import com.example.eowa.model.User;
import com.example.eowa.repository.UserRepository;
import exceptions.userExceptions.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.hibernate.HibernateException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserService {

    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

//    @Transactional
    public User saveUser(User user) throws UserException {
        validateUser(user);

        String password = user.getPassword();
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    public User getUserByUsername(String username){
        return userRepository.findById(username).orElse(null);
    }

    public void deleteUserByUsername(String username){
        userRepository.deleteById(username);
    }

    public void deleteAllUsers(){
        userRepository.deleteAll();
    }

    private void validateUser(User user) throws UserMissingRequiredFieldsException, EmailAddressInInvalidFormatException, PasswordTooShortException, UsernameNotUniqueException {
        checkRequiredFields(user);
        checkEmailFormat(user);
        checkPassword(user);
        checkUsername(user);
    }

    private void checkUsername(User user) throws UsernameNotUniqueException {
        if(getUserByUsername(user.getUsername())!=null){
            throw new UsernameNotUniqueException();
        }
    }

    private void checkPassword(User user) throws PasswordTooShortException {
        if(user.getPassword().length()<8){
            throw new PasswordTooShortException();
        }
    }

    private void checkEmailFormat(User user) throws EmailAddressInInvalidFormatException {
        if(!user.getEmail().matches(EMAIL_REGEX)){
            throw new EmailAddressInInvalidFormatException();
        }
    }

    private void checkRequiredFields(User user) throws UserMissingRequiredFieldsException {
        if(
                user.getEmail()==null || user.getEmail().isEmpty()
                || user.getUsername() == null || user.getUsername().isEmpty()
                || user.getPassword() == null || user.getPassword().isEmpty()
        ){
            throw new UserMissingRequiredFieldsException();
        }
    }

    public boolean isPasswordCorrect(String password,User user){
        String encodedPassword = passwordEncoder.encode(password);
        return user.getPassword().equals(encodedPassword);
    }
}
