package me.abdul.flightapi.flight;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
public class FlightsController {

    private final FlightService flightService;

    public FlightsController(FlightService flightService) {
        this.flightService = flightService;
    }


    @Operation(summary = "Retrieve list of flights")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of flights",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FlightData.class),
                            examples = @ExampleObject(value = "[{\"flight\": {\"number\": \"ABC123\",\"iataNumber\": \"AB123\",\"icaoNumber\": \"ABC123\",\"distance\": 500},\"arrival\": {\"iataCode\": \"JFK\",\"icaoCode\": \"KJFK\",\"terminal\": \"T1\",\"gate\": \"G12\",\"scheduledTime\": \"2023-09-16T14:30:00\"},\"departure\": {\"iataCode\": \"LAX\",\"icaoCode\": \"KLAX\",\"terminal\": \"T4\",\"gate\": \"D5\",\"scheduledTime\": \"2023-09-16T10:00:00\"},\"aircraft\": {\"modelCode\": \"B737\",\"modelText\": \"Boeing 737\"}}]"))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Flights not found",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping(value = "/flights", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FlightData>> getFlightsByAirportAndDate(@RequestParam @NotBlank @Size(min = 3, max = 3) String airportArrv, @RequestParam @NotBlank @Size(min = 3, max = 3) String airportDep, @RequestParam @NotBlank String date) {
        List<FlightData> flights = flightService.getFlightsByAirportAndDate(airportArrv, airportDep, date);
        return ResponseEntity.ok(flights);
    }

}
