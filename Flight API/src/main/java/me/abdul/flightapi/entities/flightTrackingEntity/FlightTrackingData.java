package me.abdul.flightapi.entities.flightTrackingEntity;

import me.abdul.flightapi.entities.FlightsResponseEntity.Aircraft;
import me.abdul.flightapi.entities.FlightsResponseEntity.Arrival;
import me.abdul.flightapi.entities.FlightsResponseEntity.Departure;
import me.abdul.flightapi.entities.FlightsResponseEntity.Flight;

public class FlightTrackingData {
    public String flight_date;
    public String flight_status;
    public TrackingDeparture departure;
    public TrackingArrival arrival;
    public Airline airline;
    public FlightTracking flight;
    public AircraftTracking aircraft;
    public Live live;
}
