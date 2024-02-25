package ru.kostapo.cloudfilestorage.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.kostapo.cloudfilestorage.entity.dto.UserReqDto;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NonValidConstraintException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleValidationExceptions(NonValidConstraintException ex, Model model) {
        model.addAttribute("userDto", new UserReqDto());
        model.addAttribute("errors", ex.getBindingResult());
        log.error("NonValidConstraintException: " + ex.getBindingResult());
        return "registration";
    }

    @ExceptionHandler(NonUniqConstraintException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleDataIntegrityViolationException(NonUniqConstraintException ex, Model model) {
        ex.getBindingResult()
                .rejectValue("username", "ViolationException", "Логин: логин занят!");
        model.addAttribute("userDto", new UserReqDto());
        model.addAttribute("errors", ex.getBindingResult());
        log.error("NonValidConstraintException: " + ex.getBindingResult());
        return "registration";
    }
}