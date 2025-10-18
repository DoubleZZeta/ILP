package uk.ac.ed.acp.cw2.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * PositionRegionRequest class that act as a DTO for the type of request used by isInRegion end point.
 * Uses Spring Boot Bean Validations to do built-in validation check.
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
public class PositionRegionRequest
{
    //Using the validation check in Position class to validate position
    @Valid
    //Check that position is not NULL
    @NotNull
    private Position position;

    //Using the validation check in Region class to validate region
    @Valid
    //check that region is not NULL
    @NotNull
    private Region region;
}
