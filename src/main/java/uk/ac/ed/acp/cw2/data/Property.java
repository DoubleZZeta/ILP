package uk.ac.ed.acp.cw2.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
@AllArgsConstructor
public class Property
{
    private String droneId;
    private Double totalDistance;
    private Integer totalMoves;
    private Double totalCost;
    private ArrayList<Integer> deliverIds;
}
