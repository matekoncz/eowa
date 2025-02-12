package com.example.eowa.controller.exceptionHandlers;

import com.example.eowa.exceptions.CalendarExceptions.TimeTravelException;
import com.example.eowa.exceptions.CalendarExceptions.WrongIntervalException;
import com.example.eowa.exceptions.authenticationExceptions.*;
import com.example.eowa.exceptions.userExceptions.*;
import com.mysql.cj.exceptions.PasswordExpiredException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = { Exception.class})
    protected ResponseEntity<Object> handleDefault(
            Exception ex, WebRequest request) {
        String bodyOfResponse = "An unexpected exception occured";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = { InvalidInvitationCodeException.class})
    protected ResponseEntity<Object> handleInvalidInvitationCode(
            InvalidInvitationCodeException ex, WebRequest request) {
        String bodyOfResponse = "The given invitation code is invalid";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
    }

    @ExceptionHandler(value = { InvalidSessionException.class})
    protected ResponseEntity<Object> handleInvalidSession(
            InvalidSessionException ex, WebRequest request) {
        String bodyOfResponse = "The given session id is invalid";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
    }

    @ExceptionHandler(value = { UserIsNotEventOwnerException.class})
    protected ResponseEntity<Object> handleUserIsNotEventOwner(
            UserIsNotEventOwnerException ex, WebRequest request) {
        String bodyOfResponse = "Forbidden: only the event owner has rights to perform this action";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = { UserIsNotParticipantException.class})
    protected ResponseEntity<Object> handleUserIsNotParticipant(
            UserIsNotParticipantException ex, WebRequest request) {
        String bodyOfResponse = "Forbidden: only participants have rights to perform this action";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = { WrongPasswordException.class})
    protected ResponseEntity<Object> handleWrongPassword(
            WrongPasswordException ex, WebRequest request) {
        String bodyOfResponse = "Wrong password";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value = { WrongIntervalException.class})
    protected ResponseEntity<Object> handleWrongInterval(
            WrongIntervalException ex, WebRequest request) {
        String bodyOfResponse = "Wrong time interval";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
    }

    @ExceptionHandler(value = { TimeTravelException.class})
    protected ResponseEntity<Object> handleTimeTravel(
            TimeTravelException ex, WebRequest request) {
        String bodyOfResponse = "The event cannot happen in the past";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
    }

    @ExceptionHandler(value = { UserDoesNotExistException.class})
    protected ResponseEntity<Object> handleUserDoesNotExist(
            UserDoesNotExistException ex, WebRequest request) {
        String bodyOfResponse = "The referenced user does not exist";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = { EmailAddressInInvalidFormatException.class})
    protected ResponseEntity<Object> handleEmailAddressInInvalidFormat(
            EmailAddressInInvalidFormatException ex, WebRequest request) {
        String bodyOfResponse = "The given email address is in invalid format";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
    }

    @ExceptionHandler(value = { EmailAddressNotUniqueException.class})
    protected ResponseEntity<Object> handleEmailAddressNotUnique(
            EmailAddressNotUniqueException ex, WebRequest request) {
        String bodyOfResponse = "The given email address is already in use";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = { PasswordExpiredException.class})
    protected ResponseEntity<Object> handlePasswordTooShort(
            PasswordTooShortException ex, WebRequest request) {
        String bodyOfResponse = "The given password is too short";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
    }

    @ExceptionHandler(value = { UserMissingRequiredFieldsException.class})
    protected ResponseEntity<Object> handleUserMissingRequiredFields(
            UserMissingRequiredFieldsException ex, WebRequest request) {
        String bodyOfResponse = "Required fields are missing";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
    }

    @ExceptionHandler(value = { UsernameNotUniqueException.class})
    protected ResponseEntity<Object> handleUsernameNotUnique(
            UsernameNotUniqueException ex, WebRequest request) {
        String bodyOfResponse = "The given username is already taken";
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }
}
