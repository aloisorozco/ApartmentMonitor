package com.big_hackathon.backend_v2.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// For now well keep a single centralized error middleware - well scale and make more if we see this file get too big and mixed up
@RestControllerAdvice
public class ErrorMiddleware {
    
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUserAuth(UnauthorizedException ue){
        return "Email or password is invalid";
    }

    @ExceptionHandler(InvalidParamsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidParams(InvalidParamsException ue){
        return "Invalid Arguments recieved: " + ue.getMessage();
    }

    @ExceptionHandler(DatabseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String DatabseException(DatabseException ue){
        return "Database Error";
    }
}
