package uk.ac.ed.acp.cw2.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator class that implements the validator interface.
 * Checks that if a given angle (not null) is a multiple of 22.5.
 */
public class IsAngleMultipleOfValidator implements
        ConstraintValidator<IsAngleMultipleOf, Double>
{
    @Override
    public void initialize(IsAngleMultipleOf constraintAnnotation)
    {

    }

    @Override
    public boolean isValid (Double angle, ConstraintValidatorContext context)
    {
        //If the angle is null, return true and pass it to other validators
        //The case where the angle is null is handled by @NotNull
        if (angle == null)
        {
            return true;
        }

        //If the angle is not NULL then do the check
        return angle % 22.5 == 0;
    }


}
