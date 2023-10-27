package ar.edu.itba.paw.webapp.annotations;


import ar.edu.itba.paw.webapp.validators.MeasureIdArrayValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MeasureIdArrayValidator.class)
public @interface ValidMeasureIdArray {

    String message() default "It is an invalid measure";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
