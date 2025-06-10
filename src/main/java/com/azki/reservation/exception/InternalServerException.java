package com.azki.reservation.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException
        extends RuntimeException {

    private String code;

    public InternalServerException(String code, String message) {
        super(message);
        this.code = code;
    }
}
