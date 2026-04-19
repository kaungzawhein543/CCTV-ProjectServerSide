package com.example.cctvserver.config;

import com.example.cctvserver.handler.CctvWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistration;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import static org.mockito.Mockito.*;

class WebSocketConfigTest {

    @Test
    void testRegisterWebSocketHandlers() {
        CctvWebSocketHandler handler = mock(CctvWebSocketHandler.class);
        WebSocketConfig config = new WebSocketConfig(handler);
        
        WebSocketHandlerRegistry registry = mock(WebSocketHandlerRegistry.class);
        WebSocketHandlerRegistration registration = mock(WebSocketHandlerRegistration.class);
        
        when(registry.addHandler(handler, "/cctv-control")).thenReturn(registration);
        when(registration.setAllowedOrigins("*")).thenReturn(registration);
        
        config.registerWebSocketHandlers(registry);
        
        verify(registry).addHandler(handler, "/cctv-control");
        verify(registration).setAllowedOrigins("*");
    }
}
