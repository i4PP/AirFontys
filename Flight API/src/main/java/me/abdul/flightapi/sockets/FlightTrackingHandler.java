package me.abdul.flightapi.sockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.abdul.flightapi.customExceptions.SocketException;
import me.abdul.flightapi.entities.flightTrackingEntity.FlightTrackingResponse;
import me.abdul.flightapi.services.FlightService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FlightTrackingHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final FlightService flightService;

    public FlightTrackingHandler(FlightService flightService) {
        this.flightService = flightService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String[] messageParts = message.getPayload().split(",");
            if (messageParts.length != 2) {
                throw new SocketException("Invalid message format: " + message.getPayload());
            }
            String flightNumber = messageParts[0].trim();
            String date = messageParts[1].trim();

            session.getAttributes().put("flightNumber", flightNumber);
            session.getAttributes().put("date", date);

        } catch (SocketException e) {
            sendErrorMessage(session, "Invalid message format");
        }
    }

    @Scheduled(fixedRate = 10000)
    public void sendFlightData() {
        ObjectMapper mapper = new ObjectMapper();

        // Mock data JSON string
        String mockJson = """
        {
            "pagination": {
                "total": 1
            },
            "data": [
                {
                    "flight_date": "2024-11-04",
                    "flight_status": "active",
                    "departure": {
                        "airport": "Hamburg Airport",
                        "timezone": "Europe/Berlin",
                        "iata": "HAM",
                        "icao": "EDDH",
                        "delay": 8,
                        "scheduled": 1730725200000,
                        "estimated": 1730725200000,
                        "actual": 1730725680000,
                        "estimated_runway": 1730725680000,
                        "actual_runway": 1730725680000
                    },
                    "arrival": {
                        "airport": "Castellon",
                        "timezone": "Europe/Madrid",
                        "iata": "CDT",
                        "icao": "LECH",
                        "delay": 1,
                        "scheduled": 1730734800000,
                        "estimated": 1730734800000
                    },
                    "airline": {
                        "name": "Eurowings",
                        "iata": "EW",
                        "icao": "EWG"
                    },
                    "flight": {
                        "number": "6969",
                        "iata": "EW6969",
                        "icao": "EWG6969"
                    },
                    "aircraft": {
                        "registration": "9H-MLS",
                        "iata": "A320",
                        "icao": "A320",
                        "icao24": "4D23A2"
                    },
                    "live": {
                        "updated": 1730725362000,
                        "latitude": 49.2818,
                        "longitude": 4.2627,
                        "altitude": 11887.2,
                        "direction": 243.0,
                        "speed_horizontal": 779.692,
                        "speed_vertical": 0,
                        "is_ground": false
                    }
                }
            ]
        }
        """;

        for (WebSocketSession session : sessions) {
            try {
                // Map the mock JSON data to FlightTrackingResponse class
                FlightTrackingResponse response = mapper.readValue(mockJson, FlightTrackingResponse.class);
                String responseJson = mapper.writeValueAsString(response);

                // Send the mock response to the client
                session.sendMessage(new TextMessage(responseJson));

            } catch (IOException e) {
                sendErrorMessage(session, "Failed to send flight data");
            }
        }
    }

    private void sendErrorMessage(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            throw new SocketException("Failed to send error message");
        }
    }
}
