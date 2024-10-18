package me.abdul.flightapi.flight;

import me.abdul.flightapi.customExceptions.FlightNotFoundException;
import me.abdul.flightapi.customExceptions.FlightServiceException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.lang.Math.round;

@Service
public class FlightService {

    private final FlightClient flightClient;

    public FlightService(FlightClient flightClient) {
        this.flightClient = flightClient;
    }

    public List<FlightData> getFlightsByAirportAndDate(String airportArrv, String airportDep, String date) {
        List<FlightData> flightsRoot = new ArrayList<>();
        final long startTime = System.currentTimeMillis();
        Random rand = new Random();
        try {
            // fetch airport data from API TIME
            final long airportsFetchStartTime = System.currentTimeMillis();
            Airport airportArrival = flightClient.fetchAirportFromAPI(airportArrv);
            Airport airportDeparture = flightClient.fetchAirportFromAPI(airportDep);
            final long airportsFetchEndTime = System.currentTimeMillis();
            System.out.println("Time taken to fetch airports: " + (airportsFetchEndTime - airportsFetchStartTime) + "ms");

            // calculate distance between two airports time
            final long distanceCalculationStartTime = System.currentTimeMillis();
            double distance = calculateDistance(airportArrival, airportDeparture);
            final long distanceCalculationEndTime = System.currentTimeMillis();
            System.out.println("Time taken to calculate distance: " + (distanceCalculationEndTime - distanceCalculationStartTime) + "ms");

            // fetch flights from API time
            final long flightsFetchStartTime = System.currentTimeMillis();
            Optional<FlightData[]> optionalFlights = flightClient.fetchFlightsFromAPI(airportDep, date);
            final long flightsFetchEndTime = System.currentTimeMillis();
            System.out.println("Time taken to fetch flights: " + (flightsFetchEndTime - flightsFetchStartTime) + "ms");

            // filter flights by arrival airport and add distance time
            final long filterFlightsStartTime = System.currentTimeMillis();
            if (optionalFlights.isPresent()) {
                FlightData[] flightsFromAPI = optionalFlights.get();
                for (FlightData flightFromAPI : flightsFromAPI) {
                    if (flightFromAPI.arrival.getIataCode().equalsIgnoreCase(airportArrv)) {
                        double price = distance * rand.nextDouble(0.031, 0.057);
                        price = round(price * 100.0) / 100.0; // round to 2 decimal places
                        flightFromAPI.flight.setPrice(price);
                        flightFromAPI.flight.setDistance(distance);
                        flightsRoot.add(flightFromAPI);
                    }
                }
            }
            final long filterFlightsEndTime = System.currentTimeMillis();
            System.out.println("Time taken to filter flights: " + (filterFlightsEndTime - filterFlightsStartTime) + "ms");
            if (flightsRoot.isEmpty()) {
                throw new FlightNotFoundException("No flights found for the given airports and date" + airportDep + " " + airportArrv + " " + date);
            }
        } catch (FlightNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FlightServiceException("Error fetching flights: " + e.getMessage());
        }
        final long endTime = System.currentTimeMillis();
        System.out.println("Total time taken: " + (endTime - startTime) + "ms");
        return flightsRoot;
    }

    private double calculateDistance(Airport airport1, Airport airport2) {
        double lat1 = airport1.getLatitude();
        double lon1 = airport1.getLongitude();
        double lat2 = airport2.getLatitude();
        double lon2 = airport2.getLongitude();

        // calculate distance between two airports using haversine formula
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = haversine(dLat) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * haversine(dLon);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = 6371 * c;

        //round to 2 decimal places
        distance = round(distance * 100.0) / 100.0;
        return distance;
    }

    private double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
}

