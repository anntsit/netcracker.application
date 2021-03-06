package com.gmail.netcracker.application.validation;

import com.gmail.netcracker.application.dto.model.User;
import com.gmail.netcracker.application.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component
public class ResetConfirmPasswordValidator extends ModelValidator implements Validator {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required.field");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "required.field");
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "match.password");
        }
        if (passwordEncoder.matches(user.getPassword(), (userService.findUserByEmail(user.getEmail()).getPassword()))) {
            errors.rejectValue("confirmPassword", "password.actual");
        }
        validateEntity(user, errors, true);
    }
}
