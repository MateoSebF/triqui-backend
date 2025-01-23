package com.triqui.backend.controller;

import java.util.List;
import com.triqui.backend.model.GameSession;
import com.triqui.backend.model.DTO.GameSessionDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;

import com.triqui.backend.service.LobbyService;

import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("/lobby")
@Getter
@Setter
public class LobbyController {
    
    @Autowired
    private LobbyService lobbyService;

    @GetMapping("/active")
    public ResponseEntity<List<GameSessionDTO>> getActiveGames(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        // Get a list of active games
        //Pageable pageable = Pageable.ofSize(size).withPage(page);
        int pageNumber = page;
        int pageSize = size;
        List<GameSessionDTO> activeGames = lobbyService.getActiveGames(pageNumber, pageSize)
                                        .stream()
                                        .map(GameSession::toDTO).toList();
        return ResponseEntity.ok(activeGames);
    }

    @PostMapping("/create")
    public ResponseEntity<Integer> createGame(@RequestBody GameSessionDTO gameSessionDTO) {
        // Create a new game
        String gameName = gameSessionDTO.getGameName();
        String password = gameSessionDTO.getPassword();
        try {
            lobbyService.createGame(gameName, password);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Integer> removeGame(@RequestBody String gameName) {
        // Remove a game
        try {
            lobbyService.removeGame(gameName);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
