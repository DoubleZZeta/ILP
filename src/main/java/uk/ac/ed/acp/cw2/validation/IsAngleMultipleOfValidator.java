package uk.ac.ed.acp.cw2.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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
        return angle % 22.5 == 0;
    }


}
