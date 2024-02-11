package ru.kostapo.cloudfilestorage.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Log4j2
@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exception(Exception exception, Model model) {
        String errorMessage = (exception != null ? exception.getMessage() : "Unknown error");
        model.addAttribute("errorMessage", errorMessage);
        log.info("GET EXCEPTION WITH MESSAGE:" + errorMessage);
        return "error";
    }
}