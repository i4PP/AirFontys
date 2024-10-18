package me.abdul.flightapi.flight;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Arrival{
    public String iataCode;
    public String icaoCode;
    public String terminal;
    public String gate;
    public String scheduledTime;
}
