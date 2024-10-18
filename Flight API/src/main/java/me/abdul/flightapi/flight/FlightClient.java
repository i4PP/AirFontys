package me.abdul.flightapi.flight;


import me.abdul.flightapi.customExceptions.FlightNotFoundException;
import me.abdul.flightapi.customExceptions.FlightServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class FlightClient {

    private static final String API_URL = "https://api.aviationstack.com/v1/";

    @Value("${aviationstack.access_key}")
    private String ACCESS_KEY;

    private final RestTemplate restTemplate;

    public FlightClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<FlightData[]> fetchFlightsFromAPI(String airportDep, String date) {
        String url = String.format("%sflightsFuture?access_key=%s&iataCode=%s&type=departure&date=%s",
                API_URL, ACCESS_KEY, airportDep, date);

        try {
            FlightResponse response = restTemplate.getForObject(url, FlightResponse.class);
            if (response == null) {
                throw new FlightNotFoundException("No flights found for the given airport and date");
            }
            return Optional.of(response.data);

        } catch (FlightNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FlightServiceException("Error fetching flights from API: " + e.getMessage());
        }
    }

    public Airport fetchAirportFromAPI(String iataCode) {
        String url = String.format("%sairports?access_key=%s&search=%s",
                API_URL, ACCESS_KEY, iataCode);

        try {
            AirportResponse response = restTemplate.getForObject(url, AirportResponse.class);
            if (response == null ) {
                throw new FlightServiceException("No airports found for the given IATA code");
            }
            Airport[] airports = response.data;

            for (Airport airport : airports) {
                if (airport.getIata_code().equalsIgnoreCase(iataCode)) {
                    return airport;
                }
            }
            return airports[0]; // Return the first one if no exact match is found

        } catch (FlightServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new FlightServiceException("Error fetching airport from API: " + e.getMessage());
        }
    }
}

