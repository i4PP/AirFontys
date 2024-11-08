package me.abdul.flightapi.entities.flightTrackingEntity;

import java.util.Date;

public class TrackingArrival {
    public String airport;
    public String timezone;
    public String iata;
    public String icao;
    public String terminal;
    public Object gate;
    public Object baggage;
    public int delay;
    public Date scheduled;
    public Date estimated;
    public Object actual;
    public Object estimated_runway;
    public Object actual_runway;
}
