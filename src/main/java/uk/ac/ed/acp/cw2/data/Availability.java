package uk.ac.ed.acp.cw2.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.DayOfWeek;
import java.time.LocalTime;

@AllArgsConstructor
@Setter
@Getter

public class Availability
{
    private DayOfWeek dayOfWeek;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime from;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime until;
}
