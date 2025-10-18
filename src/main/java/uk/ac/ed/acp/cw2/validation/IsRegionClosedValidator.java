package uk.ac.ed.acp.cw2.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.ac.ed.acp.cw2.data.Position;

import java.util.ArrayList;
import java.util.Objects;
/**
 * Validator class that implements the validator interface.
 * Checks that if a given region is closed
 */
public class IsRegionClosedValidator implements
        ConstraintValidator<IsRegionClosed, ArrayList<Position>>
{
    @Override
    public void initialize(IsRegionClosed constraintAnnotation)
    {

    }

    @Override
    public boolean isValid (ArrayList<Position> vertices,  ConstraintValidatorContext context)
    {
        //If the region is null, return true and pass it to other validators
        //The case where the region is null is handled by @NotNull
        if (Objects.isNull(vertices))
        {
            return true;
        }

        Position first = vertices.getFirst();
        Position last = vertices.getLast();

        //If the angle is not NULL then validate by checking if the first and the last vertex are the same.
        return Objects.equals(first.getLng(), last.getLng()) && Objects.equals(first.getLat(), last.getLat());
    }


}
