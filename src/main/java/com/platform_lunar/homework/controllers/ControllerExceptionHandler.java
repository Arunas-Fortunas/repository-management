package com.platform_lunar.homework.controllers;

import com.platform_lunar.homework.controllers.exceptions.DataRetrievalException;
import com.platform_lunar.homework.controllers.exceptions.DataUpdateException;
import com.platform_lunar.homework.controllers.exceptions.UpstreamServerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
@ResponseBody
class ControllerExceptionHandler {
    @ExceptionHandler(DataRetrievalException.class)
    public ResponseEntity<String> handle(DataRetrievalException ex) {
        return new ResponseEntity<>(BAD_GATEWAY);
    }

    @ExceptionHandler(DataUpdateException.class)
    public ResponseEntity<String> handle(DataUpdateException ex) {
        return new ResponseEntity<>(BAD_GATEWAY);
    }

    @ExceptionHandler(UpstreamServerException.class)
    public ResponseEntity<String> handle(UpstreamServerException ex) {
        return new ResponseEntity<>(BAD_GATEWAY);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException ex) {
        return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
    }
}

