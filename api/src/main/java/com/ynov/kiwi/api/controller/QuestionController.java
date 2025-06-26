package com.ynov.kiwi.api.controller;

import com.ynov.kiwi.api.entity.Question;
import com.ynov.kiwi.api.response.ApiResponse;
import com.ynov.kiwi.api.response.ResponseUtil;
import com.ynov.kiwi.api.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Question>>> getAll() {
        return ResponseEntity.ok(ResponseUtil.success("Liste des questions récupérée.", questionService.getQuestions()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Question>> getOne(@PathVariable int id) {
        return questionService.getQuestionById(id)
                .map(q -> ResponseEntity.ok(ResponseUtil.success("Question trouvée.", q)))
                .orElseGet(() -> ResponseEntity.status(404).body(ResponseUtil.error("Question non trouvée.", 404)));
    }
}
