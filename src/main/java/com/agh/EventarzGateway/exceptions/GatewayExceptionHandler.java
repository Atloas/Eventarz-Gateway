package com.agh.EventarzGateway.exceptions;

import com.agh.EventarzGateway.model.dtos.ErrorDTO;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

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
