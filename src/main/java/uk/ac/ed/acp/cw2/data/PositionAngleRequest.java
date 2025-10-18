package uk.ac.ed.acp.cw2.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;
import uk.ac.ed.acp.cw2.validation.IsAngleMultipleOf;

/**
 * PositionAngleRequest class that act as a DTO for the type of request used by nextPositon end point.
 * Uses Spring Boot Bean Validations to do built-in validation check.
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
public class PositionAngleRequest
{
    //Using the validation check in Position class to validate start
    @Valid
    //Check that start is not null
    @NotNull
    private Position start;

    //Check that the angle is a multiple of 22.5
    @IsAngleMultipleOf
    //Check the angle is in the 16 directions, 360 not included
    @Range(min = 0, max = 338)
    //check that the angle is not null
    @NotNull
    private Double angle;
}
