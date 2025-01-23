package com.triqui.backend.model;

import org.springframework.web.socket.WebSocketSession;

import com.triqui.backend.model.DTO.GameSessionDTO;

import java.util.HashMap;

import lombok.Getter;

@Getter
public class GameSession {
    private String gameName;
    private String password;
    private Game game;
    private HashMap<String, WebSocketSession> sessions;

    public GameSession(String gameName, String password, Game game, HashMap<String, WebSocketSession> sessions) {
        this.gameName = gameName;
        this.password = password;
        this.game = game;
        this.sessions = sessions;
        this.sessions.put("X", null);
        this.sessions.put("O", null);
    }

    public void addSession(WebSocketSession session, String player) {
        sessions.put(player, session);
    }

    public void removeSession(WebSocketSession session) throws IllegalArgumentException {
        for (String player : sessions.keySet()) {
            if (sessions.get(player) == session) {
                sessions.put(player, null);
                return;
            }
        }
        throw new IllegalArgumentException("Session not found");
    }

    public String getAvailablePlayer() {
        for (String player : sessions.keySet()) {
            if (sessions.get(player) == null) {
                return player;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return sessions.get("X") == null && sessions.get("O") == null;
    }

    public GameSessionDTO toDTO() {
        return new GameSessionDTO(gameName, password, getAvailablePlayer());
    }
}
