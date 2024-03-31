package ru.kostapo.cloudfilestorage.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import ru.kostapo.cloudfilestorage.entity.dto.UserReqDto;

import java.util.Arrays;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public String handleMaxUploadSizeExceededException(@AuthenticationPrincipal User user,
                                                       MaxUploadSizeExceededException ex,
                                                       Model model) {
        model.addAttribute("user", user);
        model.addAttribute("message", "Превышен максимально допустимый размер файла!");
        log.error("MaxUploadSizeExceededException: " + ex.getMessage());
        return "index";
    }

    @ExceptionHandler(NonValidConstraintException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleValidationExceptions(NonValidConstraintException ex, Model model) {
        model.addAttribute("userDto", new UserReqDto());
        model.addAttribute("errors", ex.getBindingResult());
        log.error("NonValidConstraintException: " + ex.getBindingResult());
        return "registration";
    }

    @ExceptionHandler(NonUniqConstraintException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDataIntegrityViolationException(NonUniqConstraintException ex, Model model) {
        ex.getBindingResult()
                .rejectValue("username", "ViolationException", "Логин: логин занят!");
        model.addAttribute("userDto", new UserReqDto());
        model.addAttribute("errors", ex.getBindingResult());
        log.error("NonValidConstraintException: " + ex.getBindingResult());
        return "registration";
    }
}
