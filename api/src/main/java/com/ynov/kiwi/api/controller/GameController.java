package com.ynov.kiwi.api.controller;
import com.ynov.kiwi.api.response.ApiResponse;
import com.ynov.kiwi.api.response.ResponseUtil;
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
    public ResponseEntity<ApiResponse<String>> getGameState() {
        String state = playerService.isGameStarted() ? "en cours" : "en attente";
        return ResponseEntity.ok(ResponseUtil.success("Etat de la partie récupéré.", state));
    }

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<String>> startGame() {
        boolean started = playerService.startGame();
        if (!started) {
            return ResponseEntity.badRequest().body(
                    ResponseUtil.error("Il faut au moins 2 joueurs pour démarrer la partie.", 400)
            );
        }
        return ResponseEntity.ok(ResponseUtil.success("La partie commence !", null));
    }

    @PostMapping("/stop")
    public ResponseEntity<ApiResponse<String>> stopGame() {
        playerService.stopGame();
        return ResponseEntity.ok(ResponseUtil.success("Partie arrêtée.", null));
    }

    @PutMapping("/question/{id}")
    public ResponseEntity<ApiResponse<String>> setCurrentQuestion(@PathVariable int id) {
        gameService.setCurrentQuestion(id);
        return ResponseEntity.ok(ResponseUtil.success("Question actuelle changée.", String.valueOf(id)));
    }

    @GetMapping("/question")
    public ResponseEntity<ApiResponse<Integer>> getCurrentQuestion() {
        return ResponseEntity.ok(ResponseUtil.success("Question actuelle récupérée.", gameService.getCurrentQuestion()));
    }
}

