package com.triqui.backend.session;

import org.springframework.web.socket.WebSocketSession;
import java.util.List;

import com.triqui.backend.model.Game;
import lombok.Getter;


@Getter
public class GameSession {
    private Game game;
    private List<WebSocketSession> sessions;

    public GameSession(Game game, List<WebSocketSession> sessions) {
        this.game = game;
        this.sessions = sessions;
    }

    public void addSession(WebSocketSession session) {
        sessions.add(session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }
}
