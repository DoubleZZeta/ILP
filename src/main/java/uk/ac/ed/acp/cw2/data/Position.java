package uk.ac.ed.acp.cw2.data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class Position
{
    @NotNull
    @Range(min = -180, max = 180)
    private Double lng;

    @NotNull
    @Range(min = -90, max = 90)
    private Double lat;

}
