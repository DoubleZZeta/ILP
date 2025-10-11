package uk.ac.ed.acp.cw2.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class PositionRegionRequest
{
    @Valid
    @NotNull
    private Position position;

    @Valid
    @NotNull
    private Region region;
}
