package uk.ac.ed.acp.cw2.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uk.ac.ed.acp.cw2.data.Position;

import java.util.ArrayList;
import java.util.Objects;

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
        Position first = vertices.getFirst();
        Position last = vertices.getLast();

        return Objects.equals(first.getLng(), last.getLng()) && Objects.equals(first.getLat(), last.getLat());
    }


}
