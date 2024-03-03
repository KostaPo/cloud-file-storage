package ru.kostapo.cloudfilestorage.exception;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class NonUniqConstraintException extends RuntimeException {

    private final BindingResult bindingResult;

    public NonUniqConstraintException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public NonUniqConstraintException(String message, BindingResult bindingResult) {
        super(message);
        this.bindingResult = bindingResult;
    }

    public NonUniqConstraintException(String message, Throwable cause, BindingResult bindingResult) {
        super(message, cause);
        this.bindingResult = bindingResult;
    }
}
