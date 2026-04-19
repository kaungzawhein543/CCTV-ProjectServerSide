package com.example.cctvserver.config;

import com.example.cctvserver.handler.CctvWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CctvWebSocketHandler cctvWebSocketHandler;

    public WebSocketConfig(CctvWebSocketHandler cctvWebSocketHandler) {
        this.cctvWebSocketHandler = cctvWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Register the endpoint at /cctv-control
        // setAllowedOrigins("*") is used for broad access, adjust for production if needed.
        registry.addHandler(cctvWebSocketHandler, "/cctv-control").setAllowedOrigins("*");
    }
}
