package uk.ac.ed.acp.cw2.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Map;

@AllArgsConstructor
@Setter
@Getter
public class Feature
{
    private String type;
    private Map<String, Object> properties;
    private Geometry geometry;
}
