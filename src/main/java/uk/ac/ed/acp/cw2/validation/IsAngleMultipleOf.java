package uk.ac.ed.acp.cw2.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validator Interface for checking if the angle is a multiple of 22.5
 */
@Documented
@Constraint(validatedBy = IsAngleMultipleOfValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsAngleMultipleOf
{
    String message() default "Angle is not a multiple of 22.5";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
