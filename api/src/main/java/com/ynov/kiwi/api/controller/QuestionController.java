package com.ynov.kiwi.api.controller;

import com.ynov.kiwi.api.entity.Question;
import com.ynov.kiwi.api.response.ApiResponse;
import com.ynov.kiwi.api.response.ResponseUtil;
import com.ynov.kiwi.api.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService service;
    public QuestionController(QuestionService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<ApiResponse<Collection<Question>>> getAll() {
        return ResponseEntity.ok(ResponseUtil.success("Liste des questions récupérée.", service.getQuestions()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Question>> add(@RequestBody Question question) {
        Question q = service.addQuestion(question);
        return ResponseEntity.ok(ResponseUtil.success("Question ajoutée.", q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Question>> getOne(@PathVariable int id) {
        Optional<Question> q = service.getQuestion(id);
        return q
                .map(question -> ResponseEntity.ok(ResponseUtil.success("Question trouvée.", question)))
                .orElseGet(() -> ResponseEntity.status(404).body(
                        ResponseUtil.error("Question non trouvée.", 404)
                ));
    }
}

