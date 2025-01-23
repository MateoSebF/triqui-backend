package com.triqui.backend.model.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSessionDTO {
    private String gameName;
    private String password;
    private String player;

    public GameSessionDTO(String gameName, String password, String player) {
        this.gameName = gameName;
        this.password = password;
        this.player = player;
    }
    
}
