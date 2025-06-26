package com.ynov.kiwi.api.controller;
import com.ynov.kiwi.api.entity.Question;
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
        String state = playerService.isGameStarted() ? "started" : "waiting";
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

    @GetMapping("/current-question")
    public ResponseEntity<ApiResponse<Question>> getCurrentQuestion() {
        Question current = gameService.getCurrentQuestion();
        if (current == null)
            return ResponseEntity.status(404).body(ResponseUtil.error("Aucune question disponible.", 404));
        return ResponseEntity.ok(ResponseUtil.success("Question actuelle récupérée.", current));
    }

    @PostMapping("/next-question")
    public ResponseEntity<ApiResponse<Question>> nextQuestion() {
        boolean ok = gameService.nextQuestion();
        playerService.resetBuzzers();
        if (!ok)
            return ResponseEntity.badRequest().body(ResponseUtil.error("Fin de la liste des questions.", 400));
        return ResponseEntity.ok(ResponseUtil.success("Nouvelle question, buzzers réinitialisés.", gameService.getCurrentQuestion()));
    }

    @PostMapping("/reset-questions")
    public ResponseEntity<ApiResponse<Question>> resetQuestions() {
        gameService.resetQuestions();
        playerService.resetBuzzers();
        return ResponseEntity.ok(ResponseUtil.success("Première question sélectionnée.", gameService.getCurrentQuestion()));
    }
}

