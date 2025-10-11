package uk.ac.ed.acp.cw2.data;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class Position
{
    @NotNull
    private Double lng;

    @NotNull
    private Double lat;

}
