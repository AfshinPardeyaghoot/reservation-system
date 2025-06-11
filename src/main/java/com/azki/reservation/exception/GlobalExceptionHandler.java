package com.azki.reservation.exception;


import com.azki.reservation.dto.http.HttpResponse;
import com.azki.reservation.dto.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;


@Slf4j
@RestController
@ControllerAdvice
public class GlobalExceptionHandler
        extends ResponseEntityExceptionHandler {


    @ExceptionHandler(AuthenticationException.class)
    public final ResponseEntity<HttpResponse<?>> handleAuthenticationException(AuthenticationException ex) {
        HttpResponseStatus status = new HttpResponseStatus(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value()
        );
        return new ResponseEntity<>(
                new HttpResponse<>(status),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(DuplicateUserException.class)
    public final ResponseEntity<HttpResponse<?>> handleDuplicateUserException(DuplicateUserException ex) {
        HttpResponseStatus status = new HttpResponseStatus(
                ex.getMessage(),
                HttpStatus.CONFLICT.value()
        );
        return new ResponseEntity<>(
                new HttpResponse<>(status),
                HttpStatus.CONFLICT
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        HttpResponseStatus responseStatus = new HttpResponseStatus(
                "Request Method not supported",
                status.value()
        );
        return new ResponseEntity<>(
                new HttpResponse<>(responseStatus),
                status
        );
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        HttpResponseStatus httpResponseStatus = new HttpResponseStatus(
                ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage(),
                HttpStatus.BAD_REQUEST.value()
        );
        return new ResponseEntity<>(
                new HttpResponse<>(httpResponseStatus),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpException.class)
    public final ResponseEntity<HttpResponse<?>> handleHttpException(HttpException ex) {
        HttpResponseStatus status = new HttpResponseStatus(
                ex.getMessage(),
                ex.getStatus().value()
        );
        return new ResponseEntity<>(
                new HttpResponse<>(status),
                ex.getStatus()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<HttpResponse<?>> handleNotFoundException(NotFoundException ex) {
        HttpResponseStatus status = new HttpResponseStatus(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value()
        );
        return new ResponseEntity<>(
                new HttpResponse<>(status),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(InternalServerException.class)
    public final ResponseEntity<HttpResponse<?>> handleInternalServerException(InternalServerException ex) {
        log.error("InternalServerException {} message is {}", ex.getCode(), ex.getMessage());
        ex.printStackTrace();
        HttpResponseStatus status = new HttpResponseStatus(
                "Internal Server Error with code: " + ex.getCode(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return new ResponseEntity<>(
                new HttpResponse<>(status),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<HttpResponse<?>> handleAllExceptions(Exception ex) {
        if (ex.getClass().equals(org.springframework.security.access.AccessDeniedException.class)) {
            HttpResponseStatus status = new HttpResponseStatus(
                    "Access Denied",
                    HttpStatus.FORBIDDEN.value()
            );
            return new ResponseEntity<>(
                    new HttpResponse<>(status),
                    HttpStatus.FORBIDDEN
            );
        } else {
            ex.printStackTrace();
            HttpResponseStatus status = new HttpResponseStatus(
                    "Internal Server Error",
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
            return new ResponseEntity<>(
                    new HttpResponse<>(status),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

    }

}

