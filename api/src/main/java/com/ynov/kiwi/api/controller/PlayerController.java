package com.ynov.kiwi.api.controller;

import com.ynov.kiwi.api.entity.Player;
import com.ynov.kiwi.api.response.ApiResponse;
import com.ynov.kiwi.api.response.ResponseUtil;
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
    public ResponseEntity<ApiResponse<Collection<Player>>> getAll() {
        return ResponseEntity.ok(ResponseUtil.success("Liste des joueurs récupérée.", service.getPlayers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Player>> getOne(@PathVariable int id) {
        return service.getPlayer(id)
                .map(player -> ResponseEntity.ok(ResponseUtil.success("Joueur trouvé.", player)))
                .orElseGet(() -> ResponseEntity.status(404).body(
                        ResponseUtil.error("Joueur non trouvé.", 404)
                ));
    }

    @PutMapping("/{id}/score")
    public ResponseEntity<ApiResponse<Player>> addPoint(@PathVariable int id) {
        if (service.getPlayer(id).isEmpty())
            return ResponseEntity.status(404).body(ResponseUtil.error("Joueur non trouvé.", 404));
        service.addPoint(id);
        Player updated = service.getPlayer(id).get();
        return ResponseEntity.ok(ResponseUtil.success("Point ajouté au joueur #" + id, updated));
    }

    @GetMapping("/in-control")
    public ResponseEntity<ApiResponse<Object>> playerInControl() {
        Integer id = service.getPlayerInControl();
        if (id == null)
            return ResponseEntity.ok(ResponseUtil.success("Aucun joueur n’a la main.", null));
        return ResponseEntity.ok(ResponseUtil.success("Joueur ayant la main.", id));
    }

    @GetMapping("/enabled")
    public ResponseEntity<ApiResponse<Collection<Player>>> getEnabledPlayers() {
        return ResponseEntity.ok(
                ResponseUtil.success("Joueurs activés récupérés.",
                        service.getPlayers().stream().filter(Player::isEnabled).toList())
        );
    }
}



