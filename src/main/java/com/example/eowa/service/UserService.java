package com.example.eowa.service;

import com.example.eowa.exceptions.userExceptions.*;
import com.example.eowa.model.User;
import com.example.eowa.repository.UserRepository;
import jakarta.transaction.Transactional;
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

    private void validateUser(User user) throws UserException{
        checkRequiredFields(user);
        checkEmailFormat(user);
        checkPassword(user);
        checkUsername(user);
        checkIfEmailIsTaken(user);
    }

    private void checkIfEmailIsTaken(User user) throws EmailAddressNotUniqueException {
        if(user.getEmail()==null){
            return;
        }
        if(userRepository.usersWithThisEmail(user.getEmail())>0){
            throw new EmailAddressNotUniqueException();
        }
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
        return passwordEncoder.matches(password,user.getPassword());
    }
}
