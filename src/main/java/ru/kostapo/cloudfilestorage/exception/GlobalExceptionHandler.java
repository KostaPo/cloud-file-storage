package ru.kostapo.cloudfilestorage.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.NestedServletException;
import ru.kostapo.cloudfilestorage.entity.dto.UserReqDto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.error("MaxUploadSizeExceededException: " + ex.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Connection", "close");
        Map<String, String> response = new HashMap<>();
        response.put("message", String.format("Размер запроса больше %s мб!", 1000));
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .headers(headers)
                .body(response);
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
