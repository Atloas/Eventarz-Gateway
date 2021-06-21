package com.agh.EventarzGateway.exceptions;

import com.agh.EventarzGateway.model.dtos.ErrorDTO;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

// TODO: A bit bloated, trim it somehow?

@ControllerAdvice
public class GatewayExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public ResponseEntity handleFeignException(FeignException e, HttpServletRequest request) {
        if (e.status() == -1) {
            ErrorDTO errorDTO = new ErrorDTO(
                    HttpStatus.SERVICE_UNAVAILABLE.value(),
                    HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                    request.getRequestURI(),
                    "Something went wrong!"
            );
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorDTO);
        } else {
            return ResponseEntity.status(HttpStatus.valueOf(e.status())).body(e.contentUTF8());
        }
    }

    @ExceptionHandler(MicroserviceConnectionException.class)
    public ResponseEntity<ErrorDTO> handleMicroserviceConnectionException(MicroserviceConnectionException e, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                request.getRequestURI(),
                "Service unavailable!"
        );

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorDTO);
    }

    @ExceptionHandler(FounderAttemptingToLeaveException.class)
    public ResponseEntity<ErrorDTO> handleFounderAttemptingToLeaveException(FounderAttemptingToLeaveException e, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                request.getRequestURI(),
                "You cannot leave Groups you founded!"
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDTO);
    }

    @ExceptionHandler(NotFounderException.class)
    public ResponseEntity<ErrorDTO> handleNotFounderException(NotFounderException e, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                request.getRequestURI(),
                "You are not the founder of this Group!"
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDTO);
    }

    @ExceptionHandler(UserNotInEventsGroupException.class)
    public ResponseEntity<ErrorDTO> handleUserNotInEventsGroupException(UserNotInEventsGroupException e, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                request.getRequestURI(),
                "You are not allowed to post to this Group!"
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDTO);
    }

    @ExceptionHandler(NotOrganizerException.class)
    public ResponseEntity<ErrorDTO> handleNotOrganizerException(NotOrganizerException e, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                request.getRequestURI(),
                "You are not the organizer of this Event!"
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDTO);
    }

    @ExceptionHandler(EventFullException.class)
    public ResponseEntity<ErrorDTO> handleEventFullException(EventFullException e, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                request.getRequestURI(),
                "This Event is already full!"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDTO> handleUserAlreadyExistsException(UserAlreadyExistsException e, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                request.getRequestURI(),
                "Username taken!"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDTO> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                request.getRequestURI(),
                "Incorrect username or password!"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDTO);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorDTO> handleLockedException(LockedException e, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                request.getRequestURI(),
                "Account locked!"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDTO);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            builder.append(error.getDefaultMessage()).append("\n");
        }

        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                request.getRequestURI(),
                builder.toString()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }
}
