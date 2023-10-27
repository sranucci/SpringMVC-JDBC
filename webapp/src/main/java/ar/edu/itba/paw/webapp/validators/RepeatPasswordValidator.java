package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.webapp.annotations.RepeatPasswordMatchesPassword;
import ar.edu.itba.paw.webapp.forms.RegisterForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RepeatPasswordValidator implements ConstraintValidator<RepeatPasswordMatchesPassword, RegisterForm> {

    @Override
    public boolean isValid(RegisterForm registerForm, ConstraintValidatorContext constraintValidatorContext) {
        if (registerForm == null || registerForm.getPassword() == null || registerForm.getRepeatPassword() == null) {
            return false;
        }
        return registerForm.getPassword().equals(registerForm.getRepeatPassword());
    }
}
