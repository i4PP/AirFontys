package me.abdul.flightapi.flight;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightData {
    public Flight flight;
    public Arrival arrival;
    public Departure departure;
    public Aircraft aircraft;

}