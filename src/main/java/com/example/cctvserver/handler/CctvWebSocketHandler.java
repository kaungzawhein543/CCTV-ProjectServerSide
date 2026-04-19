package com.example.cctvserver.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class CctvWebSocketHandler extends TextWebSocketHandler {

    // Store all active sessions using thread-safe set
    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("New device connected! Session ID: " + session.getId() + " | Total Connections: " + sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received message from " + session.getId() + ": " + payload);

        // Broadcast the message to all OTHER connected clients
        for (WebSocketSession s : sessions) {
            if (s.isOpen() && !s.getId().equals(session.getId())) {
                try {
                    s.sendMessage(new TextMessage(payload));
                } catch (IOException e) {
                    System.err.println("Error sending message to " + s.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Device disconnected. Session ID: " + session.getId() + " | Remaining Connections: " + sessions.size());
    }
}
