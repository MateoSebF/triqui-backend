package com.triqui.backend.websocket;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.Map;

import com.triqui.backend.model.Game;
import com.triqui.backend.session.GameSession;

import java.util.HashMap;


public class GameWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, GameSession> activeGames = new HashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // Here you will handle messages (game moves, player turns, etc.)
        System.out.println("Received message: " + message.getPayload());
        String payloadId = message.getPayload();
        // Extract gameId from URI path
        String path = session.getUri().getPath();  // Example: /game/{gameId}
        String gameId = path.split("/")[2];  // Split and get the gameId
        GameSession gameSession = activeGames.get(gameId);
        Game game = gameSession.getGame();

        if (game != null) {
            String player = extractAttributeString(payloadId, "player");
            if (!player.equals(game.getCurrentPlayer())) {
                return;
            }
            int row = Integer.parseInt(extractAttributeString(payloadId, "row"));
            int col = Integer.parseInt(extractAttributeString(payloadId, "col"));
            if (game.makeMove(row, col)){
                broadcastGameState(gameId, gameSession);
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Received new connection");
        // Extract gameId from URI path
        String path = session.getUri().getPath();  // Example: /game/{gameId}
        String gameId = path.split("/")[2];  // Split and get the gameId
        
        if (gameId == null) {
            System.out.println("Missing gameId in the connection request.");
            session.close();
            return;
        }
        GameSession gameSession = activeGames.computeIfAbsent(gameId, k-> new GameSession(new Game(), new HashMap<>()));
        String availablePlayer = gameSession.getAvailablePlayer();
        if (availablePlayer == null) {
            System.out.println("Game is full");
            session.close();
            return;
        }
        gameSession.addSession(session, availablePlayer);
        activeGames.put(gameId, gameSession);
        session.sendMessage(new TextMessage("{\"player\": \"" + availablePlayer + "\"}"));
        System.out.println("WebSocket connection established: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Error occurred on WebSocket connection: " + session.getId());
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        // Extract gameId from URI path
        String path = session.getUri().getPath();  // Example: /game/{gameId}
        String gameId = path.split("/")[2];  // Split and get the gameId
        if (gameId == null) {
            System.out.println("Missing gameId in the connection request.");
            session.close();
            return;
        }

        GameSession gameSession = activeGames.get(gameId);
        if (gameSession != null) {
            gameSession.removeSession(session);
            if (gameSession.getSessions().isEmpty()) {
                activeGames.remove(gameId);
            }
        }
        
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    private String extractAttributeString(String payload, String attribute) {
        payload = payload.substring(1, payload.length() - 1);
        String[] parts = payload.split(",");
        for (String part : parts) {
            //Clear the withe spaces and quotes
            part = part.replaceAll("\\s", "");
            part = part.replaceAll("\"", "");
            String[] keyValue = part.split(":");
            if (keyValue[0].equals(attribute)) {
                return keyValue[1];
            }
        }
        return null;
    }

    private void broadcastGameState(String gameId, GameSession gameSession) {
        Game game = gameSession.getGame();
        String message = convertGameToMessage(game);
        HashMap<String, WebSocketSession> sessions = gameSession.getSessions();

        for (String player : sessions.keySet()) {
            WebSocketSession session = sessions.get(player);
            if (session != null) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String convertGameToMessage(Game game) {
        StringBuilder message = new StringBuilder();
        message.append("{");
        message.append("\"board\": [");
        for (int i = 0; i < 3; i++) {
            message.append("[");
            for (int j = 0; j < 3; j++) {
                message.append("\"" + game.getBoard()[i][j] + "\"");
                if (j < 2) {
                    message.append(",");
                }
            }
            message.append("]");
            if (i < 2) {
                message.append(",");
            }
        }
        message.append("],");
        message.append("\"gameEnded\": " + game.isGameEnded());
        message.append("}");
        return message.toString();
    }

}
