package ru.kostapo.cloudfilestorage.exception.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<ValidName, String> {

    private static final String NAME_REGEX = "^[А-Яа-яЁёA-Za-z0-9.]+$";

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return name.matches(NAME_REGEX);
    }
}
