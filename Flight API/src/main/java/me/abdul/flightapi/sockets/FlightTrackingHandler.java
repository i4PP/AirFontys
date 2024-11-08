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

        for (WebSocketSession session : sessions) {
            String flightNumber = (String) session.getAttributes().get("flightNumber");
            String date = (String) session.getAttributes().get("date");

            if (flightNumber == null || date == null) {
                continue;
            }

            try {
                FlightTrackingResponse response = flightService.getFlightTracking(flightNumber, date)
                        .orElseThrow(() -> new SocketException("Flight not found for " + flightNumber));

                String responseJson = mapper.writeValueAsString(response);
                session.sendMessage(new TextMessage(responseJson));

            } catch (SocketException e) {
                sendErrorMessage(session, "Flight not found");
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
