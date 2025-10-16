package uk.ac.ed.acp.cw2.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import uk.ac.ed.acp.cw2.validation.IsAngleMultipleOf;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class PositionAngleRequest
{
    @Valid
    @NotNull
    private Position start;

    @IsAngleMultipleOf
    @NotNull
    private Double angle;
}
