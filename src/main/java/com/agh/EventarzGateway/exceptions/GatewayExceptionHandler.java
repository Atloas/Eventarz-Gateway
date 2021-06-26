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

@ControllerAdvice
public class GatewayExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public ResponseEntity handleFeignException(FeignException exception, HttpServletRequest request) {
        if (exception.status() == -1) {
            HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
            ErrorDTO errorDTO = new ErrorDTO(
                    status,
                    request.getRequestURI(),
                    "Service unavailable!"
            );
            return ResponseEntity.status(status).body(errorDTO);
        } else {
            return ResponseEntity.status(HttpStatus.valueOf(exception.status())).body(exception.contentUTF8());
        }
    }

    @ExceptionHandler(MicroserviceConnectionException.class)
    public ResponseEntity<ErrorDTO> handleMicroserviceConnectionException(MicroserviceConnectionException exception, HttpServletRequest request) {
        return getResponse(HttpStatus.SERVICE_UNAVAILABLE, request.getRequestURI(), "Service unavailable!");
    }

    @ExceptionHandler(FounderAttemptingToLeaveException.class)
    public ResponseEntity<ErrorDTO> handleFounderAttemptingToLeaveException(FounderAttemptingToLeaveException exception, HttpServletRequest request) {
        return getResponse(HttpStatus.FORBIDDEN, request.getRequestURI(), "You cannot leave Groups you founded!");
    }

    @ExceptionHandler(NotFounderException.class)
    public ResponseEntity<ErrorDTO> handleNotFounderException(NotFounderException exception, HttpServletRequest request) {
        return getResponse(HttpStatus.FORBIDDEN, request.getRequestURI(), "You are not the founder of this Group!");
    }

    @ExceptionHandler(UserNotInEventsGroupException.class)
    public ResponseEntity<ErrorDTO> handleUserNotInEventsGroupException(UserNotInEventsGroupException exception, HttpServletRequest request) {
        return getResponse(HttpStatus.FORBIDDEN, request.getRequestURI(), "You are not allowed to post to this Group!");
    }

    @ExceptionHandler(NotOrganizerException.class)
    public ResponseEntity<ErrorDTO> handleNotOrganizerException(NotOrganizerException exception, HttpServletRequest request) {
        return getResponse(HttpStatus.FORBIDDEN, request.getRequestURI(), "You are not the organizer of this Event!");
    }

    @ExceptionHandler(EventFullException.class)
    public ResponseEntity<ErrorDTO> handleEventFullException(EventFullException exception, HttpServletRequest request) {
        return getResponse(HttpStatus.BAD_REQUEST, request.getRequestURI(), "This Event is already full!");
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDTO> handleUserAlreadyExistsException(UserAlreadyExistsException exception, HttpServletRequest request) {
        return getResponse(HttpStatus.BAD_REQUEST, request.getRequestURI(), "Username taken!");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDTO> handleBadCredentialsException(BadCredentialsException exception, HttpServletRequest request) {
        return getResponse(HttpStatus.UNAUTHORIZED, request.getRequestURI(), "Incorrect username or password!");
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorDTO> handleLockedException(LockedException exception, HttpServletRequest request) {
        return getResponse(HttpStatus.UNAUTHORIZED, request.getRequestURI(), "Account locked!");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        for (ObjectError error : exception.getBindingResult().getAllErrors()) {
            builder.append(error.getDefaultMessage()).append("\n");
        }

        return getResponse(HttpStatus.BAD_REQUEST, request.getRequestURI(), builder.toString());
    }

    private ResponseEntity<ErrorDTO> getResponse(HttpStatus status, String requestURI, String message) {
        ErrorDTO errorDTO = new ErrorDTO(status, requestURI, message);
        return ResponseEntity.status(status).body(errorDTO);
    }
}
