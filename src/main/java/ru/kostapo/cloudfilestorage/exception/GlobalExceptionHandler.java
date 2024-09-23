package ru.kostapo.cloudfilestorage.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kostapo.cloudfilestorage.entity.dto.UserReqDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NonValidConstraintException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleNonValidConstraintException(NonValidConstraintException ex, Model model) {
        model.addAttribute("userDto", new UserReqDto());
        model.addAttribute("errors", ex.getBindingResult());
        log.error("NonValidConstraintException: " + ex.getBindingResult());
        return "registration";
    }

    @ExceptionHandler(NonUniqConstraintException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleNonUniqConstraintException(NonUniqConstraintException ex, Model model) {
        ex.getBindingResult().rejectValue("username", "ViolationException", "Логин: логин занят!");
        model.addAttribute("userDto", new UserReqDto());
        model.addAttribute("errors", ex.getBindingResult());
        log.error("NonUniqConstraintException: " + ex.getBindingResult());
        return "registration";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, Model model) {
        log.error("MaxUploadSizeExceededException: " + ex.getMessage());
        model.addAttribute("message", "ПРЕВЫШЕН МАКСИМАЛЬНЫЙ РАЗМЕР ЗАПРОСА!");
        return "error";
    }

    @ExceptionHandler(StorageException.class)
    public String handleStorageException(StorageException ex, HttpServletRequest request,
                                         RedirectAttributes redirectAttributes) {
        log.error("StorageException: " + ex.getMessage());
        String path = request.getParameter("path") != null ? request.getParameter("path") : "";
        redirectAttributes.addFlashAttribute("message", ex.getMessage());
        return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public String handleFileNotFoundException(FileNotFoundException ex, HttpServletRequest request,
                                              RedirectAttributes redirectAttributes) {
        log.error("FileNotFoundException: " + ex.getMessage());
        String path = request.getParameter("path") != null ? request.getParameter("path") : "";
        redirectAttributes.addFlashAttribute("message", ex.getMessage());
        return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request,
                                                     RedirectAttributes redirectAttributes) {
        log.error("ConstraintViolationException: " + ex.getConstraintViolations());
        String path = request.getParameter("path") != null ? request.getParameter("path") : "";
        redirectAttributes.addFlashAttribute("message",
                ex.getConstraintViolations().iterator().next().getMessage());
        return "redirect:/?path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}
