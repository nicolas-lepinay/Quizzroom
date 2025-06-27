package com.ynov.kiwi.api.service;

import com.ynov.kiwi.api.controller.SseController;
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
    //private final MqttService mqttService;
    private final SseController sseController;

    private volatile boolean gameStarted = false;
    private final Integer answerTime = 7000;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public PlayerService(
            PlayerRepository repo,
            //MqttService mqttService,
            SseController sseController
    ) {
        this.repo = repo;
        //this.mqttService = mqttService;
        this.sseController = sseController;
    }

    public boolean addPlayer(int id) {
        if (gameStarted || repo.existsById(id)) return false;
        repo.save(new Player(id));
        sseController.sendPlayerUpdate(); // Sends update to front-end
        return true;
    }
    public Optional<Player> getPlayer(int id) { return repo.findById(id); }
    public Collection<Player> getPlayers() { return repo.findAll(); }
    public void addPoint(int id) {
        getPlayer(id).ifPresent(Player::addPoint);
        sseController.sendPlayerUpdate(); // Sends update to front-end
    }

    public boolean processBuzz(int playerId) {
        Player player = getPlayer(playerId).orElse(null);
        if (!gameStarted || player == null) return false;
        if (!player.isEnabled()) return false;

        // Désactive tous les buzzers
        for (Player p : repo.findAll()) {
            p.setEnabled(false);
            //mqttService.publishDisable(p.getId());
            sseController.sendDisable(p.getId());
        }
        // Le joueur a répondu :
        player.setInControl(true);
        //player.setHasAttempted(true);
        sseController.sendPlayerUpdate(); // Sends update to front-end

        scheduler.schedule(() -> endTurn(player.getId()), answerTime, TimeUnit.MILLISECONDS);
        return true;
    }

    private void endTurn(int playerId) {
        // On réactive seulement les autres joueurs que PlayerId
        for (Player p : repo.findAll()) {
            p.setInControl(false);
            if (p.getId() != playerId) {
                p.setEnabled(true);
                //mqttService.publishEnable(p.getId());
                sseController.sendEnable(p.getId());
            }
        }
        sseController.sendPlayerUpdate(); // Sends update to front-end
    }

    public void resetBuzzers() {
        for (Player p : repo.findAll()) {
            //p.setHasAttempted(false);
            p.setEnabled(true);
            p.setInControl(false);
            //mqttService.publishEnable(p.getId());
            sseController.sendEnable(p.getId());
        }
        sseController.sendPlayerUpdate(); // Sends update to front-end
    }

    public boolean isGameStarted() {
        return gameStarted;
    }
    public boolean startGame() {
        if (getPlayers().size() < 2) {
            System.out.println("[Game] Impossible de démarrer : il faut au moins 2 joueurs !");
            return false;
        }
        this.gameStarted = true;
        return true;
    }

    public void stopGame() {
        this.gameStarted = false;
        repo.clear();
        sseController.sendResetEvent();
        System.out.println("[Game] Partie arrêtée.");
    }

    public Integer getAnswerTime() {
        return answerTime;
    }
}

