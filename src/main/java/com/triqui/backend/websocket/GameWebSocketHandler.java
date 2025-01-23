package com.triqui.backend.websocket;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import com.triqui.backend.service.LobbyService;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private LobbyService lobbyService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Received new connection");
        // Extract gameName from URI path
        String path = session.getUri().getPath(); // Example: /game/{gameName}
        String gameName = path.split("/")[2]; // Split and get the gameName

        if (gameName == null) {
            System.out.println("Missing gameName in the connection request.");
            session.close();
            return;
        }
        String availablePlayer = lobbyService.getAGameSession(gameName).getAvailablePlayer();
        if (availablePlayer == null) {
            System.out.println("Game is full");
            session.close();
            return;
        }
        try {
            lobbyService.addSessionToGame(gameName, session, availablePlayer);
            session.sendMessage(new TextMessage("{\"player\": \"" + availablePlayer + "\"}"));
            System.out.println("WebSocket connection established: " + session.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("Game does not exist");
            session.close();
            return;
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Error occurred on WebSocket connection: " + session.getId());
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status)
            throws Exception {
        // Extract gameName from URI path
        String path = session.getUri().getPath(); // Example: /game/{gameName}
        String gameName = path.split("/")[2]; // Split and get the gameName
        if (gameName == null) {
            System.out.println("Missing gameName in the connection request.");
            session.close();
            return;
        }

        try {
            lobbyService.removeSessionFromGame(gameName, session);
        } catch (IllegalArgumentException e) {
            System.out.println("Game does not exist");
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // Here you will handle messages (game moves, player turns, etc.)
        System.out.println("Received message: " + message.getPayload());
        String payloadId = message.getPayload();
        // Extract gameName from URI path
        String path = session.getUri().getPath(); // /game/{gameName}
        String gameName = path.split("/")[2]; // Split and get the gameName
        if (gameName == null) {
            System.out.println("Missing gameName in the connection request.");
            session.close();
            return;
        }

        String player = extractAttributeString(payloadId, "player");
        int row = Integer.parseInt(extractAttributeString(payloadId, "row"));
        int col = Integer.parseInt(extractAttributeString(payloadId, "col"));
        if (lobbyService.makeMoveInAGame(gameName, player, row, col)) {
            broadcastGameState(gameName);
        }

    }

    private String extractAttributeString(String payload, String attribute) {
        payload = payload.substring(1, payload.length() - 1);
        String[] parts = payload.split(",");
        for (String part : parts) {
            // Clear the withe spaces and quotes
            part = part.replaceAll("\\s", "");
            part = part.replaceAll("\"", "");
            String[] keyValue = part.split(":");
            if (keyValue[0].equals(attribute)) {
                return keyValue[1];
            }
        }
        return null;
    }

    private void broadcastGameState(String gameName) {
        List<WebSocketSession> sessions = lobbyService.getSessionsInAGame(gameName);
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage("{\"game\": " + lobbyService.getGameMessage(gameName) + "}"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
