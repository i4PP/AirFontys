package me.abdul.flightapi.sockets;

import com.fasterxml.jackson.databind.JsonNode;
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
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Parse the JSON payload
            JsonNode jsonNode = objectMapper.readTree(message.getPayload());

            // Extract values
            String flightNumber = jsonNode.get("flightCode").asText();
            String date = jsonNode.get("depDate").asText();

            // Store the values in the session attributes
            session.getAttributes().put("flightNumber", flightNumber);
            session.getAttributes().put("date", date);

            // Send the flight data
            sendFlightData();
        } catch (Exception e) {
            throw new SocketException("Invalid JSON message format: " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 7000)
    public void sendFlightData() {
        for (WebSocketSession session : sessions) {
            try {
                String flightNumber = (String) session.getAttributes().get("flightNumber");
                String date = (String) session.getAttributes().get("date");

                FlightTrackingResponse flightData = flightService.getFlightTracking(flightNumber, date).orElseThrow(() -> new SocketException("Flight not found"));
                if (flightData == null) {
                    throw new SocketException("Flight not found");
                }
                ObjectMapper mapper = new ObjectMapper();
                String flightDataJson = mapper.writeValueAsString(flightData);

                session.sendMessage(new TextMessage(flightDataJson));

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
