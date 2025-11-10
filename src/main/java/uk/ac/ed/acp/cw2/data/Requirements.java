package uk.ac.ed.acp.cw2.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Setter
@Getter
public class Requirements
{
    private Double capacity;
    private Boolean cooling;
    private Boolean heating;
    private Double maxCost;
}
