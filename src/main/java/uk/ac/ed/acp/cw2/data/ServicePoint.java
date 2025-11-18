package uk.ac.ed.acp.cw2.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class ServicePoint
{
    private String name;
    private Integer id;
    private Position location;
}
