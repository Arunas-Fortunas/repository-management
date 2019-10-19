package com.platform_lunar.homework.controllers.exceptions;

public class UpstreamServerException extends RuntimeException {
    public UpstreamServerException(String message) {
        super(message);
    }
}
