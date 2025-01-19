package me.abdul.flightapi.configs;


import me.abdul.flightapi.sockets.FlightCountHandler;
import me.abdul.flightapi.sockets.FlightTrackingHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final FlightCountHandler flightCountHandler;
    private final FlightTrackingHandler FlightTrackingHandler ;

    public WebSocketConfig(FlightCountHandler flightCountHandler, FlightTrackingHandler flightTrackingHandler) {
        this.flightCountHandler = flightCountHandler;
        FlightTrackingHandler = flightTrackingHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(flightCountHandler, "/ws/flights")
                .setAllowedOrigins("*");
        registry.addHandler(FlightTrackingHandler, "/ws/flight-tracking")
                .setAllowedOrigins("*");
    }

}
