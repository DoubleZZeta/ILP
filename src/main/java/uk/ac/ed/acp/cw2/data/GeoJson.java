package uk.ac.ed.acp.cw2.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@Setter
@Getter
public class GeoJson
{
    private String type;
    private ArrayList<ArrayList<Double>> coordinates;
}
