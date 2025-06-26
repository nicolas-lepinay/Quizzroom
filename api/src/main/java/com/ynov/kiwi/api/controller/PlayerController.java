package com.ynov.kiwi.api.controller;

import com.ynov.kiwi.api.entity.Player;
import com.ynov.kiwi.api.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/players")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) { this.playerService = playerService; }

    @GetMapping
    public Collection<Player> listPlayers() { return playerService.getPlayers(); }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable int id) {
        return playerService.getPlayer(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

}

