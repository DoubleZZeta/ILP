package uk.ac.ed.acp.cw2.data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import uk.ac.ed.acp.cw2.validation.IsRegionClosed;

import java.util.ArrayList;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class Region
{
    @NotNull
    private String name;

    @IsRegionClosed
    @Size(min = 4)
    @NotNull
    private ArrayList<Position> vertices;
}
