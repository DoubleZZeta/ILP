package uk.ac.ed.acp.cw2.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * PositionsRequest class that act as a DTO for the type of request used by distanceTo and isClose to end points.
 * Uses Spring Boot Bean Validations to do built-in validation check.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class PositionsRequest
{
    //Using the validation check in Position class to validate position1
    @Valid
    //Check that position1 is not NULL
    @NotNull
    private Position position1;

    //Using the validation check in Position class to validate position2
    @Valid
    //check that position2 is not NULL
    @NotNull
    private Position position2;
}
