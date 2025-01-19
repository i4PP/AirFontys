package me.abdul.flightapi.clients;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.abdul.flightapi.customExceptions.FlightNotFoundException;
import me.abdul.flightapi.customExceptions.FlightServiceException;
import me.abdul.flightapi.entities.FlightsResponseEntity.Airport;
import me.abdul.flightapi.entities.FlightsResponseEntity.AirportResponse;
import me.abdul.flightapi.entities.FlightsResponseEntity.FlightData;
import me.abdul.flightapi.entities.FlightsResponseEntity.FlightResponse;
import me.abdul.flightapi.entities.flightTrackingEntity.FlightTrackingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class FlightClient {

    private static final String API_URL = "https://api.aviationstack.com/v1/";

    @Value("${use.mock.data:false}")
    private boolean useMockData;

    @Value("classpath:templates/Flights.json")
    private String mockFlightsPath;

    @Value("classpath:templates/Airports.json")
    private String mockAirportsPath;

    @Value("classpath:templates/WSFlights.json")
    private String mockWSFlightPath;

    @Value("${aviationstack.access_key}")
    private String ACCESS_KEY;

    private final RestTemplate restTemplate;
    private final ResourceLoader resourceLoader;


    public FlightClient(RestTemplate restTemplate, ResourceLoader resourceLoader) {
        this.restTemplate = restTemplate;
        this.resourceLoader = resourceLoader;
    }

    public Optional<FlightData[]> fetchFlightsFromAPI(String airportDep, String date) {
        if (useMockData) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                Resource resource = resourceLoader.getResource(mockFlightsPath);
                FlightResponse response = mapper.readValue(resource.getInputStream(), FlightResponse.class);
                FlightData[] flights = response.data;
                return Optional.of(flights);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


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

    public Airport fetchAirportFromAPI(String iataCode) throws IOException {
        String url = String.format("%sairports?access_key=%s&search=%s",
                API_URL, ACCESS_KEY, iataCode);

        if (useMockData) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Resource resource = resourceLoader.getResource(mockAirportsPath);
            AirportResponse jsonResponse = mapper.readValue(resource.getInputStream(), AirportResponse.class);
            Airport[] airports = jsonResponse.data;
            if(iataCode.equals("AMS")){
                return airports[1];
            }
            if(iataCode.equals("JFK")) {
                return airports[0];
            }
        }

        try {
            AirportResponse response = restTemplate.getForObject(url, AirportResponse.class);
            if (response == null) {
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

    public Optional<FlightResponse> fetchActiveFlightNow(){
        if (useMockData) {
            FlightResponse response = new FlightResponse();
            //randomly generate a number between 1 and 10
            int total = (int) (Math.random() * 10000 + 300);
            response.pagination.setTotal(total);
            return Optional.of(response);

        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = LocalDate.now().format(formatter);
        String url = String.format("%sflights?access_key=%s&flight_date=%s&flight_status=active",
                API_URL, ACCESS_KEY, formattedDate);

        try {
            FlightResponse response = restTemplate.getForObject(url, FlightResponse.class);
            if (response == null) {
                throw new FlightNotFoundException("No active flights found");
            }
            return Optional.of(response);

        } catch (FlightNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FlightServiceException("Error fetching active flights from API: " + e.getMessage());
        }
    }

    public Optional<FlightTrackingResponse> fetchFlightStatus(String flightNumber, String date){
        if (useMockData && flightNumber.equals("WS")) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            try {
                Resource resource = resourceLoader.getResource(mockWSFlightPath);
                FlightTrackingResponse response = mapper.readValue(resource.getInputStream(), FlightTrackingResponse.class);
                return Optional.of(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String url = String.format("%sflights?access_key=%s&flight_iata=%s&flight_date=%s",
                API_URL, ACCESS_KEY, flightNumber, date);

        try {
            FlightTrackingResponse response = restTemplate.getForObject(url, FlightTrackingResponse.class);
            if (response == null) {
                throw new FlightNotFoundException("No flight found for the given flight number and date");
            }
            return Optional.of(response);

        } catch (FlightNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FlightServiceException("Error fetching flight status from API: " + e.getMessage());
        }
    }
}

