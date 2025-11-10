package uk.ac.ed.acp.cw2.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
@Setter //TODO consider change all DTOs to record and delete this (only used in some tests)
public class MedicineDispatchRequest
{
    private Integer id;
    private LocalDate date;
    private LocalTime time;
    private Requirements requirements;
    private Position delivery;
}
