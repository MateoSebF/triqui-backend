package com.triqui.backend.session;

import org.springframework.web.socket.WebSocketSession;
import java.util.HashMap;

import com.triqui.backend.model.Game;
import lombok.Getter;


@Getter
public class GameSession {
    private Game game;
    private HashMap<String, WebSocketSession> sessions;

    public GameSession(Game game, HashMap<String, WebSocketSession> sessions) {
        this.game = game;
        this.sessions = sessions;
        sessions.put("X", null);
        sessions.put("O", null);
    }

    public void addSession(WebSocketSession session, String player) {
        sessions.put(player, session);
    }

    public void removeSession(WebSocketSession session) {
        for (String player : sessions.keySet()) {
            if (sessions.get(player) == session) {
                sessions.put(player, null);
            }
        }
    }

    public String getAvailablePlayer() {
        for (String player : sessions.keySet()) {
            if (sessions.get(player) == null) {
                return player;
            }
        }
        return null;
    }
}
