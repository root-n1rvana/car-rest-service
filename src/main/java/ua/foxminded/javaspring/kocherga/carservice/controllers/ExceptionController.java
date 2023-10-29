package ua.foxminded.javaspring.kocherga.carservice.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.foxminded.javaspring.kocherga.carservice.service.exceptions.BadRequestException;

@ControllerAdvice
public class ExceptionController {

    private final Logger LOG = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException ex) {
        LOG.error(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
