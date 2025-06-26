package com.ynov.kiwi.api.controller;

import com.ynov.kiwi.api.entity.Player;
import com.ynov.kiwi.api.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;

@RestController
@RequestMapping("/players")
public class PlayerController {
    private final PlayerService service;
    public PlayerController(PlayerService service) { this.service = service; }

    @GetMapping
    public Collection<Player> getAll() { return service.getPlayers(); }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getOne(@PathVariable int id) {
        return service.getPlayer(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/score")
    public ResponseEntity<Player> addPoint(@PathVariable int id) {
        service.addPoint(id);
        return getOne(id);
    }

    @GetMapping("/in-control")
    public Integer playerInControl() { return service.getPlayerInControl(); }

    @GetMapping("/enabled")
    public Collection<Player> getEnabledPlayers() {
        return service.getPlayers().stream().filter(Player::isEnabled).toList();
    }
}


