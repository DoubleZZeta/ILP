package uk.ac.ed.acp.cw2.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Drone
{
    private String name;
    private Integer id;
    private Capability capability;
}
