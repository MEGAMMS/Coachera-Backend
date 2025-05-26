package com.coachera.backend.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateOrderIndexException extends RuntimeException {
    public DuplicateOrderIndexException(String message) {
        super(message);     
    }
}
