package com.ynov.kiwi.api.service;

import com.ynov.kiwi.api.entity.Player;
import com.ynov.kiwi.api.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class PlayerService {
    private final PlayerRepository repo;
    private final MqttService mqttService;
    private volatile Integer playerInControl = null;
    private volatile long inControlSince = 0;
    private volatile boolean gameStarted = false;
    private final Integer controlTime = 10000;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired // Peut-être omis car 1 seul constructeur...
    public PlayerService(PlayerRepository repo, MqttService mqttService) {
        this.repo = repo;
        this.mqttService = mqttService;
    }

    public boolean addPlayer(int id) {
        if (gameStarted || repo.existsById(id)) return false;
        repo.save(new Player(id));
        return true;
    }
    public Optional<Player> getPlayer(int id) { return repo.findById(id); }
    public Collection<Player> getPlayers() { return repo.findAll(); }
    public void addPoint(int id) { getPlayer(id).ifPresent(Player::addPoint); }

    public boolean processBuzz(int playerId) {
        Player player = getPlayer(playerId).orElse(null);
        if (!gameStarted || player == null) return false;
        if (player.hasAttempted() || !player.isEnabled()) return false; // déjà tenté ou buzzer désactivé

        // Désactive tous les buzzers
        for (Player p : repo.findAll()) {
            p.setEnabled(false);
            mqttService.publishDisable(p.getId());
        }
        playerInControl = playerId;
        inControlSince = System.currentTimeMillis();

        // Le joueur a répondu :
        player.setHasAttempted(true);

        scheduler.schedule(() -> endTurn(), controlTime, TimeUnit.MILLISECONDS);
        return true;
    }

    private void endTurn() {
        // On réactive seulement les joueurs qui n'ont pas encore tenté
        for (Player p : repo.findAll()) {
            if (!p.hasAttempted()) {
                p.setEnabled(true);
                mqttService.publishEnable(p.getId());
            }
        }
        playerInControl = null;
        inControlSince = 0;
    }

    public void resetBuzzers() {
        for (Player p : repo.findAll()) {
            p.setHasAttempted(false);
            p.setEnabled(true);
            mqttService.publishEnable(p.getId());
        }
        playerInControl = null;
        inControlSince = 0;
    }

    public Integer getPlayerInControl() { return playerInControl; }
    public boolean isGameStarted() { return gameStarted; }
    public boolean startGame() {
        if (getPlayers().size() < 2) {
            System.out.println("[Game] Impossible de démarrer : il faut au moins 2 joueurs !");
            return false;
        }
        this.gameStarted = true;
        return true;
    }

    public void stopGame() { this.gameStarted = false; playerInControl = null; resetBuzzers(); }
    public void reset() { stopGame(); repo.clear(); }
}

