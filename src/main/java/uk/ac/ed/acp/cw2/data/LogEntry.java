package uk.ac.ed.acp.cw2.data;


import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LogEntry
{
    private Integer deliveryId;

    private String droneId;

    private Double servicePointLng;
    private Double servicePointLat;

    private Integer servicePointId;

    private String servicePointName;

    private Double deliveryPointLng;
    private Double deliveryPointLat;

    private LocalDate deliveryDate;

    private LocalTime deliveryTime;

    private Double actualCost;

    private Double requiredCapacity;

    private Boolean coolingRequired;

    private Boolean heatingRequired;
}
