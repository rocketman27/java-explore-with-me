package ru.practicum.event.validation;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.metadata.ConstraintDescriptor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventDateValidator implements ConstraintValidator<ValidEventDate, CharSequence> {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        ConstraintDescriptor<?> descriptor = ((ConstraintValidatorContextImpl) context).getConstraintDescriptor();
        boolean nullable = (boolean) descriptor.getAttributes().get("nullable");

        if (nullable) {
            return true;
        }

        long hours = (long) descriptor.getAttributes().get("value");

        LocalDateTime eventDate = LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        return eventDate.isAfter(LocalDateTime.now().plusHours(hours));
    }
}
