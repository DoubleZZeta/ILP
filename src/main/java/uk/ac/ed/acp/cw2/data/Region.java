package uk.ac.ed.acp.cw2.data;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import uk.ac.ed.acp.cw2.validation.IsRegionAPolygon;
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

    @NotNull
    @IsRegionClosed
    //@IsRegionAPolygon
    private ArrayList<Position> vertices;
}
