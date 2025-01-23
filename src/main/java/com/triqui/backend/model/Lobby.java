package com.triqui.backend.model;

//import org.springframework.data.domain.Pageable;
import org.springframework.web.socket.WebSocketSession;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class Lobby {

    private final Map<String, GameSession> activeGames = new HashMap<>();

    public GameSession getGameSession(String gameName) {
        return activeGames.get(gameName);
    }

    public List<GameSession> getActiveGames(int page, int size) {
        return activeGames.values().stream().skip(page * size).limit(size).toList();
    }

    public void createNewGame(String gameName, String password) throws IllegalArgumentException {
        if (activeGames.containsKey(gameName)) {
            throw new IllegalArgumentException("Game already exists");
        }
        String hashedPassword = hashPassword(password);
        activeGames.put(gameName, new GameSession(gameName, hashedPassword, new Game(), new HashMap<>()));
    }

    public void removeGame(String gameName) throws IllegalArgumentException {
        if (!activeGames.containsKey(gameName))
            throw new IllegalArgumentException("Game does not exist");
        activeGames.remove(gameName);
    }

    public void addSessionToGame(String gameName, WebSocketSession session, String player)
            throws IllegalArgumentException {
        GameSession gameSession = activeGames.get(gameName);
        if (gameSession == null) {
            throw new IllegalArgumentException("Game does not exist");
        }
        gameSession.addSession(session, player);
    }

    public void removeSessionFromGame(String gameName, WebSocketSession session) throws IllegalArgumentException {
        GameSession gameSession = activeGames.get(gameName);
        gameSession.removeSession(session);
        if (gameSession.isEmpty()) {
            removeGame(gameName);
        }
    }

    public boolean makeMoveInAGame(String gameName, String player, int row, int col) {
        GameSession gameSession = activeGames.get(gameName);
        return gameSession.getGame().makeMove(player, row, col);
    }

    public List<WebSocketSession> getSessionsInAGame(String gameName) {
        GameSession gameSession = activeGames.get(gameName);
        return gameSession.getSessions().values().stream().toList();
    }

    public String getGameMessage(String gameName) {
        GameSession gameSession = activeGames.get(gameName);
        return gameSession.getGame().convertToMessage();
    }

    private String hashPassword(String password) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Perform the hashing
            byte[] encodedHash = digest.digest(password.getBytes());

            // Convert byte array into a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0'); // Padding
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception if SHA-256 algorithm is not supported
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
