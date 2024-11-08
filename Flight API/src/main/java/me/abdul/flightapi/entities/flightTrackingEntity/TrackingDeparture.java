package me.abdul.flightapi.entities.flightTrackingEntity;

import java.util.Date;

public class TrackingDeparture {
    public String airport;
    public String timezone;
    public String iata;
    public String icao;
    public Object terminal;
    public String gate;
    public int delay;
    public Date scheduled;
    public Date estimated;
    public Date actual;
    public Date estimated_runway;
    public Date actual_runway;
}
