package com.example.cctvserver.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.mockito.Mockito.*;

class CctvWebSocketHandlerTest {

    private CctvWebSocketHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CctvWebSocketHandler();
    }

    @Test
    void testConnectionEstablished() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("test-1");
        
        handler.afterConnectionEstablished(session);
        // Method returns void and only prints/adds to list, we can test it handles sessions properly via handleTextMessage
    }

    @Test
    void testHandleTextMessage() throws Exception {
        WebSocketSession session1 = mock(WebSocketSession.class);
        when(session1.getId()).thenReturn("test-1");
        when(session1.isOpen()).thenReturn(true);
        
        WebSocketSession session2 = mock(WebSocketSession.class);
        when(session2.getId()).thenReturn("test-2");
        when(session2.isOpen()).thenReturn(true);

        handler.afterConnectionEstablished(session1);
        handler.afterConnectionEstablished(session2);

        TextMessage message = new TextMessage("Hello Server");
        handler.handleTextMessage(session1, message);

        verify(session2, times(1)).sendMessage(any(TextMessage.class));
        verify(session1, never()).sendMessage(any(TextMessage.class));
    }

    @Test
    void testHandleTextMessage_IOException() throws Exception {
        WebSocketSession session1 = mock(WebSocketSession.class);
        when(session1.getId()).thenReturn("test-1");
        when(session1.isOpen()).thenReturn(true);
        
        WebSocketSession session2 = mock(WebSocketSession.class);
        when(session2.getId()).thenReturn("test-2");
        when(session2.isOpen()).thenReturn(true);
        
        // Throw an exception when trying to broadcast to session2
        doThrow(new IOException("test exception")).when(session2).sendMessage(any(TextMessage.class));

        handler.afterConnectionEstablished(session1);
        handler.afterConnectionEstablished(session2);

        TextMessage message = new TextMessage("Hello Server");
        handler.handleTextMessage(session1, message);
        // It should catch the exception silently (printing to stderr)
        verify(session2, times(1)).sendMessage(any(TextMessage.class));
    }

    @Test
    void testHandleTextMessage_SessionClosed() throws Exception {
        WebSocketSession session1 = mock(WebSocketSession.class);
        when(session1.getId()).thenReturn("test-1");
        when(session1.isOpen()).thenReturn(true);
        
        WebSocketSession session2 = mock(WebSocketSession.class);
        when(session2.getId()).thenReturn("test-2");
        when(session2.isOpen()).thenReturn(false); // Session is closed

        handler.afterConnectionEstablished(session1);
        handler.afterConnectionEstablished(session2);

        TextMessage message = new TextMessage("Hello Server");
        handler.handleTextMessage(session1, message);
        
        verify(session2, never()).sendMessage(any(TextMessage.class));
    }

    @Test
    void testAfterConnectionClosed() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("test-1");
        
        handler.afterConnectionEstablished(session);
        handler.afterConnectionClosed(session, CloseStatus.NORMAL);
        
        // Test that if we try to send a message now from another session, it won't broadcast to closed/removed session
        WebSocketSession session2 = mock(WebSocketSession.class);
        when(session2.getId()).thenReturn("test-2");
        when(session2.isOpen()).thenReturn(true);
        
        handler.afterConnectionEstablished(session2);
        handler.handleTextMessage(session2, new TextMessage("Hello"));
        verify(session, never()).sendMessage(any(TextMessage.class));
    }
}
