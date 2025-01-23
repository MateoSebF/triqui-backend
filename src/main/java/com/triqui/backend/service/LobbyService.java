package com.triqui.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

import com.triqui.backend.model.GameSession;
import com.triqui.backend.model.Lobby;

@Service
public class LobbyService {

    @Autowired
    private Lobby lobby;

    public GameSession getAGameSession(String gameName) {
        return lobby.getGameSession(gameName);
    }

    public List<GameSession> getActiveGames(int page, int size) {
        return lobby.getActiveGames(page, size);
    }

    public void createGame(String gameName, String password) throws IllegalArgumentException {
        // Create a new game
        lobby.createNewGame(gameName, password);
    }

    public void removeGame(String gameName) throws IllegalArgumentException {
        lobby.removeGame(gameName);
    }

    public void addSessionToGame(String gameName, WebSocketSession session, String player)
            throws IllegalArgumentException {
        lobby.addSessionToGame(gameName, session, player);
    }

    public void removeSessionFromGame(String gameName, WebSocketSession session) throws IllegalArgumentException {
        lobby.removeSessionFromGame(gameName, session);
    }

    public boolean makeMoveInAGame(String gameName, String player, int row, int col) {
        return lobby.makeMoveInAGame(gameName, player, row, col);
    }

    public List<WebSocketSession> getSessionsInAGame(String gameName) {
        return lobby.getSessionsInAGame(gameName);
    }

    public String getGameMessage(String gameName) {
        return lobby.getGameMessage(gameName);
    }
}
