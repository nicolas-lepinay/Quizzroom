package com.ynov.kiwi.api.controller;
import com.ynov.kiwi.api.service.GameService;
import com.ynov.kiwi.api.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;
    private final PlayerService playerService;

    public GameController(GameService gameService, PlayerService playerService) {
        this.gameService = gameService;
        this.playerService = playerService;
    }

    @GetMapping("/state")
    public boolean getGameState() { return playerService.isGameStarted(); }

    @PostMapping("/start")
    public ResponseEntity<?> startGame() {
        boolean ok = playerService.startGame();
        if (!ok) {
            return ResponseEntity.badRequest().body("Il faut au moins 2 joueurs pour démarrer la partie.");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/stop")
    public void stopGame() { playerService.stopGame(); }

    @PutMapping("/question/{id}")
    public void setCurrentQuestion(@PathVariable int id) { gameService.setCurrentQuestion(id); }

    @GetMapping("/question")
    public int getCurrentQuestion() { return gameService.getCurrentQuestion(); }
}
