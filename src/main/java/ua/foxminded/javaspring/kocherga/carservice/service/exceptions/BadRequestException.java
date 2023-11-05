package ua.foxminded.javaspring.kocherga.carservice.service.exceptions;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
