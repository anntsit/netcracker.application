package com.gmail.netcracker.application.validation;

import com.gmail.netcracker.application.dto.model.Event;
import com.gmail.netcracker.application.utilites.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EventValidator extends ModelValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return Event.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Event event = (Event) o;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "required.field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "required.field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateStart", "required.field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateEnd", "required.field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", "required.field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "eventPlaceName", "required.field");
        if (!errors.hasErrors()) {
            if (compareDate(event)) {
                errors.rejectValue("dateEnd", "required.date");
            }
            validateEntity(event, errors);
        }
    }

    private boolean compareDate(Event event) {
        boolean status = false;
        Timestamp startTime = Utilities.parseStringToTimestamp(event.getDateStart());
        Timestamp endTime = Utilities.parseStringToTimestamp(event.getDateEnd());
        if (startTime != null && endTime != null) {
            if (endTime.before(startTime)) {
                status = true;
            }
        }
        return status;
    }
}