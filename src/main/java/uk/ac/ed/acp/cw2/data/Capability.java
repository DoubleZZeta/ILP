package uk.ac.ed.acp.cw2.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Capability
{
    private Boolean cooling;

    private Boolean heating;

    private Integer capacity;

    private Integer maxMoves;

    private Double costPerMove;

    private Double costInitial;

    private Double costFinal;
}
