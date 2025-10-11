package uk.ac.ed.acp.cw2.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = isAngleMultipleOfValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsAngleMultipleOf
{
    String message() default "Angle is not a multiple of 22.5";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
