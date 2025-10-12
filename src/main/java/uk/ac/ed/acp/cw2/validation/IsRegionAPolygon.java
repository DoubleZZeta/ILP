package uk.ac.ed.acp.cw2.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IsRegionAPolygonValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsRegionAPolygon
{
    String message() default "The region is not a polygon";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
