package me.abdul.flightapi.entities.FlightsResponseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Departure{
    public String iataCode;
    public String icaoCode;
    public String terminal;
    public String gate;
    public String scheduledTime;
}
