package com.big_hackathon.backend_v2.exception;

public class UnauthorizedException extends Exception{
    
    UnauthorizedException(){}

    UnauthorizedException(String message){
        super(message);
    }
}
