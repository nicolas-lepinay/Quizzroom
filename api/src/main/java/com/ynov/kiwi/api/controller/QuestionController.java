package com.ynov.kiwi.api.controller;

import com.ynov.kiwi.api.entity.Question;
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
    public Collection<Question> getAll() { return service.getQuestions(); }

    @PostMapping
    public Question add(@RequestBody Question question) { return service.addQuestion(question); }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getOne(@PathVariable int id) {
        Optional<Question> q = service.getQuestion(id);
        return q.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
