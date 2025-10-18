package uk.ac.ed.acp.cw2.data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

/**
 * Position class that act as a DTO for incoming position data.
 * Uses Spring Boot Bean Validations to do built-in validation check.
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
public class Position
{
    //Check that Longitude of Positions is not NULL
    @NotNull
    //Longitude should between -180 and 180
    @Range(min = -180, max = 180)
    private Double lng;

    //Check that Latitude of Positions is not NULL
    @NotNull
    //Latitude should between -90 and 90
    @Range(min = -90, max = 90)
    private Double lat;

}
