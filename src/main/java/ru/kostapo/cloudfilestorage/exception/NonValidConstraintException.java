package ru.kostapo.cloudfilestorage.exception;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class NonValidConstraintException extends RuntimeException {

    private final BindingResult bindingResult;

    public NonValidConstraintException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }
    public NonValidConstraintException(String message, BindingResult bindingResult) {
        super(message);
        this.bindingResult = bindingResult;
    }

    public NonValidConstraintException(String message, Throwable cause, BindingResult bindingResult) {
        super(message, cause);
        this.bindingResult = bindingResult;
    }
}
