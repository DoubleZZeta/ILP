package uk.ac.ed.acp.cw2.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import uk.ac.ed.acp.cw2.validation.IsRegionClosed;

import java.util.ArrayList;

/**
 * Region class that act as a DTO for incoming region data.
 * Uses Spring Boot Bean Validations to do built-in validation check.
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
public class Region
{
    //Check that the name is not null
    @NotNull
    private String name;

    //Check that the first vertex is the same as the last vertex
    @IsRegionClosed
    //Check that the vertices contains at least 4 vertices
    @Size(min = 4)
    //Check that vertices is not NULL
    @NotNull
    private ArrayList<Position> vertices;
}
