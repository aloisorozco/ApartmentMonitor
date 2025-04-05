package com.big_hackathon.backend_v2.exception;

public class InvalidParamsException extends Exception {
    
    InvalidParamsException(){}
    
    InvalidParamsException(String ...invalidArgs){
        super(invalidArgs.toString());
    }
}
