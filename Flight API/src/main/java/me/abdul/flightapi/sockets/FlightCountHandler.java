package me.abdul.flightapi.sockets;

import me.abdul.flightapi.customExceptions.SocketException;
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
public class FlightCountHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final FlightService flightService;

    public FlightCountHandler(FlightService flightService){
        this.flightService = flightService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        sendFlightCount(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    @Scheduled(fixedRate = 1000)
    public void broadcastFlightCount() {
        if (sessions.isEmpty()) {
            return;
        }
        int activeFlights = flightService.getActiveFlightsCount();
        TextMessage message = new TextMessage(String.valueOf(activeFlights));
        sessions.forEach(session -> {
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                throw new SocketException("Error sending message to Update client", e);
            }
        });
    }

    private void sendFlightCount(WebSocketSession session) {
        int activeFlights = flightService.getActiveFlightsCount();

        try {
            session.sendMessage(new TextMessage(String.valueOf(activeFlights)));
        } catch (IOException e) {
            throw new SocketException("Error sending message to new client", e);
        }
    }


}
