import {Aircraft} from "./Aircraft.ts";
import {Departure} from "./Departure.ts";
import {Arrival} from "./Arrival.ts";
import {Flight} from "./Flight.ts";

export type FlightData = {
    flight: Flight
    arrival: Arrival
    departure: Departure
    aircraft: Aircraft
}