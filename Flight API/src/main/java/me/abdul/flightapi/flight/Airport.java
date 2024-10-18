package me.abdul.flightapi.flight;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Airport {
    public String iata_code;
    public String icao_code;
    public double latitude;
    public double longitude;
    public String airport_name;
    public String country_name;
}
