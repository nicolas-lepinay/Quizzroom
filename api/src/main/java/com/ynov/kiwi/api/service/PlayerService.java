package com.ynov.kiwi.api.service;

import com.ynov.kiwi.api.entity.Player;
import com.ynov.kiwi.api.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class PlayerService {
    private final PlayerRepository repo;
    private volatile Integer playerInControl = null;
    private volatile long inControlSince = 0;
    private volatile boolean gameStarted = false;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public PlayerService(PlayerRepository repo) { this.repo = repo; }

    public boolean addPlayer(int id) {
        if (gameStarted || repo.existsById(id)) return false;
        repo.save(new Player(id));
        return true;
    }
    public Optional<Player> getPlayer(int id) { return repo.findById(id); }
    public Collection<Player> getPlayers() { return repo.findAll(); }
    public void addPoint(int id) { getPlayer(id).ifPresent(Player::addPoint); }

    public boolean processBuzz(int playerId) {
        if (!gameStarted) return false;
        if (playerInControl != null && System.currentTimeMillis() - inControlSince < 5000)
            return false;
        if (!repo.existsById(playerId)) return false;
        playerInControl = playerId;
        inControlSince = System.currentTimeMillis();
        repo.findAll().forEach(p -> p.setEnabled(p.getId() == playerId));
        scheduler.schedule(this::resetBuzzers, 5, TimeUnit.SECONDS);
        return true;
    }
    private void resetBuzzers() {
        playerInControl = null;
        inControlSince = 0;
        repo.findAll().forEach(p -> p.setEnabled(true));
    }

    public Integer getPlayerInControl() { return playerInControl; }
    public boolean isGameStarted() { return gameStarted; }
    public void startGame() { this.gameStarted = true; }
    public void stopGame() { this.gameStarted = false; playerInControl = null; resetBuzzers(); }
    public void reset() { stopGame(); repo.clear(); }
}

