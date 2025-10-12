package uk.ac.ed.acp.cw2.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsRegionClosedValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsRegionClosed
{
    String message() default "The region is not Closed";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
