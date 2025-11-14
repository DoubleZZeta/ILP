package uk.ac.ed.acp.cw2.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@Setter
@Getter
public class ReturnedPath
{
    private Double totalCost;
    private Integer totalMoves;
    private ArrayList<DronePath> dronePaths;
}
